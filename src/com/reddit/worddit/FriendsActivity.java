package com.reddit.worddit;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.reddit.worddit.adapters.FriendListAdapter;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Friend;

public class FriendsActivity extends ListActivity {
	public static final String TAG = "FriendList";
	
	protected Friend[] mFriends;
	protected Session mSession;
	
	private static final int FIND_FRIEND	= 1,
							 ADD_FRIEND		= 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.friends_options_menu, menu);
		return true;
	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.friend_search:
			findViewById(R.id.friends_searchpane).setVisibility(View.VISIBLE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		registerForContextMenu(getListView());

		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		setListAdapter(new FriendListAdapter(this, mSession, R.id.item_friend_email, R.id.item_friend_status));
		setupListeners();
	}

	public Friend getFriendAt(int n) {
		return (Friend)getListAdapter().getItem(n);
	}
	
	private void setupListeners() {
		findViewById(R.id.friends_searchButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: Launch ProfileActivity when it gets made.
			}
		});
		
		findViewById(R.id.friends_cancelButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView term = (TextView) findViewById(R.id.friends_searchTerm);
				findViewById(R.id.friends_searchpane).setVisibility(View.GONE);
				term.setText("");
				// TODO: Remove any filter that may exist.bob
			}
		});
		
		findViewById(R.id.friends_searchTerm).setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO: Update filter for friend list when it gets done.
				return false;
			}
		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// If a key event doesn't get handled, pass it off to the search
		// view by default.
		
		// TODO: Maybe add numerics as well? I couldn't justify it yet.
		if(keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
			findViewById(R.id.friends_searchpane).setVisibility(View.VISIBLE);
			findViewById(R.id.friends_searchTerm).requestFocus();
			findViewById(R.id.friends_searchTerm).dispatchKeyEvent(event);
		}
		return false;
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
            						ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		menu.setHeaderTitle(getFriendAt(((AdapterContextMenuInfo) menuInfo).position).email);
		if(getFriendAt(((AdapterContextMenuInfo) menuInfo).position).isRequested()) {
			inflater.inflate(R.menu.friend_request_menu, menu);
		} else if (getFriendAt(((AdapterContextMenuInfo) menuInfo).position).isActive()) {
			inflater.inflate(R.menu.friend_active_menu, menu);
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		FriendListAdapter list = (FriendListAdapter) getListAdapter();
		switch (item.getItemId()) {
			case R.id.friend_accept:
				list.acceptFriend(info.position);
				return true;
			case R.id.friend_reject:
				list.removeFriend(info.position);
				return true;
			case R.id.friend_remove:
				list.removeFriend(info.position);
			case R.id.friend_game_request:
				// TODO: Request game
				return true;
			case R.id.friend_message:
				//TODO: Message friend
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
}
