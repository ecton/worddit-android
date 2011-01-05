package com.reddit.worddit;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * WordditActivity is the class where most of the actual game
 * takes place.
 * 
 * It expects to be provided in its intent an ID over the key
 * <code>Constants.EXTRA_GAMEID</code>.
 * @author Paul Kilgo
 *
 */
public class WordditActivity extends Activity {
	protected static final String TAG = "WordditActivity";

	protected Session mSession;
	
	protected Game mGame;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		// Restore the Session
		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		// Grab the game object
		mGame = (Game) i.getParcelableExtra(Constants.EXTRA_GAME);
		
		// Set up the basic UI.
		this.setContentView(R.layout.activity_worddit);
		setupListeners();
		
		// See if we need to restore anything.
		if(icicle == null) {
			// TODO: Initializing step?
		}
		else {
			restoreFromBundle(icicle);
		}
	}

	private void restoreFromBundle(Bundle icicle) {
		// TODO Auto-generated method stub
		
	}

	private void setupListeners() {
		// TODO Auto-generated method stub
		
		String info = String.format("Game ID: %s\nGame status: %s\nYour status: %s\n\n",
				mGame.id,
				mGame.game_status,
				mGame.player_status
			); 

		
		for(int i = 0; i < mGame.players.length; i++) {
			info += "Player " + (i+1) + ": " + mGame.players[i].id + "\n";
		}
		
		((TextView)findViewById(R.id.worddit_textview)).setText(
			info	
		);
	}
}
