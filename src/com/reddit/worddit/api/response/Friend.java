package com.reddit.worddit.api.response;

/**
 * A class to represent your friend.
 * [As if you have any.]
 * @author OEP
 *
 */
public class Friend {
	/** The ID of this friend */
	public String id;
	
	/** Status of friendship */
	public String status;
	
	/**
	 * Checks to see if this friendship is // TODO: Did we request or they request?
	 * @return
	 */
	public boolean isRequested() {
		return status.equals(STATUS_REQUESTED);
	}
	
	/**
	 * Checks to see if this friendship is // TODO: Is our request or their request pending?
	 * @return
	 */
	public boolean isPending() {
		return status.equals(STATUS_PENDING);
	}
	
	/**
	 * Checks to see if this friendship is active.
	 * @return true if this friendship is active
	 */
	public boolean isActive() {
		return status.equals(STATUS_ACTIVE);
	}
	
	/** Constants posed by the Worddit server */
	public static final String
		STATUS_REQUESTED = "requested",
		STATUS_PENDING = "pending",
		STATUS_ACTIVE = "active";
}
