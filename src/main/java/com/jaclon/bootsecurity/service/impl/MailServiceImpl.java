package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dao.MailDao;
import com.jaclon.bootsecurity.model.Mail;
import com.jaclon.bootsecurity.service.MailService;
import com.jaclon.bootsecurity.service.SendMailSevice;
import com.jaclon.bootsecurity.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jaclon
 * @date 2019/8/29
 */
@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private SendMailSevice sendMailSevice;
    @Autowired
    private MailDao mailDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Mail mail, List<String> toUser) {
        mail.setUserId(UserUtil.getLoginUser().getId());
        mailDao.save(mail);

        toUser.forEach(u -> {
            int status = 1;
            try {
                sendMailSevice.sendMail(u, mail.getSubject(), mail.getContent());
            } catch (Exception e) {
                log.error("发送邮件失败", e);
                status = 0;
            }

            mailDao.saveToUser(mail.getId(), u, status);
        });
    }
}
