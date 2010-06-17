package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GamePlay extends ClientCommand {
	/**
	 * Make a move on a game<br>
	 * <br>
	 * Required args: row, column, direction, tiles<br>
	 * <br>
	 * Required URI Parameter: :id
	 * 
	 */
	public GamePlay() {
		super();

		mCommandType = CommandType.POST;

		mRequiredArgs.add("row");
		mRequiredArgs.add("column");
		mRequiredArgs.add("direction");
		mRequiredArgs.add("tiles");
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/play";
	}
}