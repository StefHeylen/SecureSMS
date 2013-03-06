package be.khleuven.kortlevenheylen.securesms.model;

import java.util.Date;

public class Message {

	Date date;
	String sender, body;
	
	public Message(Date date, String sender, String body) {
		this.setDate(date);
		this.setSender(sender);
		this.setBody(body);
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
