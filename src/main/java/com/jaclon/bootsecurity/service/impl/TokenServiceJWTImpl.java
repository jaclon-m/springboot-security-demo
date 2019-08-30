package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.dto.Token;
import com.jaclon.bootsecurity.service.SysLogService;
import com.jaclon.bootsecurity.service.TokenService;
import io.jsonwebtoken.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token 存到Redis的实现
 * jwt实现的token
 * @author jaclon
 * @date 2019/8/6
 * @time 10:48
 */
@Primary
@Service
public class TokenServiceJWTImpl implements TokenService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");
    /**
     * token过期秒数
     */
    @Value("${token.expire.seconds}")
    private Integer expireSeconds;
    @Autowired
    private RedisTemplate<String,LoginUser> redisTemplate;
    @Autowired
    private SysLogService logSevice;
    /**
     * 私钥
     */
    @Value("${token.jwtSecret}")
    private String jwtSecret;

    private static Key KEY = null;
    private static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";

    /**
     * 登录成功后，将UUID设置到loginUser的token字段，并生成一个jwtToken
     * @param loginUser
     * @return
     */
    @Override
    public Token saveToken(LoginUser loginUser) {
        loginUser.setToken(UUID.randomUUID().toString());
        cacheLoginUser(loginUser);
        //登录日志
        logSevice.save(loginUser.getId(),"登录",true,null);
        String JWTToken = createJWTToken(loginUser);
        return new Token(JWTToken,loginUser.getLoginTime());
    }

    /**
     * 创建JWTToken
     * @param loginUser
     * @return
     */
    private String createJWTToken(LoginUser loginUser) {
        //创建payload私有声明
        Map<String,Object> claims = new HashMap <>(4);
        //放入一个随机字符串，通过该字符串找到用户
        claims.put(LOGIN_USER_KEY,loginUser.getToken());
        //new一个JwtBuilder，设置jwt的body
        String jwtToken = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，
                // 一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(SignatureAlgorithm.HS256,getKeyInstance())
                //执行压缩
                .compact();
        return jwtToken;
    }

    /**
     * 解析JWTToken
     * @param jwtToken
     * @return
     */
    private String getUUIDFromJWT(String jwtToken) {
        if("null".equals(jwtToken) || StringUtils.isBlank(jwtToken)){
            return null;
        }
        Map<String,Object> jwtClaims = null;
        try {
            jwtClaims = Jwts.parser()
                    //签名秘钥，和生成的签名的秘钥一模一样
                    .setSigningKey(getKeyInstance())
                    //设置需要解析的jwt
                    .parseClaimsJws(jwtToken)
                    .getBody();
            //生成时的代码claims.put(LOGIN_USER_KEY,loginUser.getToken())
            return MapUtils.getString(jwtClaims,LOGIN_USER_KEY);
        } catch (ExpiredJwtException e) {
            log.error("{}已过期",jwtToken);
        } catch (Exception e) {
            log.error("{}",e);
        }
        return null;
    }

    private Key getKeyInstance(){
        //双重锁
        if(KEY == null){
            synchronized (TokenServiceJWTImpl.class){
                if(KEY == null){
                    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
                    KEY = new SecretKeySpec(apiKeySecretBytes,SignatureAlgorithm.HS256.getJcaName());
                }
            }
        }
        return KEY;
    }

    private void cacheLoginUser(LoginUser loginUser){
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireSeconds*1000);
        //根据uuid将loginUser缓存
        redisTemplate.boundValueOps(getTokenKey(loginUser.getToken())).set(loginUser,expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 更新用户缓存
     * @param loginUser
     */
    @Override
    public void refresh(LoginUser loginUser) {
        cacheLoginUser(loginUser);
    }

    @Override
    public LoginUser getLoginUser(String jwtToken) {
        String uuid = getUUIDFromJWT(jwtToken);
        if(uuid != null){
            return redisTemplate.boundValueOps(getTokenKey(uuid)).get();
        }
        return null;
    }



    @Override
    public boolean deleteToken(String token) {
        String uuid = getUUIDFromJWT(token);
        if(uuid != null){
            String key = getTokenKey(uuid);
            LoginUser loginUser = redisTemplate.opsForValue().get(key);
            if(loginUser != null){
                redisTemplate.delete(key);
                //退出日志
                logSevice.save(loginUser.getId(),"退出",true,null);
                return true;
            }

        }
        return false;
    }

    private String getTokenKey(String uuid) {
        return "tokens:" + uuid;
    }
}
