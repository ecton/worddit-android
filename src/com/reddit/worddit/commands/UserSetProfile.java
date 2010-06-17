package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class UserSetProfile extends ClientCommand {
	/**
	 * Sets fields on the user's profile. Fields that are not specified will not
	 * be altered<br>
	 * <br>
	 * Required args: none
	 * 
	 */
	public UserSetProfile() {
		super();

		mCommandType = CommandType.POST;
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/user/setprofile";
	}
}