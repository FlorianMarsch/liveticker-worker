package de.florianmarsch.liveticker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import de.florianmarsch.liveticker.slack.SlackConnection;
import de.florianmarsch.liveticker.twitter.Connection;
import de.florianmarsch.liveticker.twitter.Tweet;
import de.florianmarsch.liveticker.twitter.TweetCreator;

public class TweetJob implements Job {

	final static Logger logger = LoggerFactory.getLogger(TweetJob.class);

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		EventService service = new EventService();
		LiveTicker liveTicker = new LiveTicker();
		SwitchService switchService = new SwitchService();
		Connection con = new Connection();
		SlackConnection slack = new SlackConnection();

		logger.info("execute new turn around and tweet live results");
		Gameday currentGameDay = Gameday.getCurrentGameDay();
		if (currentGameDay == null) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("detect gameday : " + currentGameDay.getGameday());
		}

		Integer lastGameDay = switchService.getLastGameDay(currentGameDay);
		Boolean gameDayChanged = !currentGameDay.isSame(lastGameDay);
		if (gameDayChanged) {

			// todo : tweet leaderboard
			Tweet leaderBoardTweet = new Tweet(null);
			leaderBoardTweet
					.setText("Spieltag " + lastGameDay + " ist in der #comunioLDC vorbei. Das sind die Ergebnisse :");
			leaderBoardTweet.setImage("http://football-api.florianmarsch.de/leaderboard.png");
			leaderBoardTweet.setIsImage(Boolean.TRUE);

			con.tweet(leaderBoardTweet);
			slack.tweet(leaderBoardTweet);

			Mail mail = new Mail(lastGameDay);
			mail.send();
		}

		Set<Event> liveTickerEvents = liveTicker.getGoals(currentGameDay);
			logger.info("recive " + liveTickerEvents.size() + " Events");
		Set<Event> newOnes = service.saveAndReturnNewEvents(liveTickerEvents);
		logger.info("filter " + newOnes.size() + " new Events");
		Set<Event> lostOnes = service.deleteAndReturnLostEvents(liveTickerEvents, currentGameDay);
		logger.info("find " + lostOnes.size() + " wrong Events");

		Set<Event> events = new HashSet<Event>();
		events.addAll(newOnes);
		events.addAll(lostOnes);

		if (events.isEmpty()) {
			logger.info("quit processing");
			return;
		}

		TweetCreator mc = new TweetCreator(events);
		for (Tweet tweet : mc.getTweets()) {
			con.tweet(tweet);
			slack.tweet(tweet);
		}
		if (!mc.getTweets().isEmpty()) {
			try {
				new URL("http://football-api.florianmarsch.de/leaderboard_download.php").openConnection().getContent();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("end processing");
	}
}
