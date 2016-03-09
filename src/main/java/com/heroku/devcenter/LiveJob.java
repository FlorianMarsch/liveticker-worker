package com.heroku.devcenter;

import java.util.HashSet;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heroku.devcenter.db.EventService;
import com.heroku.devcenter.gameday.Gameday;
import com.heroku.devcenter.gameday.SwitchService;
import com.heroku.devcenter.liveticker.Event;
import com.heroku.devcenter.liveticker.LiveTicker;
import com.heroku.devcenter.mail.Mail;
import com.heroku.devcenter.twitter.TweetCreator;
import com.heroku.devcenter.twitter.Connection;
import com.heroku.devcenter.twitter.Tweet;

public class LiveJob implements Job {

	final static Logger logger = LoggerFactory.getLogger(LiveJob.class);


	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		EventService service = new EventService();
		LiveTicker liveTicker = new LiveTicker();
		SwitchService switchService = new SwitchService();

		logger.info("execute new turn around and check live results");
		Gameday currentGameDay = Gameday.getCurrentGameDay();
		if (logger.isDebugEnabled()) {
			logger.debug("detect gameday : " + currentGameDay.getNumber());
		}
		
		Integer lastGameDay = switchService.getLastGameDay(currentGameDay);
		Boolean gameDayChanged = !currentGameDay.isSame(lastGameDay);
		if(gameDayChanged){
			Mail mail = new Mail(lastGameDay);
			mail.send();
		}
		
		Set<Event> liveTickerEvents = liveTicker.getGoals(currentGameDay);
		if (logger.isDebugEnabled()) {
			logger.debug("recive " + liveTickerEvents.size() + " Events");
		}
		Set<Event> newOnes = service.saveAndReturnNewEvents(liveTickerEvents);
		logger.info("filter " + newOnes.size() + " new Events");
		Set<Event> lostOnes = service.deleteAndReturnLostEvents(liveTickerEvents,currentGameDay);
		logger.info("find " + lostOnes.size() + " wrong Events");
		
		Set<Event> events = new HashSet<Event>();
		events.addAll(newOnes);
		events.addAll(lostOnes);
		
		if (events.isEmpty()) {
			logger.info("quit processing");
			return;
		}
		TweetCreator mc = new TweetCreator(events);
		Connection con = new Connection();
		for (Tweet tweet : mc.getTweets()) {
			con.tweet(tweet.getText());
		}
		logger.info("end processing");
	}
}
