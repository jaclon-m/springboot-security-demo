package com.jaclon.bootsecurity.controller;

import com.google.common.collect.Maps;
import com.jaclon.bootsecurity.annotation.LogAnnotation;
import com.jaclon.bootsecurity.dao.RoleDao;
import com.jaclon.bootsecurity.dto.RoleDto;
import com.jaclon.bootsecurity.model.Role;
import com.jaclon.bootsecurity.page.table.PageTableHandler;
import com.jaclon.bootsecurity.page.table.PageTableRequest;
import com.jaclon.bootsecurity.page.table.PageTableResponse;
import com.jaclon.bootsecurity.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/26
 */
@Api(tags = "角色")
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleService roleService;

    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "保存角色")
    @PreAuthorize("hasAuthority('sys:role:add')")
    public void saveRole(@RequestBody RoleDto roleDto) {
        roleService.saveRole(roleDto);
    }

    @GetMapping
    @ApiOperation(value = "角色列表")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public PageTableResponse listRoles(PageTableRequest request){
       /* return new PageTableHandler(() -> roleDao.count(request.getParams())
        ,() -> roleDao.list(request.getParams(),request.getOffset(),request.getLimit())
        ).handle(request);*/

        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return roleDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<Role> list(PageTableRequest request) {
                List<Role> list = roleDao.list(request.getParams(), request.getOffset(), request.getLimit());
                return list;
            }
        }).handle(request);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取角色")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public Role get(@PathVariable Long id) {
        return roleDao.getById(id);
    }

    @GetMapping("/all")
    @ApiOperation(value = "所有角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public List<Role> roles() {
        return roleDao.list(Maps.newHashMap(), null, null);
    }

    @GetMapping(params = "userId")
    @ApiOperation(value = "根据用户id获取拥有的角色")
    @PreAuthorize("hasAnyAuthority('sys:user:query','sys:role:query')")
    public List<Role> roles(Long userId) {
        return roleDao.listByUserId(userId);
    }

    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除角色")
    @PreAuthorize("hasAuthority('sys:role:del')")
    public void delete(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}
