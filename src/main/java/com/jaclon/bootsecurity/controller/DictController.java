package com.jaclon.bootsecurity.controller;

import com.jaclon.bootsecurity.dao.DictDao;
import com.jaclon.bootsecurity.model.Dict;
import com.jaclon.bootsecurity.page.table.PageTableHandler;
import com.jaclon.bootsecurity.page.table.PageTableRequest;
import com.jaclon.bootsecurity.page.table.PageTableResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理
 * @author jaclon
 * @date 2019/8/26
 */
@RestController
@RequestMapping("/dicts")
public class DictController {

    @Autowired
    private DictDao dictDao;

    /**
     * start,length参数是pagetable自带的
     * @param request
     * @return
     */
    @GetMapping(params = {"start","length"})
    @ApiOperation(value = "列表")
    @PreAuthorize("hasAuthority('dict:query')")
    public PageTableResponse list(PageTableRequest request){
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return dictDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<Dict> list(PageTableRequest request) {
                return dictDao.list(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dict:query')")
    @ApiOperation(value = "根据id获取")
    public Dict get(@PathVariable Long id) {
        return dictDao.getById(id);
    }

    @PreAuthorize("hasAuthority('dict:add')")
    @PostMapping
    @ApiOperation(value = "保存")
    public Dict save(@RequestBody Dict dict) {
        Dict d = dictDao.getByTypeAndK(dict.getType(), dict.getK());
        if (d != null) {
            throw new IllegalArgumentException("类型和key已存在");
        }
        dictDao.save(dict);

        return dict;
    }

    @PreAuthorize("hasAuthority('dict:add')")
    @PutMapping
    @ApiOperation(value = "修改")
    public Dict update(@RequestBody Dict dict) {
        dictDao.update(dict);

        return dict;
    }

    @PreAuthorize("hasAuthority('dict:del')")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    public void delete(@PathVariable Long id) {
        dictDao.delete(id);
    }

    /**
     * 供其他模块按类型搜索使用
     * @param type
     * @return
     */
    @GetMapping(params = "type")
    public List<Dict> listByType(String type) {
        return dictDao.listByType(type);
    }
}
