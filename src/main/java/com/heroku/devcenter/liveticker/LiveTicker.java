package com.heroku.devcenter.liveticker;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.heroku.devcenter.gameday.Gameday;

public class LiveTicker {

	public LiveTicker() {

	}

	public Set<Event> getGoals(Gameday aMatchday) {
		Integer gameday = aMatchday.getNumber();
		String content = loadFile("http://fussballmanager.herokuapp.com/live/" + gameday);

		Set<Event> eventList = new HashSet<Event>();
		try {
			JSONObject json = new JSONObject(content);
			JSONArray data = json.getJSONArray("data");

			for (int i = 0; i < data.length(); i++) {

				JSONObject goal = data.getJSONObject(i);	
				
				Event e = new Event();
				e.setId(goal.getString("id"));
				e.setName(goal.getString("name"));
				e.setType(goal.getString("type"));
				e.setOwner(goal.getString("owner"));
				e.setGameTag(goal.getString("gameTag"));
				e.setResult(goal.getString("result"));
				e.setFakeGame(goal.getBoolean("fakeGame"));
				e.setByeGame(goal.getBoolean("byeGame"));
				e.setGameday(gameday);
				e.setCreationDate(new Date());
				eventList.add(e);

			}

		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("Abbruch", e);
		}
		return eventList;
	}

	private String loadFile(String url) {

		StringBuffer tempReturn = new StringBuffer();
		try {
			URL u = new URL(url);
			InputStream is = u.openStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			String s;

			while ((s = dis.readLine()) != null) {
				tempReturn.append(s);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempReturn.toString();
	}
}
