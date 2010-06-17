package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class UserLogin extends ClientCommand {
	/**
	 * This method attempts to log in as an existing user.<br>
	 * <br>
	 * Required args: client_type, device_id, email, password
	 * 
	 */
	public UserLogin() {
		super();

		mCommandType = CommandType.POST;

		mRequiredArgs.add("client_type");
		mRequiredArgs.add("device_id");
		mRequiredArgs.add("email");
		mRequiredArgs.add("password");
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/user/login";
	}
}