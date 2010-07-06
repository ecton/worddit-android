package com.reddit.worddit.api;


public interface APICallback {
	void onCallComplete(boolean success, APICall task);
	
}
