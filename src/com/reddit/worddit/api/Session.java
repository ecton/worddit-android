package com.reddit.worddit.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.reddit.worddit.api.response.Friend;
import com.reddit.worddit.api.response.Game;
import com.reddit.worddit.api.response.GameBoard;
import com.reddit.worddit.api.response.ChatMessage;
import com.reddit.worddit.api.response.Move;
import com.reddit.worddit.api.response.Profile;
import com.reddit.worddit.api.response.Tile;

public class Session {
	/** The default, official Worddit server */
	public static final String API_URL = "http://api.dev.worddit.org";
	
	/** The URL of the server we are working with */
	private URL mURL;
	
	/** The cookie argument provided by the server's login or create API call */
	private String mCookie;
	
	/** Last HTTP response code from the Worddit server */
	private int mLastResponse;
	
	/** The client type (what is this?) */
	// TODO: Find a way to resolve what this value is.
	private String mClientType = "foo";
	// Note: I assumed this won't need to be defined by client code (except possibly once)
	
	/** The device id (what is this?) */
	// TODO: Find a way to resolve what this value is.
	private String mDeviceId = "bar";
	// Note: I assumed this won't need to be defined by client code (except possibly once)
	
	/** This object can't be instantiated directly. */
	private Session() { }
	
	/**
	 * Attempt to create an account.
	 * Uses the provided email and password as the credentials.
	 * 		Note: for better granularity to see why request
	 * 		failed, use <code>Session.getLastResponse()</code>.
	 * @param email The email address to use as the account name
	 * @param password The password to use for this account
	 * @return true if the account was created, false otherwise
	 * @throws IOException
	 */
	public boolean createAccount(String email, String password)
	throws IOException {
		HttpURLConnection connection = post(
				Worddit.PATH_USER_ADD,
				Worddit.EMAIL, email,
				Worddit.PASSWORD, password,
				Worddit.CLIENT_TYPE, mClientType,
				Worddit.DEVICE_ID, mDeviceId);
		
		int response = connection.getResponseCode();
		if( response != Worddit.USER_CREATED ) return false;
		
		// Server should have returned an auth cookie.
		mCookie = String.format("%s=%s",
				Worddit.AUTH_COOKIE, HttpHelper.readCookie(connection, Worddit.AUTH_COOKIE));
		
		return true;
	}
	
	public boolean login(String email, String password) throws IOException {
		HttpURLConnection connection = post(
				Worddit.PATH_USER_LOGIN,
				Worddit.EMAIL, email,
				Worddit.PASSWORD, password,
				Worddit.CLIENT_TYPE, mClientType,
				Worddit.DEVICE_ID, mDeviceId);
		
		int response = connection.getResponseCode();
		if( response != Worddit.SUCCESS && response != Worddit.SUCCESS_NOT_VERIFIED ) return false;

		// Server should have returned an auth cookie.
		mCookie = String.format("%s=%s",
				Worddit.AUTH_COOKIE, HttpHelper.readCookie(connection, Worddit.AUTH_COOKIE));
		
		return true;
	}
	
