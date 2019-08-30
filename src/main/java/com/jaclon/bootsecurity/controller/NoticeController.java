package com.jaclon.bootsecurity.controller;

import com.jaclon.bootsecurity.annotation.LogAnnotation;
import com.jaclon.bootsecurity.dao.NoticeDao;
import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.dto.NoticeReadVO;
import com.jaclon.bootsecurity.dto.NoticeVO;
import com.jaclon.bootsecurity.model.Notice;
import com.jaclon.bootsecurity.model.SysUser;
import com.jaclon.bootsecurity.page.table.PageTableHandler;
import com.jaclon.bootsecurity.page.table.PageTableRequest;
import com.jaclon.bootsecurity.page.table.PageTableResponse;
import com.jaclon.bootsecurity.utils.UserUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/23
 * @time 14:24
 */
@RestController
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeDao noticeDao;

    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "保存公告")
    @PreAuthorize("hasAuthority('notice:add')")
    public Notice saveNotice(@RequestBody Notice notice) {
        noticeDao.save(notice);

        return notice;
    }

    /**
     * 用在updateNotice.html
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取公告")
    @PreAuthorize("hasAuthority('notice:query')")
    public Notice get(@PathVariable Long id) {
        return noticeDao.getById(id);
    }

    /**
     * 详情页根据id获取公告内容和已读者
     * @param id
     * @return
     */
    @GetMapping(params = "id")
    public NoticeVO readNotice(Long id){
        NoticeVO noticeVO = new NoticeVO();
        Notice notice = noticeDao.getById(id);

        if(notice == null || notice.getStatus() == Notice.Status.DRAFT){
            return noticeVO;
        }

        noticeVO.setNotice(notice);
        noticeDao.saveReadRecord(notice.getId(),UserUtil.getLoginUser().getId());
        List<SysUser> users = noticeDao.listReadUsers(id);
        noticeVO.setUsers(users);
        return  noticeVO;

    }

    @LogAnnotation
    @PutMapping
    @ApiOperation(value = "修改公告")
    @PreAuthorize("hasAuthority('notice:add')")
    public Notice updateNotice(@RequestBody Notice notice) {
        Notice no = noticeDao.getById(notice.getId());
        if (no.getStatus() == Notice.Status.PUBLISH) {
            throw new IllegalArgumentException("发布状态的不能修改");
        }
        noticeDao.update(notice);

        return notice;
    }

    @GetMapping
    @ApiOperation(value = "公告管理列表")
    @PreAuthorize("hasAuthority('notice:query')")
    public PageTableResponse listNotice(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return noticeDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<Notice> list(PageTableRequest request) {
                return noticeDao.list(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }

    @RequestMapping("/count-unread")
    public Integer countUnread(){
        LoginUser loginUser = UserUtil.getLoginUser();
        return noticeDao.countUnread(loginUser.getId());
    }

    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除公告")
    @PreAuthorize("hasAuthority('notice:del')")
    public void delete(@PathVariable Long id) {
        noticeDao.delete(id);
    }

    /**
     * 获取发布的公告，用在noticePublist.html
     * @param request
     * @return
     */
    @GetMapping("/published")
    @ApiOperation(value="公告列表")
    public PageTableResponse listNoticeReadVO(PageTableRequest request){
        request.getParams().put("userId", UserUtil.getLoginUser().getId());

        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return noticeDao.countNotice(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<NoticeReadVO> list(PageTableRequest request) {
                return noticeDao.listNotice(request.getParams(), request.getOffset(), request.getLimit());
            }
        }).handle(request);
    }
}
