package de.florianmarsch.liveticker.slack;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import de.florianmarsch.liveticker.twitter.Tweet;

public class SlackConnection {

	private CloseableHttpClient client;
	
	public SlackConnection(){
		client = HttpClients.createDefault();

	
	}
	
	
	public void tweet(Tweet aMessage){
		try {
			HttpPost httppost = new HttpPost("http://social-system-api.herokuapp.com/api/slack");
			httppost.setHeader("Content-Type", "application/json");
			JSONObject message = new JSONObject();
			message.put("tweet", aMessage.getText());
			message.put("image", aMessage.getImage());
			message.put("isImage", aMessage.getIsImage());
			message.put("accessToken",System.getenv("SLACK_TOCKEN"));
			httppost.setEntity(new StringEntity(message.toString()));
			client.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
