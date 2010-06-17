package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class UserDefriend extends ClientCommand {
	/**
	 * Remove a friend request or friend link<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id
	 * 
	 */
	public UserDefriend() {
		super();

		mCommandType = CommandType.GET;
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/user/:id/defriend";
	}
}