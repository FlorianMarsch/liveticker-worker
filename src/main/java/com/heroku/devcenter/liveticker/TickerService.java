package com.heroku.devcenter.liveticker;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heroku.devcenter.TweetJob;
import com.heroku.devcenter.gameday.Gameday;

public class TickerService {
	final static Logger logger = LoggerFactory.getLogger(TweetJob.class);

	Map<String, Tick> map = new HashMap<String, Tick>();

	public TickerService() {
		try {
			String all = loadFile("http://fussballmanager.herokuapp.com/Tick");
			JSONObject data = new JSONObject(all);
			JSONArray ticks = data.getJSONArray("data");
			for (int i = 0; i < ticks.length(); i++) {
				JSONObject tick = ticks.getJSONObject(i);

				Tick tempTick = new Tick();
				tempTick.setExternId(tick.getString("externId"));
				tempTick.setName(tick.getString("name"));
				tempTick.setEvent(tick.getString("event"));
				tempTick.setMatchdayNumber(tick.getInt("matchdayNumber"));
				tempTick.setId(tick.getString("id"));
				map.put(tempTick.getExternId(), tempTick);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("already " + map.size() + " ticks here");
	}

	public List<Tick> getLiveTickerEvents(Gameday aMatchday) {
		String content = getLiveTickerText(aMatchday);
		return getLiveTickerEvents(content, aMatchday);
	}

	public String getLiveTickerText(Gameday aMatchday) {
		Integer id = aMatchday.getNumber() + 5662927;
		String content = loadFile(
				"http://feedmonster.iliga.de/feeds/il/de/competitions/1/1271/matchdays/" + id + ".json");
		return content;
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

	public List<Tick> getLiveTickerEvents(String content, Gameday aMatchday) {

		List<Tick> eventList = new ArrayList<Tick>();
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
							String player = goal.getJSONObject("player").getString("name");

							String norm = Normalizer.normalize(player, Normalizer.Form.NFD);
							norm = norm.replaceAll("[^\\p{ASCII}]", "");

							Tick e = new Tick();
							e.setExternId(eventId);
							e.setEvent(type);
							e.setName(norm);
							e.setMatchdayNumber(aMatchday.getNumber());
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

	public void save(Tick aTick) throws JSONException, IOException {
		String id = aTick.getExternId();
		if (map.get(id) == null) {
			logger.info("new tick detected : " + aTick.getName());

			JSONObject tempJSONObject = new JSONObject();

			tempJSONObject.put("externId",aTick.getExternId());
			tempJSONObject.put("name", aTick.getName());
			tempJSONObject.put("event", aTick.getEvent());
			tempJSONObject.put("matchdayNumber", aTick.getMatchdayNumber());

			String json = tempJSONObject.toString();
			URL url = new URL("http://fussballmanager.herokuapp.com/Tick/new");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
			out.write(json);
			out.close();
			httpCon.getInputStream();
		}
	}

	public List<Tick> getLost(List<Tick> liveTickerEvents) {
		List<Tick> olds = new ArrayList<Tick>();
		olds.addAll(map.values());
		olds.removeAll(liveTickerEvents);
		return olds;
	}

	public void delete(Tick aTick) throws IOException {
		logger.info("delete tick  : " + aTick.getName());

		
		
		URL url = new URL("http://fussballmanager.herokuapp.com/Tick/"+aTick.getId());
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("DELETE");
		OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
		out.write("");
		out.close();
		httpCon.getInputStream();
		
	}
}
