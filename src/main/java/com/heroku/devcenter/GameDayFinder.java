package com.heroku.devcenter;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameDayFinder {

	private String gamedayUrl = "http://feedmonster.iliga.de/feeds/il/de/competitions/1/1271/matchdaysOverview.json";

	public String getCurrentGameDay() {
		try {
			String content = loadFile(gamedayUrl);
			JSONArray days = new JSONObject(content).getJSONArray("matchdays");

			for (int i = 0; i < days.length(); i++) {
				JSONObject tempDay = days.getJSONObject(i);
				if (tempDay.getBoolean("isCurrentMatchday")) {

					return tempDay.getString("name").split(". ")[0];

				}
			}

			return null;
		} catch (Exception e) {
			return null;
		}
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
