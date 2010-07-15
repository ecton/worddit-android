package com.reddit.worddit.adapters;

import com.reddit.worddit.R;
import com.reddit.worddit.api.response.Friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter {
	protected Friend[] mFriends;
	protected LayoutInflater mInflater;
	protected Context mContext;
	
	private int mEmailField, mStatusField; 
	
	public FriendListAdapter(Context ctx, Friend[] friends) {
		mFriends = friends;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = ctx;
		mEmailField = R.id.item_friend_email;
		mStatusField = R.id.item_friend_status;
	}
	
	public FriendListAdapter(Context ctx, Friend[] friends, 
			int emailField, int statusField) {
		this(ctx, friends);
		mEmailField = emailField;
		mStatusField = statusField;
	}

	@Override
	public int getCount() {
		return mFriends.length;
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
	public View getView(int position, View convertView, ViewGroup parent) {
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

}
