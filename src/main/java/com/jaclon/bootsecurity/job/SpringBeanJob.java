package com.jaclon.bootsecurity.job;

import com.jaclon.bootsecurity.config.JobConfig;
import com.jaclon.bootsecurity.service.JobService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author jaclon
 * @date 2019/8/28
 */
public class SpringBeanJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            ApplicationContext applicationContext = (ApplicationContext) jobExecutionContext.getScheduler().getContext()
                    .get(JobConfig.KEY);
            JobService jobService = applicationContext.getBean(JobService.class);
            jobService.doJob(jobExecutionContext.getJobDetail().getJobDataMap());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
