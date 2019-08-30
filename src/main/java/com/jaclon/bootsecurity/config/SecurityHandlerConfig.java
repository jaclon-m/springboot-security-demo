package com.jaclon.bootsecurity.config;

import com.jaclon.bootsecurity.Filter.TokenFilter;
import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.dto.ResponseInfo;
import com.jaclon.bootsecurity.dto.Token;
import com.jaclon.bootsecurity.service.TokenService;
import com.jaclon.bootsecurity.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jaclon
 * @date 2019/8/6
 * @time 10:17
 */
@Configuration
public class SecurityHandlerConfig {

    @Autowired
    private TokenService tokenService;

    /**
     * 登录成功后返回token
     * @return
     */
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return  new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
                LoginUser loginUser = (LoginUser)authentication.getPrincipal();
                Token token = tokenService.saveToken(loginUser);
                ResponseUtil.responseJson(response, HttpStatus.OK.value(),token);
            }
        };
    }

    /**
     * 处理登录失败
     * @return
     */
    @Bean
    public AuthenticationFailureHandler loginFailureHandle(){

        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                String msg = null;
                //密码认证失败
                if(exception instanceof BadCredentialsException){
                    msg="密码错误";
                }else {
                    msg = exception.getMessage();
                }
                ResponseInfo info = new ResponseInfo(HttpStatus.UNAUTHORIZED.value()+"",msg);
                ResponseUtil.responseJson(response,HttpStatus.UNAUTHORIZED.value(),info);
            }
        };
    }

    /**
     * 未登录，返回401
     * @return
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return  new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                ResponseInfo info = new ResponseInfo(HttpStatus.UNAUTHORIZED.value() + "", "请先登录");
                ResponseUtil.responseJson(response,HttpStatus.UNAUTHORIZED.value(),info);
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(){
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                ResponseInfo info = new ResponseInfo(HttpStatus.OK + "", "退出成功");
                String token = TokenFilter.getToken(request);
                tokenService.deleteToken(token);
                ResponseUtil.responseJson(response,HttpStatus.OK.value(),info);
            }
        };
    }
}
