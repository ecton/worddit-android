package com.reddit.worddit;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
		
		// Set up the basic UI.
		this.setContentView(R.layout.activity_worddit);
		setupListeners();
		
		// See if we need to restore anything.
		if(icicle == null) {
			fetchGameInfo();
		}
		else {
			restoreFromBundle(icicle);
		}
	}

	private void fetchGameInfo() {
		APICall call = new APICall(new APICallback() {
			@Override
			public void onCallComplete(boolean success, APICall task) {
				if(success) {
					mGame = (Game) task.getPayload();
				}
				else {
					// TODO: Wig out to the user about it.
					Log.w(TAG, "Didn't properly handle error while fetching game");
				}
			}
		}, mSession);
		
		// TODO: Some initializing API Call?
	}

	private void restoreFromBundle(Bundle icicle) {
		// TODO Auto-generated method stub
		
	}

	private void setupListeners() {
		// TODO Auto-generated method stub
		
	}
}
