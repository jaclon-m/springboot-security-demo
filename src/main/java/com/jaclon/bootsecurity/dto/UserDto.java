package com.jaclon.bootsecurity.dto;

import com.jaclon.bootsecurity.model.SysUser;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/6
 * @time 0:13
 */
public class UserDto extends SysUser {

    private static final long serialVersionUID = -184009306207076712L;

    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
