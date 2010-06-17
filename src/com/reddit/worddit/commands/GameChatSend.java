package com.reddit.worddit.commands;

import com.reddit.worddit.R;
import com.reddit.worddit.WordditHome;

public class GameChatSend extends ClientCommand {
	/**
	 * Post a new message in the chat<br>
	 * <br>
	 * Required args: message<br>
	 * <br>
	 * Required URI Parameter: :id
	 * 
	 */
	public GameChatSend() {
		super();

		mCommandType = CommandType.POST;

		URI = WordditHome.GetApplicationContext().getString(R.string.worddit_server) + "/game/:id/chat/send";

		mRequiredArgs.add("message");
	}
}