package de.florianmarsch.liveticker.liveticker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.florianmarsch.liveticker.gameday.Gameday;

public class TickerService {
	final static Logger logger = LoggerFactory.getLogger(TickerService.class);

	Map<String, Tick> map = new HashMap<String, Tick>();
	Gameday matchday;

	public TickerService(Gameday aMatchday) {
		matchday = aMatchday;
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
				if (tempTick.getMatchdayNumber() == aMatchday.getGameday()) {
					map.put(tempTick.getExternId(), tempTick);

				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("already " + map.size() + " ticks here");
	}

	public List<Tick> getLiveTickerEvents() {
		String content = loadFile("http://classic-kader-api.herokuapp.com/api/result/" + matchday.getSeason() + "/"
				+ matchday.getGameday());
		return getLiveTickerEvents(content);
	}

	public String loadFile(String gamedayUrl) {

		try {
			InputStream is = (InputStream) new URL(gamedayUrl).getContent();
			return IOUtils.toString(is, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("abbruch", e);
		}
	}

	public List<Tick> getLiveTickerEvents(String content) {

		List<Tick> eventList = new ArrayList<Tick>();
		try {
			JSONArray data = new JSONArray(content);

			for (int k = 0; k < data.length(); k++) {
				JSONObject goal = data.getJSONObject(k);

				String type = goal.getString("event");
				String eventId = goal.getString("id");
				String player = goal.getString("name");

				Tick e = new Tick();
				e.setExternId(eventId);
				e.setEvent(type);
				e.setName(player);
				e.setMatchdayNumber(matchday.getGameday());
				eventList.add(e);

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

			tempJSONObject.put("externId", aTick.getExternId());
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

		URL url = new URL("http://fussballmanager.herokuapp.com/Tick/" + aTick.getId());
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("DELETE");
		OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
		out.write("");
		out.close();
		httpCon.getInputStream();

	}
}
