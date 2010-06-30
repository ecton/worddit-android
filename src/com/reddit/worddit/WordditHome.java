package com.reddit.worddit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class WordditHome extends Activity {
	public static final String TAG = "WordditHome";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setup();
	}
	
	private void setup() {
		// Force confirm password field to reflect default state of checkbox
		doNewChecked(null);
	}

	public void doLogin(View v) {
		doLogin(getEmailField(), getPasswordField());
	}
	
	public void doCreate(View v) {
		
	}
	
	public void doNewChecked(View v) {
		CheckBox check = (CheckBox) findViewById(R.id.login_check_new);
		EditText confirm = (EditText) findViewById(R.id.login_input_confirmpassword);
		
		if(check.isChecked()) {
			confirm.setVisibility(View.VISIBLE);
		} else {
			confirm.setVisibility(View.GONE);
		}
	}
	
	protected void doLogin(String email, String password) {
		Log.i(TAG, String.format("LOGIN: %s/%s\n",email,password));
	}
	
	protected String getEmailField() {
		EditText emailField = (EditText) this.findViewById(R.id.login_input_email);
		return emailField.getText().toString();
	}
	
	protected String getPasswordField() {
		EditText passwordField = (EditText) this.findViewById(R.id.login_input_password);
		return passwordField.getText().toString();
	}
}