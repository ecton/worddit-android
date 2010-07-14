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
	
	private int mIDField, mStatusField; 
	
	public FriendListAdapter(Context ctx, Friend[] friends) {
		mFriends = friends;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = ctx;
		mIDField = R.id.item_friend_id;
		mStatusField = R.id.item_friend_status;
	}
	
	public FriendListAdapter(Context ctx, Friend[] friends, 
			int idField, int statusField) {
		this(ctx, friends);
		mIDField = idField;
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
		
		TextView friendID = (TextView) friendItem.findViewById(mIDField);
		TextView friendStatus = (TextView) friendItem.findViewById(mStatusField);
		
		friendID.setText(friendForView.id);
		
		if(friendForView.isRequested()) {
			friendStatus.setText(R.string.label_requested);
		}
		else if(friendForView.isPending()) {
			friendStatus.setText(R.string.label_pending);
		}
		else if(friendForView.isActive()) {
			friendStatus.setText(R.string.label_active);
		}
		
		return friendItem;
	}

}
