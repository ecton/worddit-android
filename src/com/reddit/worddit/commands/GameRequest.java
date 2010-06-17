package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameRequest extends ClientCommand {
	/**
	 * Marks this user as wanting to join a new scrabble game. The server will
	 * pair up players automatically.<br>
	 * <br> 
	 * Required args: rules
	 */
	public GameRequest() {
		super();

		mCommandType = CommandType.POST;

		mRequiredArgs.add("rules");
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/request";
	}
}