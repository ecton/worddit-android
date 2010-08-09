package com.reddit.worddit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	protected Session mSession;
	
	// These fields need to be restorable by Bundle
	protected int mLayoutState = STATE_LOADING;
	protected int mAvatarState = STATE_UNFETCHED;
	protected Profile mProfile;
	protected int mMessageId;
	protected Bitmap mAvatar;
	
	
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

		if(p.isActive()) {
			setDefriend();
		}
		else if(p.isPending()) {
			setPending();
		}
		else if(p.isRequested()) {
			setAccept();
		}
		else if(p.isUnrequested()) {
			setRequest();
		}
		
		if(mAvatarState == STATE_FETCHED) {
			showAvatar(mAvatar);
		}
		else {
			fetchAvatar();
		}
	}
	
	protected void showAvatar(Bitmap avatar) {
		mAvatar = avatar;
		mAvatarState = STATE_FETCHED;
		
		ImageView iv = (ImageView)  findViewById(R.id.profile_avatar);
		iv.setVisibility(View.VISIBLE);
		findViewById(R.id.profile_avatarLoader).setVisibility(View.GONE);
		
		if(avatar != null) {
			iv.setImageBitmap(avatar);
		}
		else {
			iv.setImageResource(R.drawable.ic_contact_picture);
		}
	}
	
	protected void setDefriend() {
		final Button main = (Button) findViewById(R.id.profile_btnFriendAction);
		final Button aux = (Button) findViewById(R.id.profile_btnAuxFriendAction);
		final TextView status = (TextView) findViewById(R.id.profile_friendStatus);
		
		main.setVisibility(View.VISIBLE);
		aux.setVisibility(View.GONE);
		
		main.setEnabled(true);
		main.setText(R.string.label_defriend);
		status.setText(R.string.msg_friendshipActive);
		
		main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				main.setEnabled(false);
				main.setText(R.string.label_working);
				
				APICall task = new APICall(
					new APICallback() {
						@Override
						public void onCallComplete(boolean success, APICall task) {
							if(success) {
								mProfile.status = null;
							}
							else {
								Toast.makeText(
										ProfileActivity.this, task.getMessage(), Toast.LENGTH_LONG).show();
							}
							showProfile(mProfile);
						}
					},
					mSession);
				task.rejectFriend(mProfile.id);
			}
		});
	}
	
	protected void setPending() {
		final Button main = (Button) findViewById(R.id.profile_btnFriendAction);
		final Button aux = (Button) findViewById(R.id.profile_btnAuxFriendAction);
		final TextView status = (TextView) findViewById(R.id.profile_friendStatus);
		
		main.setVisibility(View.GONE);
		aux.setVisibility(View.GONE);
		status.setText(R.string.msg_friendshipPending);
	}
	
	protected void setRequest() {
		final Button main = (Button) findViewById(R.id.profile_btnFriendAction);
		final Button aux = (Button) findViewById(R.id.profile_btnAuxFriendAction);
		final TextView status = (TextView) findViewById(R.id.profile_friendStatus);
		
		main.setVisibility(View.VISIBLE);
		aux.setVisibility(View.GONE);
		
		main.setEnabled(true);
		main.setText(R.string.label_request_friend);
		status.setText(R.string.msg_friendshipInactive);
		
		main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				main.setEnabled(false);
				main.setText(R.string.label_working);
				
				APICall task = new APICall(
					new APICallback() {
						@Override
						public void onCallComplete(boolean success, APICall task) {
							if(success) {
								mProfile.status = Profile.STATUS_PENDING;
							}
							else {
								Toast.makeText(
										ProfileActivity.this, task.getMessage(), Toast.LENGTH_LONG).show();
							}
							showProfile(mProfile);
						}
					},
					mSession);
				task.addFriend(mProfile.id);
			}
		});
	}
	
	protected void setAccept() {
		final Button main = (Button) findViewById(R.id.profile_btnFriendAction);
		final Button aux = (Button) findViewById(R.id.profile_btnAuxFriendAction);
		final TextView status = (TextView) findViewById(R.id.profile_friendStatus);
		
		main.setVisibility(View.VISIBLE);
		aux.setVisibility(View.VISIBLE);
		main.setEnabled(true);
		aux.setEnabled(true);
		
		main.setText(R.string.label_accept);
		aux.setText(R.string.label_reject);
		status.setText(R.string.msg_friendshipRequested);
		
		main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aux.setVisibility(View.GONE);
				main.setEnabled(false);
				main.setText(R.string.label_working);
				
				APICall task = new APICall(
					new APICallback() {
						@Override
						public void onCallComplete(boolean success, APICall task) {
							if(success) {
								mProfile.status = Profile.STATUS_ACTIVE;
							}
							else {
								Toast.makeText(
										ProfileActivity.this, task.getMessage(), Toast.LENGTH_LONG).show();
							}
							showProfile(mProfile);
						}
					},
					mSession);
				task.acceptFriend(mProfile.id);
			}
		});
		
		aux.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				main.setVisibility(View.GONE);
				aux.setEnabled(false);
				aux.setText(R.string.label_working);
				
				APICall task = new APICall(
					new APICallback() {
						@Override
						public void onCallComplete(boolean success, APICall task) {
							if(success) {
								mProfile.status = null;
							}
							else {
								Toast.makeText(
										ProfileActivity.this, task.getMessage(), Toast.LENGTH_LONG).show();
							}
							showProfile(mProfile);
						}
					},
					mSession);
				task.rejectFriend(mProfile.id);
			}
		});
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
	
	protected void fetchAvatar() {
		findViewById(R.id.profile_avatarLoader).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_avatar).setVisibility(View.GONE);
		
		FetchAvatarTask task = new FetchAvatarTask();
		task.execute(mProfile.avatar);
	}

	private void saveToBundle(Bundle b) {
		if(b == null) return;
		b.putParcelable(PROFILE, mProfile);
		b.putInt(MSGID, mMessageId);
		b.putInt(LAYOUT_STATE, mLayoutState);
		b.putParcelable(AVATAR, mAvatar);
		b.putInt(AVATAR_STATE, mAvatarState);
	}
	
	class FetchAvatarTask extends AsyncTask<String, Integer, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... location) {
			try {
				location[0] = "http://www.osnn.net/customavatars/avatar128467_1.gif";
				URL url = new URL(location[0]);
				Bitmap bm = BitmapFactory.decodeStream(url.openStream());
				return bm;
			}
			catch (MalformedURLException e) { }
			catch (IOException e) { }
			
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			ProfileActivity.this.showAvatar(result);
		}
	}
	
	private void restoreFromBundle(Bundle b) {
		if(b == null) return;
		mProfile = (Profile) b.getParcelable(PROFILE);
		mMessageId = b.getInt(MSGID);
		mLayoutState = b.getInt(LAYOUT_STATE);
		mAvatar = (Bitmap) b.getParcelable(AVATAR);
		mAvatarState = b.getInt(AVATAR_STATE);
	
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
	
	/** State for how the UI should look. */
	public static final int
		STATE_LOADING = 1,
		STATE_MESSAGE = STATE_LOADING + 1,
		STATE_PROFILE = STATE_LOADING + 2;
	
	/** State for the fetchedness of the avatar */
	public static final int
		STATE_UNFETCHED = 1,
		STATE_FETCHED = STATE_UNFETCHED + 1;
	
	public static final String
		LAYOUT_STATE = "layout-state",
		PROFILE = "profile",
		MSGID = "msg-id",
		AVATAR = "avatar",
		AVATAR_STATE = "avatar-state";
}
