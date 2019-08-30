package com.jaclon.bootsecurity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jaclon.bootsecurity.model.Permission;
import com.jaclon.bootsecurity.model.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jaclon
 * @date 2019/8/6
 * @time 10:23
 */
public class LoginUser extends SysUser implements UserDetails {

    private static final long serialVersionUID = -1379274258881257107L;

    private List<Permission> permissions;
    private String token;
    /** 登陆时间戳（毫秒） */
    private Long loginTime;
    /** 过期时间戳 */
    private Long expireTime;

    public List <Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List <Permission> permissions) {
        this.permissions = permissions;
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

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.parallelStream().filter(p -> !StringUtils.isEmpty(p.getPermission()))
                .map(p -> new SimpleGrantedAuthority(p.getPermission())).collect(Collectors.toSet());
    }

    /**
     * notion ! this method do nothing !
     * @param authorities
     */
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        // do nothing
    }

    /**
     * 检查账户是否过期
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否锁定
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return getStatus() != Status.LOCKED;
    }

    /**
     * 密码是否未过期
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否激活
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
