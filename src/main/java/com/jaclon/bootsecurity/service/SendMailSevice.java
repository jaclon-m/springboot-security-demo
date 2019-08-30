package com.jaclon.bootsecurity.service;

import javax.mail.MessagingException;
import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/29
 */
public interface SendMailSevice {

    /**
     *批量发送邮件
     * @param toUser
     * @param subject
     *            标题
     * @param text
     *            内容（支持html格式）
     */
    void sendMail(List<String> toUser, String subject, String text);

    void sendMail(String toUser, String subject, String text) throws MessagingException;
}
