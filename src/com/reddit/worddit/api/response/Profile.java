package com.reddit.worddit.api.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to represent a user's profile.
 * @author OEP
 *
 */
public class Profile implements Parcelable {
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
	 * Default constructor.
	 * GSON requires a no-args constructor for automatic casting.
	 */
	public Profile() {
		
	}
	
	/**
	 * Construct a Profile from a Parcel.
	 * @param in the parcel to construct from
	 */
	private Profile(Parcel in) {
		id = in.readString();
		nickname = in.readString();
		avatar = in.readString();
		email = in.readString();
		status = in.readString();
	}
	
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(nickname);
		dest.writeString(avatar);
		dest.writeString(email);
		dest.writeString(status);
	}
	
	public static final Parcelable.Creator<Profile> CREATOR
		= new Parcelable.Creator<Profile>() {
			@Override
			public Profile createFromParcel(Parcel source) {
				return new Profile(source);
			}

			@Override
			public Profile[] newArray(int size) {
				return new Profile[size];
			}
		};
}
