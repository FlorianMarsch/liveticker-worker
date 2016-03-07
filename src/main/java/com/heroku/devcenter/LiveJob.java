package com.heroku.devcenter;

import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heroku.devcenter.db.EventService;
import com.heroku.devcenter.gameday.Gameday;
import com.heroku.devcenter.liveticker.Event;
import com.heroku.devcenter.liveticker.LiveTicker;

public class LiveJob implements Job {

    final static Logger logger = LoggerFactory.getLogger(LiveJob.class);
	
    private LiveTicker liveTicker = new LiveTicker();
    
    private EventService service = new EventService();
    
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("execute new turn around");
		Gameday currentGameDay = Gameday.getCurrentGameDay();
		if (logger.isDebugEnabled()) {
			logger.debug("detect gameday : " + currentGameDay.getNumber());
		}
		Set<Event> liveTickerEvents = liveTicker.getGoals(currentGameDay);
		if (logger.isDebugEnabled()) {
			logger.debug("recive " + liveTickerEvents.size() +" Events");
		}
		Set<Event> newOnes = service.saveAndReturnNewEvents(liveTickerEvents);
		logger.info("filter " + newOnes.size() +" new Events");
	}
}
