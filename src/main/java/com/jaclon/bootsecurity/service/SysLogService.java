package com.jaclon.bootsecurity.service;

import com.jaclon.bootsecurity.model.SysLogs;

/**
 * 日志
 * @author jaclon
 * @date 2019/8/6
 * @time 10:52
 */
public interface SysLogService {

    void save(SysLogs sysLogs);

    void save(Long userId, String module, Boolean flag, String remark);

    void deleteLogs();
}
