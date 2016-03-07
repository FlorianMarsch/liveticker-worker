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

	public LiveTicker(){
		
	}
	
	public Set<Event> getGoals(Gameday aMatchday) {
		Integer id = aMatchday.getNumber() + 5662927;
		String content = loadFile("http://feedmonster.iliga.de/feeds/il/de/competitions/1/1271/matchdays/"
				+ id + ".json");
		
		Set<Event> eventList = new HashSet<Event>();
		try {
			JSONObject json = new JSONObject(content);
			JSONArray kickoffs = json.getJSONArray("kickoffs");

			for (int k = 0; k < kickoffs.length(); k++) {
				JSONObject element = kickoffs.getJSONObject(k);
				JSONArray groups = element.getJSONArray("groups");
				for (int gr = 0; gr < groups.length(); gr++) {
					JSONObject group = groups.getJSONObject(gr);
					JSONArray matches = group.getJSONArray("matches");
					for (int m = 0; m < matches.length(); m++) {
						JSONObject match = matches.getJSONObject(m);
						JSONArray goals = match.getJSONArray("goals");
						for (int go = 0; go < goals.length(); go++) {
							JSONObject goal = goals.getJSONObject(go);
							String type = goal.getString("type");
							String eventId = goal.getString("eventId");
							String player = goal.getJSONObject("player")
									.getString("name");

							String norm = Normalizer.normalize(player, Normalizer.Form.NFD);
							norm = norm.replaceAll("[^\\p{ASCII}]", "");
							
							Event e = new Event();
							e.setId(eventId);
							e.setType(type);
							e.setName(norm);
							e.setGameday(aMatchday.getNumber());
							e.setCreationDate(new Date());
							eventList.add(e);

						}
					}
				}
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
			DataInputStream dis = new DataInputStream(new BufferedInputStream(
					is));
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
