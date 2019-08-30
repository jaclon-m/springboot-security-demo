package com.jaclon.bootsecurity.controller;

import com.jaclon.bootsecurity.annotation.LogAnnotation;
import com.jaclon.bootsecurity.dao.UserDao;
import com.jaclon.bootsecurity.dto.UserDto;
import com.jaclon.bootsecurity.model.SysUser;
import com.jaclon.bootsecurity.page.table.PageTableHandler;
import com.jaclon.bootsecurity.page.table.PageTableRequest;
import com.jaclon.bootsecurity.page.table.PageTableResponse;
import com.jaclon.bootsecurity.service.UserService;
import com.jaclon.bootsecurity.utils.UserUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/23
 * @time 14:15
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;

    @GetMapping
    @ApiOperation(value = "用户列表")
    @PreAuthorize("hasAuthority('sys:user:query')")
    public PageTableResponse listUsers(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return userDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<SysUser> list(PageTableRequest request) {
                List<SysUser> list = userDao.list(request.getParams(), request.getOffset(), request.getLimit());
                return list;
            }
        }).handle(request);
    }

    @ApiOperation(value = "当前登录用户")
    @GetMapping("/current")
    public SysUser currentUser(){
        return UserUtil.getLoginUser();
    }

    @LogAnnotation
    @ApiOperation(value = "修改密码")
    @PutMapping("/{username}")
    @PreAuthorize("hasAuthority('sys:user:query')")
    public void changePassword(@PathVariable String username, String oldPassword, String newPassword){
        userService.changePassword(username,oldPassword,newPassword);
    }

    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "保存用户")
    @PreAuthorize("hasAuthority('sys:user:add')")
    public SysUser saveUser(@RequestBody UserDto userDto) {
        SysUser u = userService.getUser(userDto.getUsername());
        if (u != null) {
            throw new IllegalArgumentException(userDto.getUsername() + "已存在");
        }

        return userService.saveUser(userDto);
    }

    @LogAnnotation
    @PutMapping
    @ApiOperation(value = "修改用户")
    @PreAuthorize("hasAuthority('sys:user:add')")
    public SysUser updateUser(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @ApiOperation(value = "根据用户id获取用户")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:query')")
    public SysUser user(@PathVariable Long id) {
        return userDao.getById(id);
    }
}
