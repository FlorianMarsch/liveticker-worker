package com.heroku.devcenter;


import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatMinutelyForever;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerMain {

    final static Logger logger = LoggerFactory.getLogger(SchedulerMain.class);
    
    public static void main(String[] args) throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail jobDetail = newJob(LiveJob.class).build();
        
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(repeatMinutelyForever(1))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }


        
    

}
