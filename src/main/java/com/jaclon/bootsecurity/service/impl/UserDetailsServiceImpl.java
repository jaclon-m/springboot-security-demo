package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dao.PermissionDao;
import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.model.Permission;
import com.jaclon.bootsecurity.model.SysUser;
import com.jaclon.bootsecurity.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/6
 * @time 16:30
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionDao permissionDao;

    /**
     * 验证用户登录
     * 同时将用户拥有的权限设置进来
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = userService.getUser(username);
        if(sysUser == null){
            throw new AuthenticationCredentialsNotFoundException("用户名不存在");
        } else if(sysUser.getStatus() == SysUser.Status.LOCKED){
            throw new LockedException("用户被锁定，请联系管理员");
        }else if (sysUser.getStatus() == SysUser.Status.DISABLED){
            throw new DisabledException("用户已作废");
        }

        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser,loginUser);

        List<Permission> permissions = permissionDao.listByUserId(sysUser.getId());
        loginUser.setPermissions(permissions);

        return loginUser;
    }
}
