package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dao.PermissionDao;
import com.jaclon.bootsecurity.model.Permission;
import com.jaclon.bootsecurity.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jaclon
 * @date 2019/8/23
 * @time 10:44
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    PermissionDao permissionDao;

    @Override
    public void save(Permission permission) {
        permissionDao.save(permission);
        log.debug("新增菜单{}",permission.getName());
    }

    @Override
    public void update(Permission permission) {
        permissionDao.update(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        permissionDao.deleteRolePermission(id);
        permissionDao.delete(id);
        permissionDao.deleteByParentId(id);
        log.debug("删除菜单id: {}",id);
    }
}
