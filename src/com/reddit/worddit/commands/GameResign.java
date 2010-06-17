package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameResign extends ClientCommand {
	/**
	 * Forfeit the game.<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id
	 * 
	 */	
	public GameResign() {
		super();

		mCommandType = CommandType.POST;
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/resign";
	}
}