package de.florianmarsch.liveticker.twitter;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

public class Connection {

	private CloseableHttpClient client;
	
	public Connection(){
		client = HttpClients.createDefault();

	
	}
	
	
	public void tweet(String aMessage){
		try {
			HttpPost httppost = new HttpPost("http://social-system-api.herokuapp.com/api/twitter");
			httppost.setHeader("Content-Type", "application/json");
			JSONObject message = new JSONObject();
			message.put("tweet", aMessage);
			message.put("consumerKey",System.getenv("TWITTER_CONSUMER_KEY"));
			message.put("consumerSecret",System.getenv("TWITTER_CONSUMER_SECRET"));
			message.put("accessToken",System.getenv("TWITTER_ACCESS_TOKEN"));
			message.put("accessTokenSecret",System.getenv("TWITTER_ACCESS_TOKEN_SECRET"));
			httppost.setEntity(new StringEntity(message.toString()));
			client.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
