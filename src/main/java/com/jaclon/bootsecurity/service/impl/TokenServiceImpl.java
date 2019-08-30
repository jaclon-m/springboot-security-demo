package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.dto.Token;
import com.jaclon.bootsecurity.service.SysLogService;
import com.jaclon.bootsecurity.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * token 存到Redis的实现
 * 普通token ，UUID
 *
 * @author jaclon
 * @date 2019/8/14
 * @time 16:03
 */
@Deprecated
@Service
public class TokenServiceImpl implements TokenService {
    /**
     * token过期时间（秒）
     */
    @Value("${token.expire.seconds}")
    private Integer expireSeconds;
    @Autowired
    private RedisTemplate<String,LoginUser> redisTemplate;
    @Autowired
    private SysLogService sysLogSevice;

    @Override
    public Token saveToken(LoginUser loginUser) {
        String token = UUID.randomUUID().toString();
        loginUser.setToken(token);
        cacheLoginUser(loginUser);
        sysLogSevice.save(loginUser.getId(),"登录",true,null);

        return new Token(token,loginUser.getLoginTime());
    }

    private void cacheLoginUser(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireSeconds*1000);
        redisTemplate.boundValueOps(getTokenkey(loginUser.getToken())).set(loginUser,expireSeconds, TimeUnit.SECONDS);
    }

    private String getTokenkey(String token) {
        return "token:" + token;
    }

    @Override
    public void refresh(LoginUser loginUser) {
        cacheLoginUser(loginUser);
    }

    @Override
    public LoginUser getLoginUser(String token) {
        return redisTemplate.boundValueOps(getTokenkey(token)).get();
    }

    @Override
    public boolean deleteToken(String token) {
        String key = getTokenkey(token);
        LoginUser loginUser = redisTemplate.opsForValue().get(key);
        if(loginUser != null){
            redisTemplate.delete(key);
            sysLogSevice.save(loginUser.getId(),"退出",true,null);
            return true;
        }
        return false;
    }
}
