package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dao.RoleDao;
import com.jaclon.bootsecurity.dto.RoleDto;
import com.jaclon.bootsecurity.model.Role;
import com.jaclon.bootsecurity.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/26
 */
@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private RoleDao roleDao;

    @Override
    @Transactional
    public void saveRole(RoleDto roleDto) {
        Role role = roleDto;
        List <Long> permissionIds = roleDto.getPermissionIds();
        permissionIds.remove(0L);

        if(role.getId() != null){
            updateRole(role,permissionIds);
        }else {
            saveRole(role,permissionIds);
        }

    }

    private void updateRole(Role role, List <Long> permissionIds) {
        Role r = roleDao.getRole(role.getName());
        if (r != null && r.getId() != role.getId()) {
            throw new IllegalArgumentException(role.getName() + "已存在");
        }

        roleDao.update(role);

        roleDao.deleteRolePermission(role.getId());
        if (!CollectionUtils.isEmpty(permissionIds)) {
            roleDao.saveRolePermission(role.getId(), permissionIds);
        }
        log.debug("修改角色{}", role.getName());
    }

    private void saveRole(Role role, List<Long> permissionIds){
        Role r = roleDao.getRole(role.getName());
        if(r != null){
            throw new IllegalArgumentException(role.getName() + "已存在");
        }
        roleDao.save(role);
        if(!CollectionUtils.isEmpty(permissionIds)){
            //id在role经过保存后会生成
            roleDao.saveRolePermission(role.getId(),permissionIds);
        }
        log.debug("新增角色{}",role.getName());
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleDao.deleteRolePermission(id);
        roleDao.deleteRoleUser(id);
        roleDao.delete(id);

        log.debug("删除角色id:{}", id);
    }
}
