package com.jaclon.bootsecurity.service;

import com.jaclon.bootsecurity.dto.UserDto;
import com.jaclon.bootsecurity.model.SysUser;

/**
 * @author jaclon
 * @date 2019/8/6
 * @time 0:11
 */
public interface UserService {

    SysUser saveUser(UserDto userDto);

    SysUser updateUser(UserDto userDto);

    SysUser getUser(String username);

    void changePassword(String username, String oldPassword, String newPassword);

}
