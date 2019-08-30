package com.jaclon.bootsecurity.service.impl;

import com.jaclon.bootsecurity.dao.SysLogsDao;
import com.jaclon.bootsecurity.dto.LoginUser;
import com.jaclon.bootsecurity.model.SysLogs;
import com.jaclon.bootsecurity.model.SysUser;
import com.jaclon.bootsecurity.service.SysLogService;
import com.jaclon.bootsecurity.utils.UserUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author jaclon
 * @date 2019/8/6
 * @time 10:55
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogsDao sysLogsDao;

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Override
    public void save(SysLogs sysLogs) {
        LoginUser user = UserUtil.getLoginUser();
        if(user == null || user.getId() == null){
            return;
        }
        sysLogs.setUser(user);
        sysLogsDao.save(sysLogs);
    }

    @Override
    @Async
    public void save(Long userId, String module, Boolean flag, String remark) {
        SysLogs sysLogs = new SysLogs();
        sysLogs.setFlag(flag);
        sysLogs.setModule(module);
        sysLogs.setRemark(remark);

        SysUser user = new SysUser();
        user.setId(userId);
        sysLogs.setUser(user);

        sysLogsDao.save((sysLogs));

    }

    @Override
    public void deleteLogs() {
        Date date = DateUtils.addMonths(new Date(), -3);
        String time = DateFormatUtils.format(date, DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern());

        int n = sysLogsDao.deleteLogs(time);
        log.info("删除{}之前日志{}条",time,n);
    }
}
