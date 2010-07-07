package com.reddit.worddit.api.response;

/**
 * A class to represent a game that a player can join.
 * @author OEP
 *
 */
public class Game {
	/** ID for this game */
	public String id;
	
	/** Status for this game. Can be: {invited|accepted|active|waiting}. */
	public String status;
	
	/** Current player for this game. */
	public String current_player;
	
	/** Players for this game in order of rotation. */
	public Player players[];
	
	/** Date of last move in UTC. */
	public String last_move_utc;
	
	/**
	 * Checks if the game is waiting for acceptance from some players.
	 * @return true if the game is waiting on other players
	 */
	public boolean isPending() {
		return status.equalsIgnoreCase(STATUS_PENDING);
	}
	
	/**
	 * Checks if the game is currently being played.
	 * @return true if all players have accepted
	 */
	public boolean isInProgress() {
		return status.equalsIgnoreCase(STATUS_COMPLETED);
	}
	
	/**
	 * Checks if the game is completed.
	 * @return true if the game is completed
	 */
	public boolean isCompleted() {
		return status.equalsIgnoreCase(STATUS_COMPLETED);
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
	
	/** Constant values for the 'status' field. */
	public static final String
		STATUS_PENDING = "pending",
		STATUS_INPROGRESS = "inprogress",
		STATUS_COMPLETED = "completed";
	
	public static void main(String [] args) {
		
	}
}
