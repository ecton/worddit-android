package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameHistory extends ClientCommand {
	/**
	 * Returns the last "limit" entries in the game history<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id
	 * Optional URI Parameter: :limit
	 */
	public GameHistory() {
		super();

		mCommandType = CommandType.GET;
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/history/:limit";
	}
}