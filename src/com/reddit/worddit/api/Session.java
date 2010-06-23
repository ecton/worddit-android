package com.reddit.worddit.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<Object> getGames() throws IOException {
		HttpURLConnection conn = get(Worddit.PATH_USER_GAMES);
		int response = conn.getResponseCode();
		if(response != Worddit.SUCCESS) return null;
		// TODO: JSON stuff here.
		return new ArrayList<Object>();
	}
	
	public List<Object> getFriends() {
		// TODO: Implement getFriends
		return null;
	}
	
	public Object findFriend(String id) {
		// TODO: Implement findFriend
		return null;
	}
	
	public boolean befriend(String id) {
		// TODO: Implement befriend
		return false; // You can't have any friends, silly.
	}
	
	public boolean defriend(String id) {
		// TODO: Implement defriend
		return false;
	}
	
	public boolean acceptFriend(String id) {
		// TODO: Implement acceptFriend
		return false;
	}
	
	public Object newGame(List<String> ids, List<String> rules) {
		// TODO: Implement newGame
		return null;
	}
	
	public Object requestGame(int players, List<String> rules) {
		// TODO: Implement requestGame
		return null;
	}
	
	public boolean acceptGame(String id) {
		// TODO: Implement acceptGame
		return false;
	}
	
	public boolean rejectGame(String id) {
		// TODO: Implement rejectGame
		return false;
	}
	
	public Object getBoard(String id) {
		// TODO: Implement getBoard
		return null;
	}
	
	public List<Object> getRack(String id) {
		// TODO: Implement getRack
		return null;
	}
	
	public List<Object> getGameHistory(String id, int limit) {
		// TODO: Implement getGameHistory
		return null;
	}
	
	public Object play(String id, int row, int column, boolean isVertical, List<Object> tiles) {
		// TODO: Implement play
		return null;
	}
	
	public Object swap(String id, List<Object> tiles) {
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
	
	public List<Object> getChatHistory(String id, int limit) {
		// TODO: Implement getChatHistory
		return null;
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
