package com.reddit.worddit;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

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
		registerForContextMenu(getListView());

		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		fetchFriends();
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
            						ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.friend_menu, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.friend_accept:
				new APICall(this, mSession).acceptFriend(new String[]{mFriends[info.position].id});
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void fetchFriends() {
		new APICall(this, mSession).getFriends();
	}

	@Override
	public void onCallComplete(boolean success, APICall task) {
		if(success) {
			if(task.getCall() == APICall.USER_FRIENDS) {
				mFriends = (Friend[]) task.getPayload();
			
				setListAdapter(new FriendListAdapter(this, mFriends, R.id.item_friend_email, R.id.item_friend_status));
			} else if(task.getCall() == APICall.USER_ACCEPTFRIEND) {
				new APICall(this, mSession).getFriends(); // Way too inefficient, but temporarily gets the job done.
			}
		}
	}
}
