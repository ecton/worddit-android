package com.reddit.worddit.api.response;


/**
 * Tile object used to represent a user's rack.
 * To be distincted from <code>Move.Tile</code>
 * and <code>GameBoard.Tile</code>.
 * @author OEP
 *
 */
public class Tile {
	/** Letter which this tile represents. */
	public char letter;
	
	/** Points this tile is worth. */
	public int points;
}
