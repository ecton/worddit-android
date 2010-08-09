package com.reddit.worddit;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.reddit.worddit.adapters.GameListAdapter;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Game;

public class GamesActivity extends ListActivity {
	public static final String TAG = "GameList";
	
	protected Game[] mGames;
	protected Session mSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		

		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		setupList();
	}

	private void setupList() {
		GameListAdapter adapter;
		setListAdapter(adapter = new GameListAdapter(this, mSession));
		adapter.repopulate();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.games_options_menu, menu);
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.game_add:
			//TODO: add game
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
