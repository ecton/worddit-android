package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameReject extends ClientCommand {
	/**
	 * 
	 * Reject the invitation to this game<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id
	 */
	public GameReject() {
		super();

		mCommandType = CommandType.GET;
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/reject";
	}
}