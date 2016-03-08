package com.heroku.devcenter.queue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heroku.devcenter.LiveJob;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class QueueFactory {

	final static Logger logger = LoggerFactory.getLogger(QueueFactory.class);

	private Channel channel;
	private String name;
	
	public QueueFactory(String aName) {
		name = aName;

		String uri = System.getenv("CLOUDAMQP_URL");
		if (logger.isDebugEnabled()) {
			logger.debug("connect queue " + uri);
		}

		ConnectionFactory factory = new ConnectionFactory();
		try {
			factory.setUri(uri);

			factory.setRequestedHeartbeat(30);
			factory.setConnectionTimeout(30);
			Connection connection = factory.newConnection();
			channel = connection.createChannel();

		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Boolean publish(byte[] aMessage) {
		if (logger.isDebugEnabled()) {
			logger.debug("send message " + aMessage);
		}
		try {
			channel.basicPublish("", name, null, aMessage);
			return Boolean.TRUE;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Boolean.FALSE;
	}

	public void close() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("close");
			}
			channel.close();
			channel = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
