package com.reddit.worddit;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Game;

public class GameList extends ListActivity implements APICallback {
	
	private ArrayList<Game> mGameList;
	private Session mSession;
	
	public GameList (Session sess) {
		mSession = sess;
	}
	
	@Override
	public void onCallComplete(boolean success, APICall task) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.worddit_game_list);
		//fetchGames();
		
	}

	private void fetchGames() {

	}
}
