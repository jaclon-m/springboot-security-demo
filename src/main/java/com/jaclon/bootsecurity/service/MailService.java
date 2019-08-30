package com.jaclon.bootsecurity.service;

import com.jaclon.bootsecurity.model.Mail;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/29
 */
public interface MailService {
    void save(Mail mail, List<String> toUser);
}
