package com.reddit.worddit;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	protected Session mSession;
	
	// These fields need to be restorable by Bundle
	protected int mLayoutState = STATE_LOADING;
	protected Profile mProfile;
	protected int mMessageId;
	
	
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		this.setContentView(R.layout.activity_profile);
		setupListeners();
		
		if(icicle == null) {
			showFetching();
			fetchInfo();
		}
		else {
			restoreFromBundle(icicle);
			
			// Orientation changes kill the thread, so the best thing
			// we can do is just to start the damn thing over.
			if(mLayoutState == STATE_LOADING) {
				fetchInfo();
			}
		}
	}
	
	protected void onSaveInstanceState(Bundle icicle) {
		saveToBundle(icicle);
	}
	
	protected void onRestoreInstanceState(Bundle icicle) {
		restoreFromBundle(icicle);
	}
	
	protected void showFetching() {
		mLayoutState = STATE_LOADING;
		findViewById(R.id.profile_bodyContainer).setVisibility(View.GONE);
		findViewById(R.id.profile_messageContainer).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_progressBar).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_btnRetry).setVisibility(View.GONE);
		
		TextView msg = (TextView) findViewById(R.id.profile_message);
		msg.setText(R.string.label_loading);
	}
	
	protected void showMessage(int resId) {
		mLayoutState = STATE_MESSAGE;
		mMessageId = resId;
		findViewById(R.id.profile_bodyContainer).setVisibility(View.GONE);
		findViewById(R.id.profile_messageContainer).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_progressBar).setVisibility(View.GONE);
		findViewById(R.id.profile_btnRetry).setVisibility(View.VISIBLE);
		
		TextView msg = (TextView) findViewById(R.id.profile_message);
		msg.setText(resId);
	}
	
	protected void showProfile(Profile p) {
		mLayoutState = STATE_PROFILE;
		TextView title = (TextView) findViewById(R.id.profile_title);
		TextView subtitle = (TextView) findViewById(R.id.profile_subtitle);
		
		mProfile = p;
		
		if(p.nickname != null && p.nickname.length() > 0 && p.email != null) {
			title.setText(p.nickname);
			subtitle.setText(p.email);
		}
		else if(p.email != null) {
			title.setText(p.email);
			subtitle.setVisibility(View.GONE);
		}
		else if(p.nickname != null && p.nickname.length() > 0) {
			title.setText(p.nickname);
			subtitle.setVisibility(View.GONE);
		}
		else {
			title.setText(R.string.label_no_nickname);
			subtitle.setVisibility(View.GONE);
		}
		
		findViewById(R.id.profile_bodyContainer).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_messageContainer).setVisibility(View.GONE);
		
		// TODO: Friend status? Change the buttons?
	}
	
	protected void fetchInfo() {
		if(mProfile != null) return;
		
		APICall task = new APICall(new APICallback() {
			@Override
			public void onCallComplete(boolean success, APICall task) {
				if(success) {
					showProfile((Profile) task.getPayload());
				}
				else {
					showMessage(task.getMessage());
				}
			}
		}, mSession);
		
		Intent i = getIntent();
		task.findUser( i.getStringExtra(Constants.EXTRA_FRIENDID) );
	}

	private void saveToBundle(Bundle b) {
		if(b == null) return;
		b.putParcelable(PROFILE, mProfile);
		b.putInt(MSGID, mMessageId);
		b.putInt(LAYOUT_STATE, mLayoutState);
	}
	
	private void restoreFromBundle(Bundle b) {
		if(b == null) return;
		mProfile = (Profile) b.getParcelable(PROFILE);
		mMessageId = b.getInt(MSGID);
		mLayoutState = b.getInt(LAYOUT_STATE);
	
		switch(mLayoutState) {
		case STATE_LOADING: showFetching(); break;
		case STATE_MESSAGE: showMessage(mMessageId); break;
		case STATE_PROFILE: showProfile(mProfile); break;
		default: showMessage(R.string.msg_internal_error); break;
		}
	}
	
	private void setupListeners() {
		findViewById(R.id.profile_btnRetry).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProfileActivity.this.showFetching();
				ProfileActivity.this.fetchInfo();
			}
		});
	}
	
	public static final int
		STATE_LOADING = 1,
		STATE_MESSAGE = STATE_LOADING + 1,
		STATE_PROFILE = STATE_LOADING + 2;
	
	public static final String
		LAYOUT_STATE = "layout-state",
		PROFILE = "profile",
		MSGID = "msg-id";
}
