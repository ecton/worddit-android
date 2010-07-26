package com.reddit.worddit.adapters;

import java.util.ArrayList;
import java.util.Arrays;

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
	protected ArrayList<Friend> mFriends = new ArrayList<Friend>( );
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
		return (mFriends == null) ? 0 : mFriends.size();
	}

	@Override
	public Friend getItem(int n) {
		return mFriends.get(n);
	}

	@Override
	public long getItemId(int n) {
		Friend f = mFriends.get(n);
		return f.id.hashCode();
	}
	
	@Override 
	public View getItemLoadingView(int position, View convertView, ViewGroup parent) {
		View friendLoadingItem;
		Friend friendForView = mFriends.get(position);
		
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
		Friend friendForView = mFriends.get(position);
		
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
			Friend friends[] = (Friend[]) task.getPayload();
			mFriends = new ArrayList<Friend>(Arrays.asList(friends));
		}
	}
	
	public void acceptFriend(final int position) {
		setUpdating(position,true);
		Friend friend = getItem(position);
		
		// Force the list to show the updating view
		FriendListAdapter.this.notifyDataSetChanged();
		
		// Build our API call
		APICall task = new APICall(
				new APICallback() {
					@Override
					public void onCallComplete(boolean success, APICall task) {
						setUpdating(position,false);
						if(success) {
							getItem(position).status = Friend.STATUS_ACTIVE;
						}
						FriendListAdapter.this.notifyDataSetChanged();
					}
				},
				mSession);
		
		task.acceptFriend(friend.id);
	}
	
	public void removeFriend(final int position) {
		setUpdating(position,true);
		Friend friend = getItem(position);
		
		// Forces redisplay to show the updating item.
		FriendListAdapter.this.notifyDataSetChanged();
		
		// Build our API call
		APICall task = new APICall(
					new APICallback() {
						@Override
						public void onCallComplete(boolean success, APICall task) {
							setUpdating(position, false);
							if(success) {
								removeFriendFromList(position);
							}
							FriendListAdapter.this.notifyDataSetChanged();
						}
					}, mSession);
		
		task.rejectFriend(friend.id);
	}
	
	/**
	 * Removes a Friend object from memory.
	 * @param position the position of the Friend to remove
	 * @return the removed Friend
	 */
	protected Friend removeFriendFromList(int position) {
		Friend removeFriend = getItem(position);
		mFriends.remove(position);
		return removeFriend;
	}

}
