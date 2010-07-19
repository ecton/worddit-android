package com.reddit.worddit;


import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class FriendAdd extends Activity implements APICallback {
	public static final String TAG = "FriendAdd";
	
	private Session mSession;
	private boolean mWindowIndeterminate = false;
	private static final int DIALOG_WAIT = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		doAddFriend(i.getStringExtra("com.reddit.worddit.ProfileID"));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Resources r = getResources();
		
		if (id == DIALOG_WAIT) {
			ProgressDialog dlg = new ProgressDialog(this);
			dlg.setTitle(R.string.app_name);
			dlg.setMessage(r.getString(R.string.msg_communicating));
			return dlg;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String msg = "(null)";
		
		try { msg = r.getString(id); }
		catch(Exception e) { msg = String.format(r.getString(R.string.msg_not_found), id); }
		
		return builder.setMessage(msg)
			.setTitle(R.string.app_name)
			.setCancelable(true)
			.setNeutralButton(R.string.label_ok, null)
			.create();
	}
	
	protected void doAddFriend(String id) {
		boolean fail = false;
		int msg = 0;
		
		
		if(fail) {
			showDialog(msg);
		}
		else {
			APICall task = new APICall(this, mSession);
			Log.i("FriendFind.java", id);
			task.addFriend(id);
			setLoading(true);
		}
	}
	
	protected void setLoading(boolean state) {
		// Do this if the device supports having a progress indicator in the
		// activity's toolbar
		if(mWindowIndeterminate == true) {
			this.setProgressBarIndeterminate(true);
			this.setProgressBarIndeterminateVisibility(state);
		}
		
		// Otherwise, just show a boring progress dialog.
		else if(state == true) {
			showDialog(DIALOG_WAIT);
		}
		else if(state == false) {
			removeDialog(DIALOG_WAIT);
		}
	}
	
	@Override
	public void onCallComplete(boolean success, APICall task) {
		setLoading(false);
		
		if(success) {	
			setResult(RESULT_OK);
            finish();
		}
		else {
			setResult(RESULT_CANCELED);
            finish();
		}
	}
}
