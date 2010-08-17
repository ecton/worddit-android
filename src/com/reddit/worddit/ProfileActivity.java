package com.reddit.worddit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Profile;

/**
 * This activity's purpose is to display the profile
 * of a Worddit user. The data is obtained by making an
 * API call to GET /user/find/:id_or_email, where :id_or_email can be the
 * unique identifier for that user or the email address for the user.
 * 
 * The user ID or email is passed via an Intent extra with the key
 * <code>Constants.EXTRA_FRIENDID</code>.
 * @author pkilgo
 *
 */
public class ProfileActivity extends Activity {

	/** The Session we will use to make our API calls. */
	protected Session mSession;
	
	// These fields need to be restorable by Bundle
	
	/** Holds the state of the Layout for this activity. */
	protected int mLayoutState = STATE_LOADING;
	
	/** Holds the state of the avatar */
	protected int mAvatarState = STATE_UNFETCHED;
	
	/** Holds the profile information of the user we want to display */
	protected Profile mProfile;
	
	/** Holds the message ID in case there was an error message */
	protected int mMessageId;
	
	/** Holds the bitmap data of the avatar */
	protected Bitmap mAvatar;
	
	/**
	 * Creates the activity.
	 */
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		// Restore the Session
		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		// Set up the basic UI.
		this.setContentView(R.layout.activity_profile);
		setupListeners();
		
		// See if we need to restore anything.
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
	
	/**
	 * Saves the state of this activity.
	 */
	protected void onSaveInstanceState(Bundle icicle) {
		saveToBundle(icicle);
	}
	
	/**
	 * Restores the state of this activity.
	 * TODO: See if we are duplicating functionality provided in onCreate(Bundle)
	 */
	protected void onRestoreInstanceState(Bundle icicle) {
		restoreFromBundle(icicle);
	}
	
