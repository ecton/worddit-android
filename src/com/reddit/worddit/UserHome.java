package com.reddit.worddit;

import com.reddit.worddit.api.Session;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class UserHome extends TabActivity {
	
	protected Session mSession;

	/** Called when the activity is first created */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_userhome);
		
		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		makeTabs();
	}
	
	protected void makeTabs() {
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		intent = new Intent(this, GameList.class);
		intent.putExtra(Constants.EXTRA_SESSION, mSession);
		spec = tabHost.newTabSpec(TAB_GAMES)
			.setIndicator(res.getString(R.string.label_games),
					res.getDrawable(android.R.drawable.ic_menu_myplaces))
			.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent(this, FriendList.class);
		intent.putExtra(Constants.EXTRA_SESSION, mSession);
		spec = tabHost.newTabSpec(TAB_FRIENDS)
			.setIndicator(res.getString(R.string.label_friends),
					res.getDrawable(android.R.drawable.ic_menu_share))
			.setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTabByTag(TAB_GAMES);
	}
	
	
	public static final String
		TAB_GAMES = "Games",
		TAB_FRIENDS = "Friends";
}
