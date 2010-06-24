package com.reddit.worddit.api.response;


/**
 * Message class to represent a chat message from a particular user
 * as returned by the Worddit server.
 * @author OEP
 *
 */
public class ChatMessage {
	/** User who sent the message */
	public String user;
	
	/** Human language message sent by the user */
	public String message;
	
	/** Date the message was sent */
	public String date;
}
