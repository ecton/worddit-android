package com.reddit.worddit.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.reddit.worddit.api.response.ChatMessage;
import com.reddit.worddit.api.response.Friend;
import com.reddit.worddit.api.response.Game;
import com.reddit.worddit.api.response.GameBoard;
import com.reddit.worddit.api.response.Move;
import com.reddit.worddit.api.response.Profile;
import com.reddit.worddit.api.response.Tile;

public class Session {
	/** Debug tag */
	public static final String TAG = "Session";
	
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
		
		if( getLastResponse() != Worddit.SUCCESS_CREATED ) return false;
		
		// Server should have returned an auth cookie.
		String value = HttpHelper.readCookie(connection, Worddit.AUTH_COOKIE);
		mCookie = (value != null) ? String.format("%s=%s", Worddit.AUTH_COOKIE, value) : null;
		
		return true;
	}
	
	public boolean login(String email, String password) throws IOException {
		HttpURLConnection connection = post(
				Worddit.PATH_USER_LOGIN,
				Worddit.EMAIL, email,
				Worddit.PASSWORD, password,
				Worddit.CLIENT_TYPE, mClientType,
				Worddit.DEVICE_ID, mDeviceId);
		
		if( getLastResponse() != Worddit.SUCCESS && getLastResponse() != Worddit.SUCCESS_ACCEPTED ) return false;

		// Server should have returned an auth cookie.
		String value = HttpHelper.readCookie(connection, Worddit.AUTH_COOKIE);
		mCookie = (value != null) ? String.format("%s=%s", Worddit.AUTH_COOKIE, value) : null;
		
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
		post(
				Worddit.PATH_USER_SETPROFILE,
				Worddit.EMAIL, email,
				Worddit.NEW_PASSWORD, newPassword,
				Worddit.NICKNAME, nickname);
		
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean setAvatar(URI imageUri) {
		// TODO: Implement setAvatar
		return false;
	}
	
	/**
	 * Retrieve a list of games that are relevant to the player
	 * @return list of available games
	 * @throws IOException if there are connection issues
	 */
	public Game[] getGames() throws IOException {
		HttpURLConnection conn = get(Worddit.PATH_USER_GAMES);
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn,Game[].class);
	}
	
	public Friend[] getFriends() throws IOException {
		HttpURLConnection conn = get(Worddit.PATH_USER_FRIENDS);
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn,Friend[].class);
	}
	
	public Profile findUser(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_USER_FIND,id));
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn,Profile.class);
	}
	
	public boolean befriend(String id) throws IOException {
		get(String.format(Worddit.PATH_USER_BEFRIEND,id));
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean defriend(String id) throws IOException {
		get(String.format(Worddit.PATH_USER_DEFRIEND,id));
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean acceptFriend(String id) throws IOException {
		get(String.format(Worddit.PATH_USER_ACCEPTFRIEND,id));
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
	}
	
	public String newGame(List<String> ids, List<String> rules) throws IOException {
		return newGame(collapse(ids,','), collapse(rules,','));
	}
	
	public String newGame(String ids, String rules) throws IOException {
		HttpURLConnection conn = post(Worddit.PATH_GAME_NEW,
				Worddit.INVITATIONS, ids,
				Worddit.RULES, rules);
		
		if(getLastResponse() != Worddit.SUCCESS_CREATED) return null;
		return castJson(conn, String.class);
	}
	
	public String requestGame(int players, List<String> rules) throws IOException {
		return requestGame(players,collapse(rules,','));
	}
	
	public String requestGame(int players, String rules) throws IOException {
		HttpURLConnection conn = post(Worddit.PATH_GAME_REQUEST,
				Worddit.REQUESTED_PLAYERS, Integer.toString(players),
				Worddit.RULES, rules);
		
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn, String.class);
	}
	
	public boolean acceptGame(String id) throws IOException {
		get(String.format(Worddit.PATH_GAME_ACCEPT,id));
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean rejectGame(String id) throws IOException {
		get(String.format(Worddit.PATH_GAME_REJECT,id));
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
	}
	
	public GameBoard getBoard(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_BOARD,id));
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn,GameBoard.class);
	}
	
	public Tile[] getRack(String id) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_RACK,id));
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn,Tile[].class);
	}
	
	public Move[] getGameHistory(String id, int limit) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_HISTORY,id,limit));
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn,Move[].class);
	}
	
	public Move play(String id, int row, int column, String dir, String tiles)
	throws IOException {
		if(dir.equalsIgnoreCase(Worddit.DOWN) == false
				&& dir.equalsIgnoreCase(Worddit.RIGHT) == false) {
			throw new IllegalArgumentException("Invalid direction: " + dir);
		}
		HttpURLConnection conn = post(String.format(Worddit.PATH_GAME_PLAY, id),
				Worddit.ROW, Integer.toString(row),
				Worddit.COLUMN, Integer.toString(column),
				Worddit.DIRECTION, dir.toLowerCase(),
				Worddit.TILES, tiles);
		
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn, Move.class);
	}
	
	public Tile[] swap(String id, String tiles) throws IOException {
		HttpURLConnection conn = post(String.format(Worddit.PATH_GAME_SWAP, id), Worddit.TILES, tiles);
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn, Tile[].class);	}
	
	public boolean pass(String id) throws IOException {
		post(String.format(Worddit.PATH_GAME_PASS, id));
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
	}
	
	public boolean resign(String id) throws IOException {
		post(String.format(Worddit.PATH_GAME_RESIGN, id));
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
	}
	
	public ChatMessage[] getChatHistory(String id, int limit) throws IOException {
		HttpURLConnection conn = get(String.format(Worddit.PATH_GAME_CHATHISTORY,id));
		if(getLastResponse() != Worddit.SUCCESS) return null;
		return castJson(conn, ChatMessage[].class);
	}
	
	public boolean sendChatMessage(String id, String message) throws IOException {
		post(String.format(Worddit.PATH_GAME_CHATSEND, id), Worddit.MESSAGE, message);
		if(getLastResponse() != Worddit.SUCCESS) return false;
		return true;
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
	 * Get the authentication cookie.
	 * @return cookie we're currently using, or null if there is none
	 */
	public String getCookie() {
		return mCookie;
	}
	
	/**
	 * Set the authentication cookie.
	 * @param cookie to start using
	 */
	public void setCookie(String cookie) {
		mCookie = cookie;
	}
	
	public String getURL() {
		Log.i(TAG, "String URL is: " + mURL.toExternalForm());
		return mURL.toString();
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
		Log.i(TAG, "POST " + path);
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
	
	private String collapse(List<String> strings, char delimeter) {
		StringBuffer output = new StringBuffer();
		for(Iterator<String> it = strings.iterator(); it.hasNext(); ) {
			String id = it.next();
			output.append(id);
			// Add a comma if needed
			if(it.hasNext()) output.append(delimeter);
		}
		return output.toString();
	}
	
	private <T> String collapse(T array[], char delimeter) {
		StringBuffer output = new StringBuffer();
		for(int i = 0; i < array.length; i++) {
			T bit = array[i];
			output.append(bit);
			if(i < array.length - 1) output.append(',');
		}
		return output.toString();
	}
	
	/**
	 * Initiate an HTTP GET for the given path.
	 * @param path to perform GET command to
	 * @return an <code>HttpURLConnection</code> representing the connection
	 * @throws IOException if there's big trouble.
	 */
	private HttpURLConnection get(String path) throws IOException {
		HttpURLConnection connection = HttpHelper.makeGet(mURL, path, mCookie);
		Log.i(TAG, "GET " + path);
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
		return makeSession(url,null);
	}
	
	/**
	 * Create a session which uses a provided URL as the 
	 * game server location.
	 * @param url Which URL to use as the game server.
	 * @param cookie A cookie to start using.
	 * @return Session object set up for the URL
	 * @throws MalformedURLException If the URL is invalid.
	 */
	public static Session makeSession(String url, String cookie) throws MalformedURLException {
		Session s = new Session();
		s.mURL = new URL(url);
		s.setCookie(cookie);
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

			if(result == false && s.getLastResponse() == Worddit.ERROR_CONFLICT) {
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
