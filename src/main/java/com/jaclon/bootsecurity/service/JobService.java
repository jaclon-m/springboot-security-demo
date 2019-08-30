package com.jaclon.bootsecurity.service;

import com.jaclon.bootsecurity.model.JobModel;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

/**
 * @author jaclon
 * @date 2019/8/28
 */
public interface JobService {

    void saveJob(JobModel jobModel);

    void doJob(JobDataMap jobDataMap);

    void deleteJob(Long id) throws SchedulerException;
}
