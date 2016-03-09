package com.heroku.devcenter.gameday;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gameday {

	final static Logger logger = LoggerFactory.getLogger(Gameday.class);
	  
	private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	private static String gamedayUrl = "http://feedmonster.iliga.de/feeds/il/de/competitions/1/1271/matchdaysOverview.json";

	public static Gameday getCurrentGameDay() {
		try {
			String content = loadFile(gamedayUrl);
			JSONArray days = new JSONObject(content).getJSONArray("matchdays");

			for (int i = 0; i < days.length(); i++) {
				JSONObject tempDay = days.getJSONObject(i);
				if (tempDay.getBoolean("isCurrentMatchday")) {

					String temp= tempDay.getString("name").split(". ")[0];
					Gameday response = new Gameday();
					response.setNumber(Integer.valueOf(temp));
					if (logger.isDebugEnabled()) {
						logger.debug("run gameday"+response.getNumber());
					}
					return response;
				}
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private static String loadFile(String url) {

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

	public Boolean isSame(Integer lastGameDay) {
		return lastGameDay.equals(getNumber());
	}
}
