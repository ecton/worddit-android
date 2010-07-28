package com.reddit.worddit;


import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;
import com.reddit.worddit.api.response.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class FriendFind extends Activity implements APICallback {
	public static final String TAG = "FriendFind";
	
	private Session mSession;
	private boolean mWindowIndeterminate = false;
	private static final int DIALOG_WAIT = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find);

		Intent i = getIntent();
		mSession = (Session) i.getParcelableExtra(Constants.EXTRA_SESSION);
		
		setup();
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
	
	private void setup() {
		setupListeners();
	}
	
	private void setupListeners() {
		findViewById(R.id.friend_find_button_search)
			.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doButtonClick(v);
			}
		});
	}
	
	public void doButtonClick(View v) {
		doFindFriend(v);
	}
	
	public void doFindFriend(View v) {
		doFindFriend(getEmailField());
	}
	
	protected void doFindFriend(String email) {
		boolean fail = false;
		int msg = 0;
		
		if(email.length() == 0) {
			msg = R.string.msg_required_fields_missing;
			fail = true;
		}
		
		if(fail) {
			showDialog(msg);
		}
		else {
			APICall task = new APICall(this, mSession);
			Log.i("FriendFind.java", email);
			task.findUser(email);
			setLoading(true);
		}
	}
	
	protected String getEmailField() {
		EditText emailField = (EditText) this.findViewById(R.id.friend_find_input_email);
		return emailField.getText().toString();
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
			Profile p = (Profile) task.getPayload();
			Intent i = getIntent();
			Bundle b = new Bundle();
			
			b.putString("id", p.id);
			b.putString("email", p.email);
			b.putString("nickname", p.nickname);
			b.putString("avatar", p.avatar);
			
			i.putExtra("com.reddit.worddit.Profile", b);
			
			setResult(RESULT_OK, i);
            finish();
		}
		else {
			setResult(RESULT_CANCELED);
            finish();
		}
	}
}
