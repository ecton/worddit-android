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
	
	/** Status of friendship */
	public String status;
	
	/**
	 * Checks to see if this friendship is // TODO: Did we request or they request?
	 * @return
	 */
	public boolean isRequested() {
		return STATUS_REQUESTED.equalsIgnoreCase(status);
	}
	
	/**
	 * Checks to see if this friendship is // TODO: Is our request or their request pending?
	 * @return
	 */
	public boolean isPending() {
		return STATUS_PENDING.equalsIgnoreCase(status);
	}
	
	/**
	 * Checks to see if this friendship is active.
	 * @return true if this friendship is active
	 */
	public boolean isActive() {
		return STATUS_ACTIVE.equalsIgnoreCase(status);
	}
	
	/**
	 * Checks to see if this person has not been requested to be a friend.
	 * @return true if this person has no friend status
	 */
	public boolean isUnrequested() {
		return status == null || status.length() == 0;
	}
	
	/** Constants posed by the Worddit server */
	public static final String
		STATUS_REQUESTED = "requested",
		STATUS_PENDING = "pending",
		STATUS_ACTIVE = "active";
}
