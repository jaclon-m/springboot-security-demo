package com.jaclon.bootsecurity.dto;

import com.jaclon.bootsecurity.model.Notice;

import java.util.Date;

/**
 * @author jaclon
 * @date 2019/8/27
 */
public class NoticeReadVO extends Notice {

    private static final long serialVersionUID = -3842182350180882396L;

    private Long userId;
    private Date readTime;
    private Boolean isRead;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
