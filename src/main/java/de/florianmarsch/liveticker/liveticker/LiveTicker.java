package de.florianmarsch.liveticker.liveticker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.florianmarsch.liveticker.gameday.Gameday;
import de.florianmarsch.liveticker.slack.SlackConnection;

public class LiveTicker {

	public LiveTicker() {

	}

	public Set<Event> getGoals(Gameday aMatchday) {
		
		Set<Event> eventList = new HashSet<Event>();
		
		try {
			String gamedayUrl = "http://fussballmanager.herokuapp.com/live/" + aMatchday.getGameday();
			InputStream is = (InputStream) new URL(gamedayUrl).getContent();
			String content = IOUtils.toString(is, "UTF-8");

			

			JSONObject json = new JSONObject(content);
			JSONArray data = json.getJSONArray("data");

			for (int i = 0; i < data.length(); i++) {

				JSONObject goal = data.getJSONObject(i);

				Event e = new Event();
				e.setId(goal.getString("id"));
				e.setName(goal.getString("name"));
				e.setType(goal.getString("type"));
				e.setOwner(goal.getString("owner"));
				e.setGameTag(goal.getString("gameHashTag"));
				e.setResult(goal.getString("result"));
				e.setFakeGame(goal.getBoolean("fake"));
				e.setByeGame(goal.getBoolean("bye"));
				e.setGameday(aMatchday.getGameday());
				e.setCreationDate(new Date());
				e.setMatch(goal.getString("match"));
				eventList.add(e);

			}

		} catch (JSONException e) {
			new SlackConnection().handleException(e);
			e.printStackTrace();
			throw new RuntimeException("Abbruch", e);
		} catch (IOException e1) {
			new SlackConnection().handleException(e1);
			e1.printStackTrace();
			throw new RuntimeException("Abbruch", e1);
		}
		return eventList;
	}
}
