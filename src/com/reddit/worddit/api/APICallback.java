package com.reddit.worddit.api;


public interface APICallback {
	void onCallComplete(boolean success, int resId, Session sess);
	
}
