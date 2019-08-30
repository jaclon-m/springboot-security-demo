package com.jaclon.bootsecurity.Filter;

import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.service.TokenService;
import jdk.nashorn.internal.parser.TokenKind;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token 过滤器
 * @author jaclon
 * @date 2019/8/6
 * @time 15:08
 */
@Component
public class TokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_KEY = "token";
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserDetailsService userDetailsService;

    private static final Long MINUTES_10 = 10*60*1000L;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if(!StringUtils.isBlank(token)){
            LoginUser loginUser = tokenService.getLoginUser(token);
            if(loginUser != null){
                loginUser = checkLoginTime(loginUser);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser,
                        null, loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        filterChain.doFilter(request,response);
    }

    /**
     * 校验登录时长
     * 如果当前时间与过期时间差距10分钟以内的话，自动刷新缓存
     * @param loginUser
     * @return
     */
    private LoginUser checkLoginTime(LoginUser loginUser) {
        Long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if(expireTime - currentTime <= MINUTES_10){
            String token = loginUser.getToken();
            //因为是从缓存中取得的用户，所以需要重新从数据库中获取一次，更新成用户最新的状态
            loginUser = (LoginUser)userDetailsService.loadUserByUsername(loginUser.getUsername());
            loginUser.setToken(token);
            tokenService.refresh(loginUser);
        }
        return loginUser;
    }

    /**
     * 从请求中获取token的方法
     * 首先检查请求参数，如果没有的话再检查请求头
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        String token = request.getParameter(TOKEN_KEY);
        if(StringUtils.isEmpty(token)){
            token = request.getHeader(TOKEN_KEY);
        }
        return token;
    }
}
