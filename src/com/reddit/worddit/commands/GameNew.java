package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameNew extends ClientCommand {
	/**
	 * Creates a new game<br>
	 * <br>
	 * Required args: invitations, rules
	 */
	public GameNew() {
		super();

		mCommandType = CommandType.POST;

		mRequiredArgs.add("invitations");
		mRequiredArgs.add("rules");
		
		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/new";
	}
}