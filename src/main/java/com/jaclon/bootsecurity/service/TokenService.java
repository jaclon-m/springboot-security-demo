package com.jaclon.bootsecurity.service;

import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.dto.Token;
import com.jaclon.bootsecurity.service.impl.TokenServiceJWTImpl;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

/**
 * Token管理器<br>
 *  可存储到redis或者数据库<br>
 *  具体可看实现类<br>
 *  默认基于redis，实现类为 com.jaclon.bootsecurity.service.impl.TokenServiceJWTImpl<br>
 *  如要换成数据库存储，将TokenServiceImpl类上的注解@Primary挪到com.jaclon.bootsecurity.service.impl.TokenServiceDbImpl
 * @author jaclon
 * @date 2019/8/6
 * @time 10:19
 */
public interface TokenService {

    Token saveToken(LoginUser loginUser);

    void refresh(LoginUser loginUser);

    LoginUser getLoginUser(String token);

    boolean deleteToken(String token);

}
