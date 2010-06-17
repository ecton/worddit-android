package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class UserSetAvatar extends ClientCommand {
	/**
	 * Uploads a picture for the profile<br>
	 * <br>
	 * Required args: image
	 * 
	 */
	public UserSetAvatar() {
		super();

		// throw new Exception("command not implemented");

		mCommandType = CommandType.POST;

		// TODO: figure out how to post a file in a reasonable manner
		mRequiredArgs.add("image");
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/user/setavatar";
	}
}