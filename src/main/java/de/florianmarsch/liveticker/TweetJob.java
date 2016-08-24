package de.florianmarsch.liveticker;

import java.util.HashSet;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.florianmarsch.liveticker.db.EventService;
import de.florianmarsch.liveticker.gameday.Gameday;
import de.florianmarsch.liveticker.gameday.SwitchService;
import de.florianmarsch.liveticker.liveticker.Event;
import de.florianmarsch.liveticker.liveticker.LiveTicker;
import de.florianmarsch.liveticker.mail.Mail;
import de.florianmarsch.liveticker.pushbullet.PushbulletConnection;
import de.florianmarsch.liveticker.twitter.TweetCreator;
import de.florianmarsch.liveticker.twitter.Connection;
import de.florianmarsch.liveticker.twitter.Tweet;

public class TweetJob implements Job {

	final static Logger logger = LoggerFactory.getLogger(TweetJob.class);


	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		EventService service = new EventService();
		LiveTicker liveTicker = new LiveTicker();
		SwitchService switchService = new SwitchService();

		logger.info("execute new turn around and tweet live results");
		Gameday currentGameDay = Gameday.getCurrentGameDay();
		if(currentGameDay == null){
			return;
		}
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
		PushbulletConnection push = new PushbulletConnection();
		for (Tweet tweet : mc.getTweets()) {
			String message = tweet.getText();
			con.tweet(message);
			push.send(message);
		}
		logger.info("end processing");
	}
}
