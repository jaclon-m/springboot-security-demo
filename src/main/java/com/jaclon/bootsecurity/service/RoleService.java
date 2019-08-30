package com.jaclon.bootsecurity.service;

import com.jaclon.bootsecurity.dto.RoleDto;

/**
 * @author jaclon
 * @date 2019/8/26
 */
public interface RoleService {
    void saveRole(RoleDto roleDto);

    void deleteRole(Long id);
}
