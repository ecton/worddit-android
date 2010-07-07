package com.reddit.worddit.adapters;

import java.util.List;

import com.reddit.worddit.api.response.Game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GameListAdapter extends BaseAdapter {
	protected List<Game> mGames;
	protected LayoutInflater mInflater;
	
	private int mStatusField, mNextPlayerField, mLastMoveField; 
	
	public GameListAdapter(Context ctx, List<Game> games) {
		
	}
	
	public GameListAdapter(Context ctx, List<Game> games, 
			int statusField, int nextPlayerField, int lastMoveField) {
		
	}

	@Override
	public int getCount() {
		return mGames.size();
	}

	@Override
	public Object getItem(int n) {
		return mGames.get(n);
	}

	@Override
	public long getItemId(int n) {
		Game g = mGames.get(n);
		return Long.parseLong(g.id, 16);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
