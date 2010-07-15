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
	protected Context mContext;
	
	private int mStatusField, mNextPlayerField, mLastMoveField; 
	
	public GameListAdapter(Context ctx, Game[] games) {
		mGames = games;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = ctx;
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
		
		TextView main = (TextView) gameItem.findViewById(mNextPlayerField);
		TextView subtext = (TextView) gameItem.findViewById(mLastMoveField);
		
		// Pre-decide the player's status.
		int playerStatus = R.string.label_invalidvalue;
		if(gameForView.isInvited()) {
			playerStatus = R.string.label_invited;
		}
		else if(gameForView.isPlaying()) {
			playerStatus = R.string.label_playing;
		}
			
		
		// If the game is in progress, we'd want to show who is next up
		// If possible, we can show when the last move was made, otherwise,
		// we can just show the player is playing in this game.
		if(gameForView.isInProgress()) {
			String mainLabel = mContext.getString(R.string.label_nextup);
			main.setText(String.format(mainLabel, gameForView.players[gameForView.current_player].id));
			
			if(gameForView.last_move_utc != null) {
				String subLabel = mContext.getString(R.string.label_lastplay);
				subtext.setText(String.format(subLabel, gameForView.last_move_utc));
			}
			else {
				subtext.setText(playerStatus);
			}
		}
		// Simply show that the server is waiting on players
		// and show the current players response
		else if (gameForView.isPending()) {
			main.setText(R.string.label_game_pending);
			subtext.setText(playerStatus);
		}
		// TODO: We could display a 'winner' here
		// Show the game is completed and the last move.
		else if(gameForView.isCompleted()) {
			main.setText(R.string.label_game_completed);
			String subLabel = mContext.getString(R.string.label_lastplay);
			subtext.setText(String.format(subLabel, gameForView.last_move_utc));
		}
		else {
			main.setText("Unknown state: " + gameForView.game_status);
		}
		
		return gameItem;
	}

}
