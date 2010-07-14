package com.reddit.worddit;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.reddit.worddit.adapters.GameListAdapter;
import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Game;

public class GameList extends ListActivity implements APICallback {
	public static final String TAG = "GameList";
	
	protected Game[] mGames;
	protected Session mSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.worddit_game_list);

		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		fetchGames();
	}

	private void fetchGames() {
		APICall task = new APICall(this, mSession);
		task.getGames();
	}

	@Override
	public void onCallComplete(boolean success, APICall task) {
		if(success) {
			mGames = (Game[]) task.getPayload();
			
			setListAdapter(new GameListAdapter(this, mGames, 0, R.id.item_game_nextup, R.id.item_game_lastplay));
		}
	}
}
