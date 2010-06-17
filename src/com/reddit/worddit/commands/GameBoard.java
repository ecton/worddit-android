package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameBoard extends ClientCommand {
	/**
	 * Returns the current state of the board for the game "id"<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id
	 */
	public GameBoard() {
		super();

		URI = WordditHome.GetApplicationContext().getString(
				R.string.worddit_server)
				+ "/game/:id/board";

		mCommandType = CommandType.GET;
	}
}