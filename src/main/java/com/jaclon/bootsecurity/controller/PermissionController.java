package com.jaclon.bootsecurity.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jaclon.bootsecurity.annotation.LogAnnotation;
import com.jaclon.bootsecurity.dao.PermissionDao;
import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.model.Permission;
import com.jaclon.bootsecurity.service.PermissionService;
import com.jaclon.bootsecurity.utils.UserUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限相关接口
 * @author jaclon
 * @date 2019/8/23
 * @time 10:39
 */
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "当前登录用户拥有的权限")
    @GetMapping("/current")
    public List<Permission> currentPermission(){
        LoginUser loginUser = UserUtil.getLoginUser();
        List <Permission> list = loginUser.getPermissions();
        final List<Permission> permissions = list.stream().filter(l -> l.getType().equals(1)).collect(Collectors.toList());
        
        setChild(permissions);

        return permissions.stream().filter(p -> p.getParentId().equals(0L)).collect(Collectors.toList());
    }

    /**
     * 循环终止条件： child 为 空
     * @param permissions
     */
    private void setChild(List<Permission> permissions) {
        permissions.parallelStream().forEach(per -> {
            List<Permission> child = permissions.stream().filter(p -> p.getParentId().equals(per.getId())).collect(Collectors.toList());
            per.setChild(child);
        });
    }

    @ApiOperation(value = "所有菜单")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public JSONArray permissionsAll(){
        List<Permission> permissions = permissionDao.listAll();
        JSONArray array = new JSONArray();
        setPermissionsTree(0L,permissions,array);
        return array;
    }

    @GetMapping("/parents")
    @ApiOperation(value = "一级菜单")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public List<Permission> parentMenu() {
        List<Permission> parents = permissionDao.listParents();

        return parents;
    }

    /**
     * 菜单树
     * @param pId
     * @param permissionsAll
     * @param array
     */
    private void setPermissionsTree(long pId, List<Permission> permissionsAll, JSONArray array) {
        for(Permission per: permissionsAll){
            if(per.getParentId().equals(pId)){
                //构建JSONObject
                String string = JSONObject.toJSONString(per);
                JSONObject parent = (JSONObject)JSONObject.parse(string);
                array.add(parent);

                if(permissionsAll.stream().filter(p -> p.getParentId().equals(per.getId())).findAny() != null){
                    JSONArray child = new JSONArray();
                    parent.put("child", child);
                    setPermissionsTree(per.getId(), permissionsAll, child);
                }
            }
        }
    }

    /**
     * 校验权限
     * @return
     */
    @GetMapping("/owns")
    @ApiOperation(value = "校验当前用户的权限")
    public Set<String> ownsPermission(){
        List <Permission> permissions = UserUtil.getLoginUser().getPermissions();
        if(CollectionUtils.isEmpty(permissions)){
            return Collections.emptySet();
        }

        return permissions.parallelStream().filter(p -> !StringUtils.isEmpty(p.getPermission()))
                .map(Permission::getPermission).collect(Collectors.toSet());
    }

    /**
     * 获取权限列表，menulist中initMenu方法中默认访问
     * @return
     */
    @ApiOperation(value = "菜单列表")
    @GetMapping
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public List<Permission> permissionList(){
        List <Permission> permissionsAll = permissionDao.listAll();
        List<Permission> list = Lists.newArrayList();
        setPermissionList(0L,permissionsAll,list);
        return list;
    }

    /**
     * 菜单列表，将菜单按 父菜单 子菜单 子菜单 父菜单 父菜单 子菜单。。。的顺序排列
     * @param pId
     * @param permissionsAll
     * @param list
     */
    private void setPermissionList(long pId, List<Permission> permissionsAll, List<Permission> list) {
        for(Permission per: permissionsAll){
            if(per.getParentId().equals(pId)){
                list.add(per);
                if(permissionsAll.stream().filter(p -> p.getParentId().equals(per.getId())).findAny() != null){
                    setPermissionList(per.getId(),permissionsAll,list);
                }
            }
        }
    }

    @GetMapping(params = "roleId")
    @ApiOperation(value = "根据角色Id获取权限")
    @PreAuthorize("hasAnyAuthority('sys:menu:query','sys:role:query')")
    public List<Permission> listByRoleId(Long roleId){
        return permissionDao.listByRoleId(roleId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public Permission get(@PathVariable Long id) {
        return permissionDao.getById(id);
    }

    /**
     * 添加/保存菜单
     * @param permission
     */
    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "保存菜单")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    public void save(@RequestBody Permission permission) {
        permissionDao.save(permission);
    }

    @LogAnnotation
    @PutMapping
    @ApiOperation(value = "修改菜单")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    public void update(@RequestBody Permission permission) {
        permissionService.update(permission);
    }


    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除菜单")
    @PreAuthorize("hasAuthority('sys:menu:del')")
    public void delete(@PathVariable Long id){
        permissionService.delete(id);
    }
}
