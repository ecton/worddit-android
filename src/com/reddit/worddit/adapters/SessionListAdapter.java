package com.reddit.worddit.adapters;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;

/**
 * This is an adapter that adapts lists of objects returned by com.reddit.worddit.api.Session
 * into a ListView.
 * @author pkilgo
 *
 */
public abstract class SessionListAdapter extends BaseAdapter {
	
	protected Context mContext;
	protected Session mSession;
	protected LayoutInflater mInflater;

	private AtomicBoolean mFetching = new AtomicBoolean(false);
	private boolean mLoadingFlags[];
	private View mLoadingView;
	
	abstract protected void fetchData(APICallback callback);
	abstract public int getItemCount();
	abstract protected View getLoadingView();
	abstract protected View getItemView(int position, View convertView, ViewGroup parent);
	abstract protected void onFetchComplete(boolean result, APICall task);
	
	public SessionListAdapter(Context ctx, Session session) {
		mContext = ctx;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSession = session;
		repopulate();
	}
	
	public int getCount() {
		if(mFetching.get() == true) return 1;
		return getItemCount();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mFetching.get() == true) {
			if(mLoadingView == null) {
				mLoadingView = getLoadingView();
			}
			return mLoadingView;
		}
		else if(mLoadingFlags != null && position < mLoadingFlags.length && mLoadingFlags[position] == true) {
			// TODO: Fetching single item case.
			TextView tv = new TextView(mContext);
			tv.setText("TODO: Make single item load");
			return tv;
		}
		else if(convertView == mLoadingView) {
			return getItemView(position, null, parent);
		}
		
		return getItemView(position, convertView, parent);
	}
	
	protected boolean isFetching() {
		return mFetching.get();
	}
	
	protected void markUpdating(int position) {
		mLoadingFlags[position] = true;
	}
	
	protected void markUpdated(int position) {
		mLoadingFlags[position] = false;
	}
	
	protected boolean isUpdating(int position) {
		return mLoadingFlags[position];
	}
	
	private void repopulate() {
		// We're working on it already!
		if(mFetching.get() == true) return;
		
		mFetching.set(true);
		mLoadingFlags = null;
		fetchData(new APICallback() {
			@Override
			public void onCallComplete(boolean success, APICall task) {
				mFetching.set(false);
				SessionListAdapter.this.notifyDataSetChanged();
				mLoadingFlags = new boolean[getCount()];
				onFetchComplete(success,task);
			}
		});
	}
}
