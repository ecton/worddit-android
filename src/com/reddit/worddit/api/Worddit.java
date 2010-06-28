package com.reddit.worddit.api;

/**
 * A class which defines values as constants as defined
 * by the Worddit API.
 * @author OEP
 *
 */
public class Worddit {
	/** Constants for response codes returned by the Worddit server. */
	public static final int
		SUCCESS_CREATED = 201,
		USER_EXISTS = 409,
		SUCCESS = 200,
		SUCCESS_NOT_VERIFIED = 202,
		NOT_FOUND = 404,
		AUTH_INVALID = 403,
		ARGUMENT_ERROR = 400;
	
	/** Constant keywords for payloads (arguments) required by the Worddit server */
	public static final String
		EMAIL = "email",
		PASSWORD = "password",
		CLIENT_TYPE = "client_type",
		NEW_PASSWORD = "newpassword",
		NICKNAME = "nickname",
		AVATAR = "avatar",
		IMAGE = "image",
		ID = "id",
		STATUS = "status",
		CURRENT_PLAYER = "current_player",
		PLAYERS = "players",
		LAST_MOVE = "last_move_utc",
		INVITATIONS = "invitations",
		RULES = "rules",
		REQUESTED_PLAYERS = "requested_players",
		ROW = "row",
		COLUMN = "column",
		DIRECTION = "direction",
		TILES = "tiles",
		MESSAGE = "message",
		DEVICE_ID = "device_id",
		AUTH_COOKIE = "auth";
	
	/** Constant values as defined by the server API */
	public static final String
		DOWN = "down",
		RIGHT = "right";
	
	/** Constant paths defined by the server API */
	public static final String
		PATH_USER_ADD = "/user/add",
		PATH_USER_LOGIN = "/user/login",
		PATH_USER_SETPROFILE = "/user/setprofile",
		PATH_USER_SETAVATAR = "/user/setavatar",
		PATH_USER_GAMES = "/user/games",
		PATH_USER_FRIENDS = "/user/friends",
		PATH_USER_FIND = "/user/find/%s",
		PATH_USER_BEFRIEND = "/user/%s/befriend",
		PATH_USER_DEFRIEND = "/user/%s/defriend",
		PATH_USER_ACCEPTFRIEND = "/user/%s/acceptfriend",
		PATH_GAME_NEW = "/game/new",
		PATH_GAME_REQUEST = "/game/request",
		PATH_GAME_ACCEPT = "/game/%s/accept",
		PATH_GAME_REJECT = "/game/%s/reject",
		PATH_GAME_BOARD = "/game/%s/board",
		PATH_GAME_RACK = "/game/%s/rack",
		PATH_GAME_HISTORY = "/game/%s/history/%d",
		PATH_GAME_PLAY = "/game/%s/play",
		PATH_GAME_SWAP = "/game/%s/swap",
		PATH_GAME_PASS = "/game/%s/pass",
		PATH_GAME_RESIGN = "/game/%s/resign",
		PATH_GAME_CHATHISTORY = "/game/%s/resign",
		PATH_GAME_CHATSEND = "/game/%s/resign";
}
