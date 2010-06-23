package com.reddit.worddit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A class meant to help out on some of the more verbose
 * stuff when dealing with low-level HTTP connections.
 * @author OEP
 *
 */
public class HttpHelper {
	/**
	 * Create an <code>HttpURLConnection</code> object and make it post
	 * its parameters to the Worddit server.
	 * @param baseUrl The base URL of the Worddit server.
	 * @param path The path to make the POST to
	 * @param params URL-encoded parameters
	 * @param cookie to send to the server
	 * @return an <code>HttpURLConnection</code> for the connection
	 * @throws IOException
	 */
	public static HttpURLConnection makePost(URL baseUrl, String path, String params, String cookie)
	throws IOException {
		URL url = new URL(String.format("%s/%s", baseUrl.toString(), path));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		// Set cookie if it was given
		if(cookie != null) {
			connection.setRequestProperty("Cookie", cookie);
		}
		
		connection.setRequestProperty("Content-Length", Integer.toString(params.getBytes().length));
		connection.setRequestProperty("Content-Language", "en-US");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		
		// Send the request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(params);
		wr.flush();
		wr.close();
		return connection;
	}
	
	/**
	 * Construct and return an <code>HttpURLConnection</code> object
	 * to use for a GET connection.
	 * @param baseUrl The base URL of the server
	 * @param path The path to make the GET call to.
	 * @param cookie The cookie to send to the server (null if no cookie)
	 * @return an <code>HttpURLConnection</code> object representing the established connection
	 * @throws IOException if there was trouble establishing the connection
	 */
	public static HttpURLConnection makeGet(URL baseUrl, String path, String cookie)
	throws IOException {
		URL url = new URL(String.format("%s/%s", baseUrl.toString(), path));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Language", "en-US");
		
		// Set cookie if it was given
		if(cookie != null) {
			connection.setRequestProperty("Cookie", cookie);
		}
		
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(false);
		
		return connection;
	}
	
	/**
	 * Take an arbitrary list of arguments and return a URL-encoded
	 * String representation of the arguments.
	 * 		Note: This takes even-numbered lists of arguments
	 * @param args An alternating list of key/value payloads to URL-encode
	 * @return String representation of the key/value pairs, URL-encoded
	 * @throws UnsupportedEncodingException if "UTF-8" is not permissible
	 */
	public static String encodeParams(String ... args)
	throws UnsupportedEncodingException {
		if(args.length % 2 != 0) {
			throw new IllegalArgumentException("Must have a multiple of two arguments");
		}
		
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < args.length; i+=2) {
			buffer.append(URLEncoder.encode(args[i], "UTF-8"));
			buffer.append('=');
			buffer.append(URLEncoder.encode(args[i+1], "UTF-8"));
			buffer.append('&');
		}
		// Get rid of trailing '&'
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}
	
	/**
	 * Attempt to read the cookie provided by the Worddit server.
	 * This method fails silently if no cookies are provided.
	 * @param connection An HTTP connection to read the cookie from.
	 * @param name The name of the cookie to search for
	 * @return the contents of the cookie
	 */
	public static String readCookie(HttpURLConnection connection, String name) {
		Map<String,List<String>> headers = connection.getHeaderFields();
		List<String> values = headers.get("Set-Cookie");
		if(values == null) {
			System.err.println("Warning: no cookies provided");
			return null;
		}
		
		StringBuffer buf = new StringBuffer();
		for(Iterator<String> iter = values.iterator(); iter.hasNext(); ) {
			String v = iter.next();
			buf.append(v);
		}
		
		String fullCookie = buf.toString();
		int i = fullCookie.indexOf(name);
		if(i < 0) {
			System.err.printf("Warning: cookie '%s' not found\n", name);
			return null;
		}
		i += name.length();
		
		buf = new StringBuffer();
		while(i < fullCookie.length() && fullCookie.charAt(i) != ';') {
			char c = Character.toLowerCase( fullCookie.charAt(i) );
			if(c >= '0' && c <= '9' || c >= 'a' && c <= 'f')
				buf.append(c);
			i++;
		}
		
		return buf.toString();
	}
}
