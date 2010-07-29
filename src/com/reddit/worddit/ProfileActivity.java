package com.reddit.worddit;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	protected Session mSession;
	protected String mFriendStatus;
	protected String mAvatarUrl;
	protected boolean mInfoFetched = false;
	protected boolean mAvatarFetched = false;
	
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		this.setContentView(R.layout.activity_profile);
		fetchInfo();
	}
	
	protected void onSaveInstanceState(Bundle icicle) {
		saveToBundle(icicle);
	}
	
	protected void onRestoreInstanceState(Bundle icicle) {
		restoreFromBundle(icicle);
	}
	
	protected void setProfile(Profile p) {
		TextView title = (TextView) findViewById(R.id.profile_title);
		TextView subtitle = (TextView) findViewById(R.id.profile_subtitle);
		
		mAvatarUrl = p.avatar;
		
		if(p.nickname != null && p.nickname.length() > 0 && p.email != null) {
			title.setText(p.nickname);
			subtitle.setText(p.email);
		}
		else if(p.email != null) {
			title.setText(p.email);
			subtitle.setVisibility(View.GONE);
		}
		else {
			title.setText(R.string.label_no_nickname);
			subtitle.setVisibility(View.GONE);
		}
		
		// TODO: Friend status? Change the buttons?
	}
	
	protected void fetchInfo() {
		if(mInfoFetched == true) return;
		
		APICall task = new APICall(new APICallback() {
			@Override
			public void onCallComplete(boolean success, APICall task) {
				mInfoFetched = success;
				
				if(success) {
					setProfile((Profile) task.getPayload());
				}
			}
		}, mSession);
		mInfoFetched = true;
		
		Intent i = getIntent();
		task.findUser( i.getStringExtra(Constants.EXTRA_FRIENDID) );
	}

	private void saveToBundle(Bundle b) {
		if(b == null) return;
		
		TextView title = (TextView) findViewById(R.id.profile_title);
		TextView subtitle = (TextView) findViewById(R.id.profile_subtitle);
		
		b.putString(TITLE, title.getText().toString());
		b.putString(SUBTITLE, subtitle.getText().toString());
		b.putInt(SUBTITLE_STATUS, subtitle.getVisibility());
		b.putString(STATUS, mFriendStatus);
		b.putString(AVATAR_URL, mAvatarUrl);
		b.putBoolean(FETCHED_INFO, mInfoFetched);
		b.putBoolean(FETCHED_AVATAR, mAvatarFetched);
		
	}
	
	private void restoreFromBundle(Bundle b) {
		if(b == null) return;
		
		TextView title = (TextView) findViewById(R.id.profile_title);
		TextView subtitle = (TextView) findViewById(R.id.profile_subtitle);
		
		title.setText(b.getString(TITLE));
		subtitle.setText(b.getString(SUBTITLE));
		subtitle.setVisibility(b.getInt(SUBTITLE_STATUS));
		mFriendStatus = (b.containsKey(STATUS)) ? b.getString(STATUS) : mFriendStatus;
		mAvatarUrl = (b.containsKey(AVATAR_URL)) ? b.getString(AVATAR_URL) : mAvatarUrl;
		mInfoFetched = b.getBoolean(FETCHED_INFO);
		mAvatarFetched = b.getBoolean(FETCHED_AVATAR);
	}
	
	public static final String
		TITLE = "title",
		SUBTITLE = "subtitle",
		SUBTITLE_STATUS = "subtitle-status",
		STATUS = "status",
		AVATAR_URL = "avatar-url",
		FETCHED_INFO = "fetched-info",
		FETCHED_AVATAR = "fetched-avatar";
}
