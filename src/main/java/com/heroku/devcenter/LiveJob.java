package com.heroku.devcenter;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveJob implements Job {

    final static Logger logger = LoggerFactory.getLogger(LiveJob.class);
	
    GameDayFinder gameDayFinder = new GameDayFinder();
    
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Gameday : "+gameDayFinder.getCurrentGameDay());
	}
}
