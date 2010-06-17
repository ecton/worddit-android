package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class UserAdd extends ClientCommand {
	/**
	 * This method registers a new user. The server will initiate an email
	 * verification process. <br>
	 * <br>
	 * Required args: client_type, device_id, email, password
	 */
	public UserAdd() {
		super();

		mCommandType = CommandType.POST;

		mRequiredArgs.add("client_type");
		mRequiredArgs.add("device_id");
		mRequiredArgs.add("email");
		mRequiredArgs.add("password");
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/user/add";
	}
}