package com.reddit.worddit.adapters;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.reddit.worddit.R;
import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Game;

public class GameListAdapter extends BaseAdapter {
	protected Game[] mGames;
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected Session mSession;
	protected AtomicBoolean mLoading = new AtomicBoolean(false);
	protected View mLoadingView;
	
	private int mStatusField, mNextPlayerField, mLastMoveField; 
	
	public GameListAdapter(Context ctx, Session session) {
		this(ctx,session,0,R.id.item_game_nextup,R.id.item_game_lastplay);
	}
	
	public GameListAdapter(Context ctx, Session session,
			int statusField, int nextPlayerField, int lastMoveField) {
		mSession = session;
		mContext = ctx;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mStatusField = statusField;
		mNextPlayerField = nextPlayerField;
		mLastMoveField = lastMoveField;
		
		fetchGames();
	}
	
	public GameListAdapter(Context ctx, Game[] games) {
		this(ctx,games,0,R.id.item_game_nextup,R.id.item_game_lastplay);
	}
	
	public GameListAdapter(Context ctx, Game[] games, 
			int statusField, int nextPlayerField, int lastMoveField) {
		mGames = games;
		mContext = ctx;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mStatusField = statusField;
		mNextPlayerField = nextPlayerField;
		mLastMoveField = lastMoveField;
	}

	@Override
	public int getCount() {
		if(mLoading.get() == true || mGames == null) {
			return 1;
		}
		
		return mGames.length;
	}

	@Override
	public Game getItem(int n) {
		return mGames[n];
	}

	@Override
	public long getItemId(int n) {
		if(mLoading.get() == true) {
			if(n == 0) return 1;
			return 0;
		}
		
		Game g = mGames[n];
		return g.id.hashCode();
	}
	
	protected View getLoadingView() {
		if(mLoadingView == null) {
			mLoadingView = mInflater.inflate(R.layout.item_loadingitem, null);
		}
		
		return mLoadingView;
	}
	
	protected void fetchGames() {
		APICall task = new APICall(new APICallback() {
			@Override
			public void onCallComplete(boolean success, APICall task) {
				if(success) {
					mGames = (Game[]) task.getPayload();
					GameListAdapter.this.notifyDataSetChanged();
				}
				
				mLoading.set(false);
			}
		},
		mSession);
		
		mLoading.set(true);
		task.getGames();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View gameItem;

		// Stick a loading view in there if we're still fetchin' em
		if(mLoading.get() == true) {
			gameItem = getLoadingView();
			return gameItem;
		}
		
		// TODO: Case where mGames == null or mGames.length == 0 ?
		
		// Replace if convertView is null or it's still using the loading view
		if(convertView == null || convertView == getLoadingView()) {
			gameItem = mInflater.inflate(R.layout.item_gameitem, null);
		} else {
			gameItem = convertView;
		}
		
		Game gameForView = mGames[position];
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
