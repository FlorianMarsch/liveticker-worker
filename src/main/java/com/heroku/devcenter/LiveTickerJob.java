package com.heroku.devcenter;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heroku.devcenter.gameday.Gameday;
import com.heroku.devcenter.liveticker.Tick;
import com.heroku.devcenter.liveticker.TickerService;

public class LiveTickerJob implements Job {

	final static Logger logger = LoggerFactory.getLogger(LiveTickerJob.class);


	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		logger.info("execute new turn around and check live results");
		Gameday currentGameDay = Gameday.getCurrentGameDay();
		if (logger.isDebugEnabled()) {
			logger.debug("detect gameday : " + currentGameDay.getNumber());
		}
		TickerService service = new TickerService(currentGameDay);
		List<Tick> liveTickerEvents = service.getLiveTickerEvents();
		for (Tick tick : liveTickerEvents) {
			try {
				service.save(tick);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<Tick> lost = service.getLost(liveTickerEvents);
		for (Tick tick : lost) {
			try {
				service.delete(tick);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		logger.info("end processing");
	}
}
