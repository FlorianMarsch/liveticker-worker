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
import com.heroku.devcenter.twitter.TweetCreator;
import com.heroku.devcenter.twitter.Connection;
import com.heroku.devcenter.twitter.Tweet;

public class LiveJob implements Job {

	final static Logger logger = LoggerFactory.getLogger(LiveJob.class);

	private LiveTicker liveTicker = new LiveTicker();

	private EventService service = new EventService();

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("execute new turn around and check live results");
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
		TweetCreator mc = new TweetCreator(newOnes);
		Connection con = new Connection();
		for (Tweet tweet : mc.getTweets()) {
			con.tweet(tweet.getText());
		}
		logger.info("end processing");
	}
}
