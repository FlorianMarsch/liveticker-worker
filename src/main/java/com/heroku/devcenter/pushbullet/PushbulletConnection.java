package com.heroku.devcenter.pushbullet;
import java.io.IOException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

public class PushbulletConnection {

	private String api_key;
	private String channel;
	private CloseableHttpClient client;
	
	public PushbulletConnection(){
		api_key  = System.getenv("PUSHBULLET_ACCESS_TOKEN");
		channel = System.getenv("PUSHBULLET_CHANNEL_TAG");
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("api.pushbullet.com", 443),
				new UsernamePasswordCredentials(api_key, null));
		client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

	}
	

	public void send(String aMessage) {
		try {
			HttpPost httppost = new HttpPost("https://api.pushbullet.com/v2/pushes");
			httppost.setHeader("Content-Type", "application/json");
			JSONObject message = new JSONObject();
			message.put("type", "note");
			message.put("body", aMessage);
			message.put("channel_tag", channel);
			httppost.setEntity(new StringEntity(message.toString()));
			client.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
