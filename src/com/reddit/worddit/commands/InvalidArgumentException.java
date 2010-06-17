package com.reddit.worddit.commands;

public class InvalidArgumentException extends Exception {
	private static final long serialVersionUID = -8977168000034870065L;

	/**
	 * ClientCommand argument exception for invalid, missing, or improperly formatted arguments
	 * @param message 
	 */
	public InvalidArgumentException(String message)
	{
		super(message);
	}
}
