package com.jaclon.bootsecurity.service;

import com.jaclon.bootsecurity.model.Permission;

/**
 * @author jaclon
 * @date 2019/8/23
 * @time 10:44
 */
public interface PermissionService {
    void save(Permission permission);

    void update(Permission permission);

    void delete(Long id);
}
