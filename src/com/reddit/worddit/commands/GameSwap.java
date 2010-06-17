package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameSwap extends ClientCommand {
	/**
	 * 
	 * Swap tiles for new tiles<br>
	 * <br>
	 * Required args: tiles<br>
	 * <br>
	 * Required URI Parameter: :id
	 */
	public GameSwap() {
		super();

		mCommandType = CommandType.POST;

		mRequiredArgs.add("tiles");
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/swap";
	}
}