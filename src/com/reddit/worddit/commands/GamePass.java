package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GamePass extends ClientCommand {
	/**
	 * Pass on your turn<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id
	 * 
	 */	
	public GamePass() {
		super();

		mCommandType = CommandType.POST;
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/pass";
	}
}