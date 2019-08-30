package com.jaclon.bootsecurity.dao;

import com.jaclon.bootsecurity.model.Permission;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/6
 * @time 16:31
 */
@Mapper
public interface PermissionDao {

    @Select("select distinct p.* from sys_permission p inner join sys_role_permission rp on p.id = rp.permissionId inner join sys_role_user ru on ru.roleId = rp.roleId where ru.userId = #{userId} order by p.sort")
    List<Permission> listByUserId(Long id);

    @Insert("insert into sys_permission(parentId, name, css, href, type, permission, sort) values(#{parentId}, #{name}, #{css}, #{href}, #{type}, #{permission}, #{sort})")
    int save(Permission permission);

    @Update("update sys_permission t set parentId = #{parentId}, name = #{name}, css = #{css}, href = #{href}, type = #{type}, permission = #{permission}, sort = #{sort} where t.id = #{id}")
    int update(Permission permission);

    @Delete("delete from sys_role_permission where permissionId = #{permissionId}")
    int deleteRolePermission(Long id);

    @Delete("delete from sys_permission where id = #{id}")
    int delete(Long id);

    /**
     * 删除该菜单下的所有子菜单
     * @param id
     * @return
     */
    @Delete("delete from sys_permission where parentId = #{id}")
    int deleteByParentId(Long id);

    @Select("select * from sys_permission t order by t.sort")
    List<Permission> listAll();

    @Select("select * from sys_permission t where t.type = 1 order by t.sort")
    List<Permission> listParents();

    @Select("select * from sys_permission t where t.id = #{id}")
    Permission getById(Long id);

    @Select("select p.* from sys_permission p inner join sys_role_permission rp on p.id = rp.permissionId where rp.roleId = #{roleId} order by p.sort")
    List<Permission> listByRoleId(Long roleId);
}
