package com.reddit.worddit.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reddit.worddit.R;
import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Friend;

public class FriendListAdapter extends SessionListAdapter {
	protected Friend[] mFriends;
	private int mEmailField, mStatusField; 
	
	public FriendListAdapter(Context ctx, Session session) {
		this(ctx,session,R.id.item_friend_email, R.id.item_friend_status);
	}
	
	public FriendListAdapter(Context ctx, Session session,
			int emailField, int statusField) {
		super(ctx, session);
		mEmailField = emailField;
		mStatusField = statusField;
	}

	@Override
	public int getItemCount() {
		return (mFriends == null) ? 0 : mFriends.length;
	}

	@Override
	public Friend getItem(int n) {
		return mFriends[n];
	}

	@Override
	public long getItemId(int n) {
		Friend f = mFriends[n];
		return f.id.hashCode();
	}
	
	@Override 
	public View getItemLoadingView(int position, View convertView, ViewGroup parent) {
		View friendLoadingItem;
		Friend friendForView = mFriends[position];
		
		if (convertView == null) {
			friendLoadingItem = mInflater.inflate(R.layout.item_frienditem, null);
		} else {
			friendLoadingItem = convertView;
		}
		
		TextView friendEmail = (TextView) friendLoadingItem.findViewById(mEmailField);
		TextView friendStatus = (TextView) friendLoadingItem.findViewById(mStatusField);
		
		friendEmail.setText(friendForView.email);
		friendStatus.setText(R.string.label_updating);
		
		return friendLoadingItem;
		
	}

	@Override
	protected View getItemView(int position, View convertView, ViewGroup parent) {
		View friendItem;
		Friend friendForView = mFriends[position];
		
		if(convertView == null) {
			friendItem = mInflater.inflate(R.layout.item_frienditem, null);
		} else {
			friendItem = convertView;
		}
		
		TextView friendEmail = (TextView) friendItem.findViewById(mEmailField);
		TextView friendStatus = (TextView) friendItem.findViewById(mStatusField);
		
		friendEmail.setText(friendForView.email);
		
		if(friendForView.isRequested()) {
			friendStatus.setText(R.string.label_friend_requested);
		}
		else if(friendForView.isPending()) {
			friendStatus.setText(R.string.label_friend_pending);
		}
		else if(friendForView.isActive()) {
			friendStatus.setText(R.string.label_friend_active);
		}
		
		return friendItem;
	}

	@Override
	protected void fetchData(APICallback callback) {
		APICall task = new APICall(callback, mSession);
		task.getFriends();
	}

	@Override
	protected View getLoadingView() {
		return mInflater.inflate(R.layout.item_loadingitem, null);
	}

	@Override
	protected void onFetchComplete(boolean result, APICall task) {
		if(result == true) {
			mFriends = (Friend[]) task.getPayload();
		}
	}
	
	public void acceptFriend(int position) {
		setUpdating(position,true);
		Friend friend = getItem(position);
		FriendListAdapter.this.notifyDataSetChanged();
		new APICall(constructAPICallback(position), mSession).acceptFriend(friend.id);
		
	}
	
	public void removeFriend(int position) {
		setUpdating(position,true);
		Friend friend = getItem(position);
		FriendListAdapter.this.notifyDataSetChanged();
		
		new APICall(constructAPICallback(position), mSession).rejectFriend(friend.id);
		
	}
	
	
	/* This might not be the best way to do this */
	public APICallback constructAPICallback(int position) {
		return new APICallback() {
			
			private int position;

			@Override
			public void onCallComplete(boolean success, APICall task) {
				setUpdating(position,false);
				
				if (success) {
					switch(task.getCall()) {
						case APICall.USER_ACCEPTFRIEND:
							getItem(position).status = Friend.STATUS_ACTIVE; break; // Okay to do this?
						case APICall.USER_DEFRIEND:
							removeFriendFromList(position); break;
						default:
							break;
					}
					
					
					FriendListAdapter.this.notifyDataSetChanged();
				}
				
			}
			
			public APICallback setPosition(int position) {
				this.position = position;
				return this;
			}
			
			
		}.setPosition(position);
	}
	
	private Friend removeFriendFromList(int position) {
		// To remove the friend from the local copy
		// How do we update mLoadingFlags[]?
		Friend removeFriend = getItem(position);
		Friend[] newFriends = new Friend[getItemCount()-1];
		int j = 0;
		
		for(int i = 0; i<getItemCount(); i++) {
			if (i!=position) {
				newFriends[j] = mFriends[i];
				j++;
			}
		}
		
		mFriends = newFriends;
		
		return removeFriend;
		
	}

}
