package com.jaclon.bootsecurity.dto;

import com.jaclon.bootsecurity.model.Role;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/26
 */
public class RoleDto extends Role {

    private static final long serialVersionUID = 4218495592167610193L;

    private List<Long> permissionIds;

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
