package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameChatHistory extends ClientCommand {
	/**
	 * Retrieve the chat history for this game since date "limit"<br>
	 * <br>
	 * Required args: none<br>
	 * <br>
	 * Required URI Parameter: :id
	 * Optional URI Parameter: :limit
	 */
	public GameChatHistory() {
		super();

		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/chat/history/:limit";
		
		mCommandType = CommandType.GET;
	}
}