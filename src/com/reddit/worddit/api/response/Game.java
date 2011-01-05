package com.reddit.worddit.api.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A class to represent a game that a player can join.
 * @author OEP
 *
 */
public class Game implements Parcelable {
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
	 * Default constructor
	 */
	public Game() {
		
	}
	
	/**
	 * Construct a Game from a Parcel.
	 * @param in the parcel to construct from
	 */
	private Game(Parcel in) {
		id = in.readString();
		game_status = in.readString();
		player_status = in.readString();
		current_player = in.readInt();
		Parcelable p[] = in.readParcelableArray(Player[].class.getClassLoader());
		players = new Player[p.length];
		
		for(int i = 0; i < p.length; i++) {
			players[i] = (Player) p[i];
		}
		
		last_move_utc = in.readString();
	}
	
	/**
	 * Checks if the game is waiting for acceptance from current player
	 * @return true if the game is waiting on current player to accept
	 */
	public boolean isInvited() {
		return STATUS_INVITED.equalsIgnoreCase(player_status);
	}
	
	/**
	 * Checks if the game has been accepted by current player
	 * @return true if current player has accepted
	 */
	public boolean isPlaying() {
		return STATUS_PLAYING.equalsIgnoreCase(player_status);
	}
	
	/**
	 * Checks if the game is declined
	 * @return true if the game is declined by current player
	 */
	public boolean isDeclined() {
		return STATUS_DECLINED.equalsIgnoreCase(player_status);
	}
	
	/**
	 * Checks if this game is waiting for other players to accept.
	 * @return true if not all players have accepted
	 */
	public boolean isPending() {
		return STATUS_PENDING.equalsIgnoreCase(game_status);
	}
	
	/**
	 * Checks if this game is in progress
	 * @return true if all players have accepted
	 */
	public boolean isInProgress() {
		return STATUS_INPROGRESS.equalsIgnoreCase(game_status);
	}
	
	/**
	 * Checks if the game has been completed.
	 * @return true if this game has been completed
	 */
	public boolean isCompleted() {
		return STATUS_COMPLETED.equalsIgnoreCase(game_status);
	}
	
	/**
	 * A holder class to represent a player in a game.
	 * @author pkilgo
	 *
	 */
	public static class Player implements Parcelable {
		/** Default constructor */
		public Player() { }
		
		/** Player ID */
		public String id;
		
		/** Player's score */
		public String score;
		
		private Player(Parcel in) {
			id = in.readString();
			score = in.readString();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(id);
			dest.writeString(score);
		}
		
		public static final Parcelable.Creator<Player> CREATOR
		= new Parcelable.Creator<Player>() {
			@Override
			public Player createFromParcel(Parcel source) {
				return new Player(source);
			}

			@Override
			public Player[] newArray(int size) {
				return new Player[size];
			}
		};
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(game_status);
		dest.writeString(player_status);
		dest.writeInt(current_player);
		dest.writeParcelableArray(players, flags);
		dest.writeString(last_move_utc);
	}
	
	public static final Parcelable.Creator<Game> CREATOR
	= new Parcelable.Creator<Game>() {
		@Override
		public Game createFromParcel(Parcel source) {
			return new Game(source);
		}

		@Override
		public Game[] newArray(int size) {
			return new Game[size];
		}
	};
	
}
