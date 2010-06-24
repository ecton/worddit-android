package com.reddit.worddit.api.response;

/**
 * A class to represent a move in the game's history.
 * @author OEP
 *
 */
public class Move {
	/** User which played this move. Can be null if we weren't authenticated. */
	public String user;
	
	/** Points scored by this move. */
	public int points;
	
	/** Primary word this move forms. */
	public String primary_word;
	
	/** Tiles played in this turn. */
	public Tile tiles[];
	
	/**
	 * This class represents a tile in a player's move.
	 * @author OEP
	 *
	 */
	class Tile {
		/** Letter this tile represents */
		public char letter;
		
		/** Row where this tile was played */
		public int row;
		
		/** Column where this tile was played */
		public int column;
	}
}
