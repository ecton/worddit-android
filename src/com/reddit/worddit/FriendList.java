package com.reddit.worddit;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.reddit.worddit.adapters.FriendListAdapter;
import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Friend;

public class FriendList extends ListActivity implements APICallback {
	public static final String TAG = "FriendList";
	
	protected Friend[] mFriends;
	protected Session mSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.worddit_friend_list);

		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		fetchFriends();
	}

	private void fetchFriends() {
		APICall task = new APICall(this, mSession);
		task.getFriends();
	}

	@Override
	public void onCallComplete(boolean success, APICall task) {
		if(success) {
			mFriends = (Friend[]) task.getPayload();
			
			setListAdapter(new FriendListAdapter(this, mFriends, R.id.item_friend_email, R.id.item_friend_status));
		}
	}
}
