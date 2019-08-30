package com.jaclon.bootsecurity.dto;

import java.io.Serializable;

/**
 * Restful方式登录token
 * @author jaclon
 * @date 2019/8/6
 * @time 10:20
 */
public class Token implements Serializable {

    private static final long serialVersionUID = 6314027741784310221L;

    private String token;
    /** 登陆时间戳（毫秒） */
    private Long loginTime;

    public Token(String token, Long loginTime) {
        super();
        this.token = token;
        this.loginTime = loginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }
}
