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
import com.reddit.worddit.api.response.Profile;

public class FriendListAdapter extends SessionListAdapter {
	protected ArrayList<Profile> mFriends = new ArrayList<Profile>( );
	protected ArrayList<Profile> mFiltered = mFriends;
	private int mEmailField, mStatusField; 
	
	protected String mFilter;
	
	public FriendListAdapter(Context ctx, Session session) {
		this(ctx,session,R.id.item_friend_email, R.id.item_friend_status);
	}
	
	public FriendListAdapter(Context ctx, Session session,
			int emailField, int statusField) {
		super(ctx, session);
		mEmailField = emailField;
		mStatusField = statusField;
	}
	
	public void setFilter(String filter) {
		// Do nothing if the filter hasn't changed.
		if(filter.equalsIgnoreCase(mFilter)) return;
		
		mFilter = filter;
		mFiltered = getFilteredList();
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return (mFiltered == null) ? 0 : mFiltered.size();
	}

	@Override
	public Profile getItem(int n) {
		return mFiltered.get(n);
	}

	@Override
	public long getItemId(int n) {
		Profile f = mFiltered.get(n);
		return f.id.hashCode();
	}
	
	@Override 
	public View getItemLoadingView(int position, View convertView, ViewGroup parent) {
		View friendLoadingItem;
		Profile friendForView = mFiltered.get(position);
		
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
		Profile friendForView = mFiltered.get(position);
		
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
			Profile friends[] = (Profile[]) task.getPayload();
			mFriends = new ArrayList<Profile>(Arrays.asList(friends));
			mFilter = null;
			mFiltered = mFriends;
		}
	}
	
	public void acceptFriend(final int position) {
		setUpdating(position,true);
		Profile friend = getItem(position);
		
		// Force the list to show the updating view
		FriendListAdapter.this.notifyDataSetChanged();
		
		// Build our API call
		APICall task = new APICall(
				new APICallback() {
					@Override
					public void onCallComplete(boolean success, APICall task) {
						setUpdating(position,false);
						if(success) {
							getItem(position).status = Profile.STATUS_ACTIVE;
						}
						FriendListAdapter.this.notifyDataSetChanged();
					}
				},
				mSession);
		
		task.acceptFriend(friend.id);
	}
	
	public void removeFriend(final int position) {
		setUpdating(position,true);
		Profile friend = getItem(position);
		
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
	protected Profile removeFriendFromList(int position) {
		Profile removeFriend = getItem(position);
		mFriends.remove(removeFriend);
		mFiltered = getFilteredList();
		super.removeItem(position);
		return removeFriend;
	}

	/**
	 * Gets the filtered list of friends for this ListAdapter.
	 * Currently the filter matches when the filtering term
	 * is contains (case-insensitive) somewhere in the email field.
	 * @return a filtered list of friends
	 */
	protected ArrayList<Profile> getFilteredList() {
		// Use the default if there's no filter.
		if(mFilter == null || mFilter.length() == 0) return mFriends;
		
		ArrayList<Profile> filtered = new ArrayList<Profile>();
		
		String filter = mFilter.toLowerCase();
		for(Profile f : mFriends) {
			String email = f.email.toLowerCase();
			
			if(email.contains(filter)) {
				filtered.add(f);
			}
		}
		
		return filtered;
	}
}
