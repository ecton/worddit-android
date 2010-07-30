package com.reddit.worddit;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.reddit.worddit.adapters.FriendListAdapter;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Friend;

public class FriendsActivity extends ListActivity {
	public static final String TAG = "FriendList";
	
	protected Friend[] mFriends;
	protected Session mSession;
	
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
	
	protected void onListItemClick(ListView list, View v, int position, long id) {
		Friend f = (Friend) list.getItemAtPosition(position);
		showProfile(f);
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
	
	protected void updateFilter() {
		FriendListAdapter adapter = (FriendListAdapter) getListAdapter();
		String filter = getSearchTerm();
		adapter.setFilter(filter);
	}
	
	protected void showProfile(Friend friend) {
		showProfile(friend.id);
	}
	
	protected void showProfile(String id) {
		Intent i = new Intent(FriendsActivity.this, ProfileActivity.class);
		i.putExtra(Constants.EXTRA_SESSION, mSession);
		i.putExtra(Constants.EXTRA_FRIENDID, id);
		startActivity(i);
		// TODO: How do we know if in ProfileActivity they will change the friend??
	}

	public Friend getFriendAt(int n) {
		return (Friend)getListAdapter().getItem(n);
	}
	
	public String getSearchTerm() {
		EditText tv = (EditText) findViewById(R.id.friends_searchTerm);
		return tv.getEditableText().toString();
	}
	
	private void setupListeners() {
		findViewById(R.id.friends_searchButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et = (EditText) findViewById(R.id.friends_searchTerm);
				String term = et.getEditableText().toString();
				showProfile(term);
			}
		});
		
		findViewById(R.id.friends_cancelButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView term = (TextView) findViewById(R.id.friends_searchTerm);
				FriendListAdapter adapter = (FriendListAdapter) getListAdapter();
				findViewById(R.id.friends_searchpane).setVisibility(View.GONE);
				term.setText("");
				adapter.setFilter("");
			}
		});
		
		EditText text = (EditText) findViewById(R.id.friends_searchTerm);
		text.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable text) {
				updateFilter();	
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int counter, int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
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
