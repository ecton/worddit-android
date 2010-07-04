package com.reddit.worddit.api;


public interface APICallback {
	Object onCallComplete(boolean success, int resId, Session sess);
}
