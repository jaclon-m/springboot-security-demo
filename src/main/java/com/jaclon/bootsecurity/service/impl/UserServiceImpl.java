package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dao.UserDao;
import com.jaclon.bootsecurity.dto.UserDto;
import com.jaclon.bootsecurity.model.SysUser;
import com.jaclon.bootsecurity.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/6
 * @time 0:12
 */
@Service
public class UserServiceImpl implements UserService {

    private static  final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserDao userDao;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public SysUser saveUser(UserDto userDto) {
        SysUser user = userDto;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(SysUser.Status.VALID);
        userDao.save(user);
        saveUserRoles(user.getId(),userDto.getRoleIds());

        log.debug("新增用户{}" + user.getUsername());
        return user;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if(roleIds != null){
            userDao.deleteUserRole(userId);
            if(!CollectionUtils.isEmpty(roleIds)){
                userDao.saveUserRoles(userId,roleIds);
            }
        }
    }

    @Override
    @Transactional
    public SysUser updateUser(UserDto userDto) {
        userDao.update(userDto);
        saveUserRoles(userDto.getId(),userDto.getRoleIds());
        return userDto;
    }

    @Override
    public SysUser getUser(String username) {
        return userDao.getUser(username);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        SysUser user = userDao.getUser(username);
        if(user == null){
            throw new IllegalArgumentException("用户名不存在");
        }
        if(!passwordEncoder.matches(oldPassword,user.getPassword())){
            throw new IllegalArgumentException("旧密码错误");
        }
        userDao.changePassword(user.getId(),passwordEncoder.encode(newPassword));

        log.debug("修改{}的密码",username);
    }
}
