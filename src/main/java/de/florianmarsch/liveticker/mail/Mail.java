package de.florianmarsch.liveticker.mail;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;

import de.florianmarsch.liveticker.slack.SlackConnection;

import com.sendgrid.SendGridException;

public class Mail {

	private Email email;
	
	public Mail(Integer aGameday) {
		String html = loadFile("http://fussballmanager.herokuapp.com/process/" + aGameday);
		
		email = new Email();
		String target = System.getenv("SENDGRID_TARGET");
		email.addTo(target);
		email.setFrom("Liveticker");
		email.setSubject("Spieltag "+ aGameday);
		email.setHtml(html);


	}
	
	public void send(){
		try {
			String name = System.getenv("SENDGRID_USERNAME");
			String pw = System.getenv("SENDGRID_PASSWORD");
			SendGrid sendgrid = new SendGrid(name, pw);
			SendGrid.Response response = sendgrid.send(email);
		} catch (SendGridException e) {
			new SlackConnection().handleException(e);
			e.printStackTrace();
		}
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
			new SlackConnection().handleException(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			new SlackConnection().handleException(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempReturn.toString();
	}
}
