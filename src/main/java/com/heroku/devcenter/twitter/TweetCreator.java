package com.heroku.devcenter.twitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.heroku.devcenter.liveticker.Event;

public class TweetCreator {

	private List<Tweet> messages = new ArrayList<Tweet>();

	public List<Tweet> getTweets() {
		return messages;
	}


	public TweetCreator(Set<Event> gameDayGoals) {
		for (Event event : gameDayGoals) {
			messages.add(new Tweet(event));
		}
	}
}
