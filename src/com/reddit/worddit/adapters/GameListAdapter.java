package com.reddit.worddit.adapters;

import com.reddit.worddit.R;
import com.reddit.worddit.api.response.Game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GameListAdapter extends BaseAdapter {
	protected Game[] mGames;
	protected LayoutInflater mInflater;
	
	private int mStatusField, mNextPlayerField, mLastMoveField; 
	
	public GameListAdapter(Context ctx, Game[] games) {
		mGames = games;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mNextPlayerField = R.id.item_game_nextup;
		mLastMoveField = R.id.item_game_lastplay;
	}
	
	public GameListAdapter(Context ctx, Game[] games, 
			int statusField, int nextPlayerField, int lastMoveField) {
		this(ctx, games);
		mStatusField = statusField;
		mNextPlayerField = nextPlayerField;
		mLastMoveField = lastMoveField;
	}

	@Override
	public int getCount() {
		return mGames.length;
	}

	@Override
	public Game getItem(int n) {
		return mGames[n];
	}

	@Override
	public long getItemId(int n) {
		Game g = mGames[n];
		return g.id.hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View gameItem;
		Game gameForView = mGames[position];
		
		
		if(convertView == null) {
			gameItem = mInflater.inflate(R.layout.item_gameitem, null);
		} else {
			gameItem = convertView;
		}
		
		TextView nextUp = (TextView) gameItem.findViewById(mNextPlayerField);
		TextView lastPlay = (TextView) gameItem.findViewById(mLastMoveField);
		
		if(gameForView.isInProgress()) {
			Context ctx = nextUp.getContext();
			String label = ctx.getString(R.string.label_nextup);
			nextUp.setText(String.format(label, gameForView.players[gameForView.current_player].id));
		} else if (gameForView.isPending()) {
			nextUp.setText(R.string.label_pending);
		} else if(gameForView.isCompleted()) {
			nextUp.setText(R.string.label_completed);
		}
		else {
			nextUp.setText("Unknown state: " + gameForView.status);
		}
		
		if(gameForView.last_move_utc != null) {
			lastPlay.setText("Last Move: " + gameForView.last_move_utc);
		} else {
			lastPlay.setText("Last Move: ");
		} // I figure we'll switch to labels, but I wanted something to print if they were null for testing.
		
		
		return gameItem;
	}

}
