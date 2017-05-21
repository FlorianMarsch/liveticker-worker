package de.florianmarsch.liveticker.gameday;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Gameday {

	private Integer gameday;
	private String season;


	public static Gameday getCurrentGameDay() {
		try {
			String gamedayUrl = "http://classic-kader-api.herokuapp.com/api/currentGameday";
			InputStream is = (InputStream) new URL(gamedayUrl).getContent();
			String content = IOUtils.toString(is, "UTF-8");
			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(content, Gameday.class);
		} catch (Exception e) {
			return null;
		}
	}

	public Boolean isSame(Integer lastGameDay) {
		return lastGameDay.equals(getGameday());
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public Integer getGameday() {
		return gameday;
	}

	public void setGameday(Integer gameday) {
		this.gameday = gameday;
	}
	
	
}
