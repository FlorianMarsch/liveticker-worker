package de.florianmarsch.liveticker.twitter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Connection {

	private Twitter twitter;
	
	public Connection(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(System.getenv("TWITTER_CONSUMER_KEY"))
		  .setOAuthConsumerSecret(System.getenv("TWITTER_CONSUMER_SECRET"))
		  .setOAuthAccessToken(System.getenv("TWITTER_ACCESS_TOKEN"))
		  .setOAuthAccessTokenSecret(System.getenv("TWITTER_ACCESS_TOKEN_SECRET"));
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	
	}
	
	
	public void tweet(String aMessage){
		try {
			Status status = twitter.updateStatus(aMessage);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
}
