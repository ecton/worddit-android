package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;


public class GameAccept extends ClientCommand {
	/**
	 * Accept the invitation to this game<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id
	 */
	public GameAccept() {
		super();

		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/accept";
		
		mCommandType = CommandType.GET;
	}
}