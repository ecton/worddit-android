package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class UserFind extends ClientCommand {
	/**
	 * Returns full profile, if you're a friend. If not, returns public profile
	 * only.<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id_or_email
	 * 
	 */
	public UserFind() {
		super();

		mCommandType = CommandType.GET;
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/user/find/:id_or_email";
	}
}