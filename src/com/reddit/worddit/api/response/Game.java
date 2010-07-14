package com.reddit.worddit.api.response;

/**
 * A class to represent a game that a player can join.
 * @author OEP
 *
 */
public class Game {
	/** ID for this game */
	public String id;
	
	/** Status for this game. Can be: {pending|inprogress|completed}. */
	public String game_status;
	
	/** Current player's status with this game. Can be: {invited|playing|declined} */
	public String player_status;
	
	/** Current player for this game. */
	public int current_player;
	
	/** Players for this game in order of rotation. */
	public Player players[];
	
	/** Date of last move in UTC. */
	public String last_move_utc;
	
	/**
	 * Checks if the game is waiting for acceptance from current player
	 * @return true if the game is waiting on current player to accept
	 */
	public boolean isInvited() {
		return player_status.equalsIgnoreCase(STATUS_INVITED);
	}
	
	/**
	 * Checks if the game has been accepted by current player
	 * @return true if current player has accepted
	 */
	public boolean isPlaying() {
		return player_status.equalsIgnoreCase(STATUS_PLAYING);
	}
	
	/**
	 * Checks if the game is declined
	 * @return true if the game is declined by current player
	 */
	public boolean isDeclined() {
		return player_status.equalsIgnoreCase(STATUS_DECLINED);
	}
	
	/**
	 * Checks if this game is waiting for other players to accept.
	 * @return true if not all players have accepted
	 */
	public boolean isPending() {
		return game_status.equalsIgnoreCase(STATUS_PENDING);
	}
	
	/**
	 * Checks if this game is in progress
	 * @return true if all players have accepted
	 */
	public boolean isInProgress() {
		return game_status.equalsIgnoreCase(STATUS_INPROGRESS);
	}
	
	/**
	 * Checks if the game has been completed.
	 * @return true if this game has been completed
	 */
	public boolean isCompleted() {
		return game_status.equalsIgnoreCase(STATUS_COMPLETED);
	}
	
	/**
	 * A holder class to represent a player in a game.
	 * @author pkilgo
	 *
	 */
	public static class Player {
		public Player() { }
		
		/** Player ID */
		public String id;
		
		/** Player's score */
		public String score;
	}
	
	/** Constant values for the player's status */
	public static final String
		STATUS_INVITED = "invited",
		STATUS_PLAYING = "playing",
		STATUS_DECLINED = "declined";
	
	/** Constant values for the game's status */
	public static final String
		STATUS_PENDING = "pending",
		STATUS_INPROGRESS = "inprogress",
		STATUS_COMPLETED = "completed";
	
}
