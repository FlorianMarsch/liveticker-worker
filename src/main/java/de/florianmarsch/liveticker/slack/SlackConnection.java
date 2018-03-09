package de.florianmarsch.liveticker.slack;

import java.io.PrintWriter;
import java.io.StringWriter;

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
	
	
	public void handleException(Exception anException) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			anException.printStackTrace(pw);
			String errorMessage = sw.toString();
			
			
			HttpPost httppost = new HttpPost("http://social-system-api.herokuapp.com/api/slack");
			httppost.setHeader("Content-Type", "application/json");
			JSONObject message = new JSONObject();
			message.put("tweet",errorMessage);
			message.put("accessToken",System.getenv("SLACK_TOCKEN"));
			message.put("channel","#error");
			httppost.setEntity(new StringEntity(message.toString()));
			client.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			message.put("channel",System.getenv("SLACK_CHANNEL"));
			httppost.setEntity(new StringEntity(message.toString()));
			client.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
