package com.reddit.worddit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class WordditHome extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// allow global access to application context for pulling strings and
		// resources
		applicationContext = this;
		
		SpawnWorkerThread();
	}

	/**
	 * Spawn outside UI thread to handle tasks not associated with the UI
	 */
	protected void SpawnWorkerThread()
    {
		//TODO: should maybe be moved somewhere else and not reside in WordditHome
    	Thread t = new Thread() 
    	{
    		public void run() {
    			//ClientCommand.Test();
    			Log.d("Worddit", "spawned worker thread for executing commands");
    		}
    	};
    	t.start();
    }

	private static Context applicationContext;

	public static Context GetApplicationContext() {
		return applicationContext;
	}
}
