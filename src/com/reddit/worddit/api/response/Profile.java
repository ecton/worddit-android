package com.reddit.worddit.api.response;

/**
 * Class to represent a user's profile.
 * @author OEP
 *
 */
public class Profile {
	/** User's ID */
	public String id;
	
	/** Nickname of the user */
	public String nickname;
	
	/** URL to user's avatar */
	public String avatar;
	
	/** Email of user. Can be null if we are not allowed to see it. */
	public String email;
}