	/**
	 * Set profile information for the current logged-in Session.
	 * @param email // TODO: Find out what this means
	 * @param newPassword The new password for this account
	 * @param nickname New nickname for this account
	 * @return true if it worked, false if not
	 * @throws IOException If there was trouble doing so.
	 */
	public boolean setProfile(String email, String newPassword, String nickname) throws IOException {
		HttpURLConnection connection = post(
				Worddit.PATH_USER_SETPROFILE,
				Worddit.EMAIL, email,
				Worddit.NEW_PASSWORD, newPassword,
				Worddit.NICKNAME, nickname);
		
		int response = connection.getResponseCode();
		if(response != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean setAvatar(URI imageUri) {
		// TODO: Implement setAvatar
		return false;
	}
	
	public Game[] getGames() throws IOException {
		HttpURLConnection conn = get(Worddit.PATH_USER_GAMES);
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return null;
		return castJson(conn,Game[].class);
	}
	
	public Friend[] getFriends() throws IOException {
		HttpURLConnection conn = get(Worddit.PATH_USER_FRIENDS);
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return null;
		return castJson(conn,Friend[].class);
	}
	
	public Profile findFriend(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_USER_FIND,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return null;
		return castJson(conn,Profile.class);
	}
	
	public boolean befriend(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_USER_BEFRIEND,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean defriend(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_USER_DEFRIEND,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean acceptFriend(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_USER_ACCEPTFRIEND,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return false;
		return true;
	}
	
	public JsonObject newGame(List<String> ids, List<String> rules) {
		// TODO: Implement newGame
		return null;
	}
	
	public JsonObject requestGame(int players, List<String> rules) {
		// TODO: Implement requestGame
		return null;
	}
	
	public boolean acceptGame(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_ACCEPT,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean rejectGame(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_REJECT,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return false;
		return true;
	}
	
	public GameBoard getBoard(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_BOARD,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return null;
		return castJson(conn,GameBoard.class);
	}
	
	public Tile[] getRack(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_RACK,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return null;
		return castJson(conn,Tile[].class);
	}
	
	public Move[] getGameHistory(String id, int limit) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_HISTORY,id,limit));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return null;
		return castJson(conn,Move[].class);
	}
	
	public JsonObject play(String id, int row, int column, boolean isVertical, List<Object> tiles) {
		// TODO: Implement play
		return null;
	}
	
	public JsonObject swap(String id, List<Object> tiles) {
		// TODO: Implement swap
		return null;
	}
	
	public boolean pass(String id) {
		// TODO: Implement pass
		return false;
	}
	
	public boolean resign(String id) {
		// TODO: Implement resign
		return false;
	}
	
	public ChatMessage[] getChatHistory(String id, int limit) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_USER_DEFRIEND,id));
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return null;
		return castJson(conn,ChatMessage[].class);
	}
	
	public boolean sendChatMessage(String id, String message) {
		// TODO: Implement sendChatMessage
		return false;
	}
	
	/**
	 * Retrieve the last HTTP response code given by
	 * the Worddit server on the last API call.
	 * @return last HTTP response code
	 */
	public int getLastResponse() {
		return mLastResponse;
	}
	
	/**
	 * Check if this Session instance is authenticated.
	 * @return true if the cookie is set, false if it is not
	 */
	public boolean isAuthenticated() {
		return mCookie != null && mCookie.length() > 0;
	}
	
	/**
	 * Initiate an HTTP POST for the URL-encoded parameters passed.
	 * @param path to make the HTTP POST to
	 * @param params Arbitrary-length list of key/value pairs
	 * @return <code>HttpURLConnection</code> representing this connection.
	 * @throws IOException if connection can't be established
	 */
	private HttpURLConnection post(String path, String ... params)
	throws IOException {
		String encodedArgs = HttpHelper.encodeParams(params);
		HttpURLConnection connection = HttpHelper.makePost(mURL, path, encodedArgs, mCookie);
		mLastResponse = connection.getResponseCode();
		return connection;
	}
	
	/**
	 * This is a helper function to take an open <code>HttpURLConnection</code>, read the JSON
	 * payload it contains, and cast it as some object.
	 * This method also works by magic.
	 * @param <T> the object type to cast to
	 * @param connection to read JSON data from
	 * @param type of the object
	 * @return JSON cast as the specified object
	 * @throws IOException if there were connection issues.
	 */
	private <T> T castJson(HttpURLConnection connection, Class<T> type)
	throws IOException {
		BufferedReader reader =
			new BufferedReader(new InputStreamReader(connection.getInputStream()));
		Gson gson = new Gson();
		return gson.fromJson(reader,type);
	}
	
	/**
	 * Initiate an HTTP GET for the given path.
	 * @param path to perform GET command to
	 * @return an <code>HttpURLConnection</code> representing the connection
	 * @throws IOException if there's big trouble.
	 */
	private HttpURLConnection get(String path) throws IOException {
		HttpURLConnection connection = HttpHelper.makeGet(mURL, path, mCookie);
		mLastResponse = connection.getResponseCode();
		return connection;
	}
	
	/**
	 * Make a <code>Session</code> object from the hard-coded <code>API_URL</code>.
	 * @return Session object set up for the default URL.
	 * @throws MalformedURLException If the URL is not valid.
	 */
	public static Session makeSession() throws MalformedURLException {
		return Session.makeSession(API_URL);
	}
	
	/**
	 * Create a session which uses a provided URL as the 
	 * game server location.
	 * @param url Which URL to use as the game server.
	 * @return Session object set up for the URL
	 * @throws MalformedURLException If the URL is invalid.
	 */
	public static Session makeSession(String url) throws MalformedURLException {
		Session s = new Session();
		s.mURL = new URL(url);
		return s;
	}
	
	/**
	 * Test function to show basically how the API works.
	 * @param args *ignored*
	 */
	public static void main(String args[]) {
		Session s = null;
		try {
			s = Session.makeSession(API_URL);
		} catch (MalformedURLException e) {
			System.exit(1);
		}
		
		String username = "testguy@example.com", password = "secret";
		try {
			boolean result = s.createAccount(username, password);

			if(result == false && s.getLastResponse() == Worddit.USER_EXISTS) {
				result = s.login(username, password);
				if(result == true) {
					System.out.println("Logged in!");
				}
			}
			else if(result == true) {
				System.out.println("Account created!");
			}
			else {
				System.err.println("Failed with http code: " + s.getLastResponse());
			}
		} catch (IOException e) {
			System.err.println("Connection error");
		}
	}
}
