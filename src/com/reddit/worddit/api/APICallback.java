package com.reddit.worddit.api;


public interface APICallback {
<<<<<<< HEAD
	Object onCallComplete(boolean success, int resId, Session sess);
=======
	void onCallComplete(boolean success, APICall task);
	
>>>>>>> 35eeeac59b2416cc590445b9792e95c11a6281de
}
