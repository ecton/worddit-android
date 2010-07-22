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
		markUpdating(position);
		Friend friend = getItem(position);
		new APICall((APICallback) mContext, mSession).acceptFriend(friend.id);
		
	}
	
	public void removeFriend(int position) {
		markUpdating(position);
		Friend friend = getItem(position);
		new APICall((APICallback) mContext, mSession).rejectFriend(friend.id);
		
	}


}
