package com.reddit.worddit;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
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
		case R.id.friend_add:
			Intent intent = new Intent(this, com.reddit.worddit.FriendFind.class);
			intent.putExtra(Constants.EXTRA_SESSION, mSession);
		    startActivityForResult(intent, FIND_FRIEND);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
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
		menu.setHeaderTitle(mFriends[((AdapterContextMenuInfo) menuInfo).position].email);
		if(mFriends[((AdapterContextMenuInfo) menuInfo).position].isRequested()) {
			inflater.inflate(R.menu.friend_request_menu, menu);
		} else if (mFriends[((AdapterContextMenuInfo) menuInfo).position].isActive()) {
			inflater.inflate(R.menu.friend_active_menu, menu);
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.friend_accept:
				new APICall(this, mSession).acceptFriend(mFriends[info.position].id);
				return true;
			case R.id.friend_reject:
				new APICall(this, mSession).rejectFriend(mFriends[info.position].id);
				return true;
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

	private void fetchFriends() {
		new APICall(this, mSession).getFriends();
	}

	@Override
	public void onCallComplete(boolean success, APICall task) {
		if(success) {
			if(task.getCall() == APICall.USER_FRIENDS) {
				mFriends = (Friend[]) task.getPayload();
			
				setListAdapter(new FriendListAdapter(this, mSession, R.id.item_friend_email, R.id.item_friend_status));
			} else if(task.getCall() == APICall.USER_ACCEPTFRIEND || task.getCall() == APICall.USER_DEFRIEND) {
				new APICall(this, mSession).getFriends(); // Way too inefficient, but temporarily gets the job done.
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    switch (requestCode) {
	    	case ADD_FRIEND:
	    		if (resultCode == RESULT_CANCELED) {
	                Log.i("FriendList.java", "Add Friend Failed");
	                Toast.makeText(getApplicationContext(), "Add Friend Failed", Toast.LENGTH_SHORT).show();
	            } else {
	    			new APICall(this, mSession).getFriends(); // Way too inefficient, but temporarily gets the job done.
	            } break;
	    	case FIND_FRIEND:
	            if (resultCode == RESULT_CANCELED) {
	                Log.i("FriendList.java", "Find Friend Failed");
	                Toast.makeText(getApplicationContext(), "Invalid E-mail", Toast.LENGTH_SHORT).show();
	            } else {
	            	Bundle b = data.getBundleExtra("com.reddit.worddit.Profile");
	            	
	            	Log.i("FriendList.java", "Found Friend: " + b.getString("nickname"));
	            	
	            	Intent intent = new Intent(this, com.reddit.worddit.FriendAdd.class);
	    			intent.putExtra(Constants.EXTRA_SESSION, mSession);
	    			intent.putExtra("com.reddit.worddit.ProfileID", b.getString("id"));
	    		    startActivityForResult(intent, ADD_FRIEND);
	            } break;
	        default:
	            break;
	    }
	}
}
