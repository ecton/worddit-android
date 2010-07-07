package com.reddit.worddit;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.worddit_game_list);
		
		try { mSession = fetchSession(); }
		catch (MalformedURLException e) { /* Hope it doesn't happen */ }
		
		//fetchGames();
	}

	protected Session fetchSession() throws MalformedURLException {
		Intent i = getIntent();
		return fetchSession(i.getExtras());
	}
	
	protected Session fetchSession(Bundle b) throws MalformedURLException {
		String url = b.getString(Constants.EXTRA_URL);
		String cookie = b.getString(Constants.EXTRA_COOKIE);
		
		Session s = Session.makeSession(url,cookie);
		return s;
	}
	
	
	private void fetchGames() {
		APICall task = new APICall(this, mSession);
		task.getGames();
	}

	@Override
	public void onCallComplete(boolean success, APICall task) {
		if(success) {
			mGames = (Game[]) task.getPayload();
		}
		
		// TODO: Display games in the list!
	}
}