	/**
	 * Sets up the Activity's layout to tell the user we
	 * are fetching the profile information.
	 */
	protected void showFetching() {
		mLayoutState = STATE_LOADING;
		findViewById(R.id.profile_bodyContainer).setVisibility(View.GONE);
		findViewById(R.id.profile_messageContainer).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_progressBar).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_btnRetry).setVisibility(View.GONE);
	
		TextView msg = (TextView) findViewById(R.id.profile_message);
		msg.setText(R.string.label_loading);
	}

	/**
	 * Sets up the Activity's layout to display some message to the user.
	 * @param resId String resource displayed to the user.
	 */
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
	
	/**
	 * Sets up the Activity's layout to show the loaded profile to the user.
	 * @param p the Profile to display
	 */
	protected void showProfile(Profile p) {
		// Cache the profile and change the layout state.
		mProfile = p;
		mLayoutState = STATE_PROFILE;
		
		TextView title = (TextView) findViewById(R.id.profile_title);
		TextView subtitle = (TextView) findViewById(R.id.profile_subtitle);
		
		// If we have a nickname and email show the nickname as the main title
		// and the email as the subtitle.
		if(p.nickname != null && p.nickname.length() > 0 && p.email != null) {
			title.setText(p.nickname);
			subtitle.setText(p.email);
		}
		// If we just have an email, show it.
		else if(p.email != null) {
			title.setText(p.email);
			subtitle.setVisibility(View.GONE);
		}
		// If we just have a nickname, show it.
		else if(p.nickname != null && p.nickname.length() > 0) {
			title.setText(p.nickname);
			subtitle.setVisibility(View.GONE);
		}
		// Ideally this shouldn't happen, but this will show the user
		// that no email or nickname was provided.
		else {
			title.setText(R.string.label_no_nickname);
			subtitle.setVisibility(View.GONE);
		}
		
		// Get rid of the other possible layout styles.
		findViewById(R.id.profile_bodyContainer).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_messageContainer).setVisibility(View.GONE);

		// Sets up the buttons so we can defriend this person
		if(p.isActive()) {
			setDefriend();
		}
		// Hides the buttons because no action can be taken while friendship is pending.
		else if(p.isPending()) {
			setPending();
		}
		// Shows Accept/Reject button since our friendship has been requested.
		else if(p.isRequested()) {
			setAccept();
		}
		// Shows an option to request friendship from this non-friend person.
		else if(p.isUnrequested()) {
			setRequest();
		}
		
		// Decides if we need to fetch the avatar or not.
		if(mAvatarState == STATE_FETCHED) {
			showAvatar(mAvatar);
		}
		else {
			fetchAvatar();
		}
	}
	
	/**
	 * Removes the throbber and shows the avatar.
	 * If <code>avatar</code> is null, the default "no avatar found"
	 * image will be shown instead.
	 * @param avatar the user's avatar to show.
	 */
	protected void showAvatar(Bitmap avatar) {
		mAvatar = avatar;
		mAvatarState = STATE_FETCHED;
		
		ImageView iv = (ImageView)  findViewById(R.id.profile_avatar);
		iv.setVisibility(View.VISIBLE);
		findViewById(R.id.profile_avatarLoader).setVisibility(View.GONE);
		
		// Decides if we should show the default "no avatar found" drawable.
		if(avatar != null) {
			iv.setImageBitmap(avatar);
		}
		else {
			iv.setImageResource(R.drawable.ic_contact_picture);
		}
	}
	
	/**
	 * Sets up the buttons so that we can defriend the user
	 * whose profile we are viewing.
	 */
	protected void setDefriend() {
		final Button main = (Button) findViewById(R.id.profile_btnFriendAction);
		final Button aux = (Button) findViewById(R.id.profile_btnAuxFriendAction);
		final TextView status = (TextView) findViewById(R.id.profile_friendStatus);
		
		// We only need one button
		main.setVisibility(View.VISIBLE);
		aux.setVisibility(View.GONE);
		
		// Set up appropriate labels.
		main.setEnabled(true);
		main.setText(R.string.label_defriend);
		status.setText(R.string.msg_friendshipActive);
		
		// Set up the listener.
		main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				main.setEnabled(false);
				main.setText(R.string.label_working);
				
				APICall task = new APICall(
					new APICallback() {
						@Override
						public void onCallComplete(boolean success, APICall task) {
							// If we succeeded, clear the status because they
							// are now an un-friend.
							if(success) {
								mProfile.status = null;
							}
							// Show whatever error in a Toast.
							else {
								Toast.makeText(
										ProfileActivity.this, task.getMessage(), Toast.LENGTH_LONG).show();
							}
							showProfile(mProfile);
						}
					},
					mSession);
				
				// Perform the API call.
				task.rejectFriend(mProfile.id);
			}
		});
	}
	
	/**
	 * Hides the friend action buttons as if a friendship were pending.
	 */
	protected void setPending() {
		final Button main = (Button) findViewById(R.id.profile_btnFriendAction);
		final Button aux = (Button) findViewById(R.id.profile_btnAuxFriendAction);
		final TextView status = (TextView) findViewById(R.id.profile_friendStatus);
		
		main.setVisibility(View.GONE);
		aux.setVisibility(View.GONE);
		status.setText(R.string.msg_friendshipPending);
	}
	
	/**
	 * Shows the "Request friend" button and sets up listeners.
	 */
	protected void setRequest() {
		final Button main = (Button) findViewById(R.id.profile_btnFriendAction);
		final Button aux = (Button) findViewById(R.id.profile_btnAuxFriendAction);
		final TextView status = (TextView) findViewById(R.id.profile_friendStatus);
		
		// Only one button needed.
		main.setVisibility(View.VISIBLE);
		aux.setVisibility(View.GONE);
		
		main.setEnabled(true);
		main.setText(R.string.label_request_friend);
		status.setText(R.string.msg_friendshipInactive);
		
		// Set up listener for the button.
		main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				main.setEnabled(false);
				main.setText(R.string.label_working);
				
				APICall task = new APICall(
					new APICallback() {
						@Override
						public void onCallComplete(boolean success, APICall task) {
							// Set the cached profile to appropriate state.
							if(success) {
								mProfile.status = Profile.STATUS_PENDING;
							}
							// Show an error message.
							else {
								Toast.makeText(
										ProfileActivity.this, task.getMessage(), Toast.LENGTH_LONG).show();
							}
							showProfile(mProfile);
						}
					},
					mSession);
				
				// Perform the API call.
				task.addFriend(mProfile.id);
			}
		});
	}
	
	/**
	 * Sets up the friend action buttons to accept a friendship.
	 */
	protected void setAccept() {
		final Button main = (Button) findViewById(R.id.profile_btnFriendAction);
		final Button aux = (Button) findViewById(R.id.profile_btnAuxFriendAction);
		final TextView status = (TextView) findViewById(R.id.profile_friendStatus);
		
		// We will need both buttons for this.
		main.setVisibility(View.VISIBLE);
		aux.setVisibility(View.VISIBLE);
		main.setEnabled(true);
		aux.setEnabled(true);
		
		main.setText(R.string.label_accept);
		aux.setText(R.string.label_reject);
		status.setText(R.string.msg_friendshipRequested);
		
		// The main button will handle the accept action.
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
		
		// The auxiliary button will handle the decline action.
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
	
	/**
	 * Fetches the profile information for the friend ID specified in the Intent.
	 */
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
		
		// Find the friend!!
		Intent i = getIntent();
		task.findUser( i.getStringExtra(Constants.EXTRA_FRIENDID) );
	}
	
	/**
	 * Creates a thread that will fetch the avatar and sets up the UI to let
	 * the user know we are fetching the avatar in the background.
	 */
	protected void fetchAvatar() {
		// Hide the ImageView and show the ProgressView.
		findViewById(R.id.profile_avatarLoader).setVisibility(View.VISIBLE);
		findViewById(R.id.profile_avatar).setVisibility(View.GONE);
		
		FetchAvatarTask task = new FetchAvatarTask();
		task.execute(mProfile.avatar);
	}

	/**
	 * Save this Activity's state to a bundle.
	 * @param b the bundle to save our state to.
	 */
	private void saveToBundle(Bundle b) {
		if(b == null) return;
		b.putParcelable(PROFILE, mProfile);
		b.putInt(MSGID, mMessageId);
		b.putInt(LAYOUT_STATE, mLayoutState);
		b.putParcelable(AVATAR, mAvatar);
		b.putInt(AVATAR_STATE, mAvatarState);
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
	
	/**
	 * This AsyncTask's only function is the lazy-load the avatar for the user.
	 * It will call ProfileActivity.showAvatar() once finished.
	 * @author pkilgo
	 *
	 */
	class FetchAvatarTask extends AsyncTask<String, Integer, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... location) {
			if(location == null || location.length == 0) {
				return null;
			}
			
			return mSession.fetchAvatar(location[0]);
		}

		protected void onPostExecute(Bitmap result) {
			ProfileActivity.this.showAvatar(result);
		}
	}
	
	/**
	 * Sets up necessary listeners for various Views in this Activity.
	 */
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
