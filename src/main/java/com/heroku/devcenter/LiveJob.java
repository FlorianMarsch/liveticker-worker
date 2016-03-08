package com.heroku.devcenter;

import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heroku.devcenter.db.EventService;
import com.heroku.devcenter.gameday.Gameday;
import com.heroku.devcenter.liveticker.Event;
import com.heroku.devcenter.liveticker.LiveTicker;
import com.heroku.devcenter.queue.QueueFactory;

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
			logger.debug("recive " + liveTickerEvents.size() + " Events");
		}
		Set<Event> newOnes = service.saveAndReturnNewEvents(liveTickerEvents);
		logger.info("filter " + newOnes.size() + " new Events");
		if (newOnes.isEmpty()) {
			logger.info("quit processing");
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		QueueFactory queue = new QueueFactory();
		for (Event event : newOnes) {
			byte[] json;
			try {
				json = mapper.writeValueAsBytes(event);
				queue.publish(json );
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		queue.close();
		logger.info("end processing");
	}
}
