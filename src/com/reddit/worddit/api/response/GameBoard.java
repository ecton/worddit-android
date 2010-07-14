package com.reddit.worddit.api.response;

/**
 * Class to represent the state of the game board.
 * @author OEP
 *
 */
public class GameBoard {
	/** Name of current player. Can be null if the server doesn't want us to see this. */
	public String current_player;
	
	/** Rows of the game board. */
	public Tile rows[];
	
	/**
	 * Class to represent a Tile on the game board.
	 * @author OEP
	 *
	 */
	class Tile {
		/** The letter this tile represents */
		public char letter;
		
		/** Special properties for this tile. Can be null if there are no special properties. */
		public String special;
		
		/**
		 * Checks to see if this tile is on a double letter square.
		 * @return true if tile is on double letter square.
		 */
		public boolean isDoubleLetter() {
			return L2.equalsIgnoreCase(special);
		}
		
		/**
		 * Checks to see if this tile is on a triple letter square.
		 * @return true if tile is on triple letter square.
		 */
		public boolean isTripleLetter() {
			return L3.equalsIgnoreCase(special);
		}
		
		/**
		 * Checks to see if this tile is on a double word square.
		 * @return true if tile is on double word square.
		 */
		public boolean isDoubleWord() {
			return W2.equalsIgnoreCase(special);
		}
		
		/**
		 * Checks to see if this tile is on a triple word square.
		 * @return true if tile is on double triple square.
		 */
		public boolean isTripleWord() {
			return W3.equalsIgnoreCase(special);
		}
		
		/**
		 * Checks to see if this tile is on the start square.
		 * @return true if tile is on the start square.
		 */
		public boolean isStart() {
			return START.equalsIgnoreCase(special);
		}
		
		/** Constants the server uses to represent special tiles. */
		public static final String
			W2 = "2w",
			W3 = "3w",
			L2 = "2l",
			L3 = "3l",
			START = "start";
			
	}
}
