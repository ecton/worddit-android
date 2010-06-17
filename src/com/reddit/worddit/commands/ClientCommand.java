package com.reddit.worddit.commands;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

//TODO - implement as a call back handler so this class can also receive the 
// results of the request.  A commandprocessor/commandqueue would execute 
// each request and deal with passing the results to the UI, or to the
// particular Game instance

/* Constructors in sub classes should call super() for compatibility with
 * potential changes down the road. Before calling .Post, the commandType should
 * be set to either CommandType.GET or CommandType.Post, and any required
 * arguments should be added to mRequiredArgs.
 */
/**
 * Generic class for sending commands to the server API. Actual commands are
 * implemented as subclasses of this base class.
 * 
 * ClientCommands should not be executed in the UI thread as each web request is
 * a blocking call
 */
public class ClientCommand {
	protected int mStatus;

	protected CommandType mCommandType;

	protected ArrayList<String> mRequiredArgs = new ArrayList<String>();

	protected HashMap<String, NameValuePair> mCommandArgs;

	protected String URI;

	public ClientCommand() {
		// potentially add required arguments here that would span any client
		// command.
		// device_id and client_type might be some that we want to collect in
		// every
		// request for validation or generating reports

		// mRequiredArgs.add("client_type");
		// mRequiredArgs.add("device_id");
	}

	public void AddArgument(String name, String payload) {
		if (mCommandArgs == null)
			mCommandArgs = new HashMap<String, NameValuePair>();

		mCommandArgs.put(name, new BasicNameValuePair(name, payload));
	}

	/**
	 * Set a custom URI parameter for Commands that have variable URI's Example:
	 * /user/find/:id_or_email
	 * 
	 * @param name
	 * @param value
	 */
	public void SetCustomURIParameter(String name, String value) {
		URI.replace(name, value);
	}

	public void Post() throws InvalidArgumentException,
			ClientProtocolException, IOException {
		assert (mCommandType != null);

		// verify mRequiredArgs are all present, more for debugging/sanity
		// checks.
		// In production code this should not ever fail as it is a guaranteed
		// error
		// from the server API
		for (String argName : mRequiredArgs) {
			if (!mCommandArgs.containsKey(argName))
				throw new InvalidArgumentException("Required argument "
						+ argName + " missing from command "
						+ this.getClass().getName());
		}

		// make request as Get/Post
		URI location = null;
		try {
			location = new URI(URI);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpHost httpHost = new HttpHost(location.getHost(),
				location.getPort(), location.getScheme());

		HttpClient httpClient = new DefaultHttpClient();

		// TODO: can we assume this is a GET if mCommandArgs is empty? Would
		// prevent the need
		// for us to set it in the ClientCommand subclass but may result in API
		// wonkiness
		// if the server is expecting a POST and gets a GET for a command with
		// optional parameters
		if (mCommandType == CommandType.GET)
			HandleGetRequest(httpHost, httpClient);
		else
			HandlePostRequest(httpHost, httpClient);

		httpClient.getConnectionManager().shutdown();
	}

	// make our httprequest. If we get an exception pass it up the line so we
	// our command queue can handle
	// it and alert the UI
	private void HandleGetRequest(HttpHost host, HttpClient client)
			throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(URI);

		long start = System.currentTimeMillis();
		HttpResponse response = client.execute(host, get);
		long end = System.currentTimeMillis();

		Log.d("Worddit", "HttpClient.Execute took " + (end - start) + "ms");
		HttpEntity entity = response.getEntity();

		Log.d("Worddit", response.getStatusLine().toString());

		mStatus = response.getStatusLine().getStatusCode();

		if (entity != null) {
			entity.consumeContent();
		}
	}

	// make our httprequest. If we get an exception pass it up the line so we
	// our command queue can handle
	// it and alert the UI
	private void HandlePostRequest(HttpHost host, HttpClient client)
			throws ClientProtocolException, IOException {

		HttpPost post = new HttpPost(URI);

		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.addAll(mCommandArgs.values());

		try {
			post.setEntity(new UrlEncodedFormEntity(args, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long start = System.currentTimeMillis();
		HttpResponse response = client.execute(host, post);
		long end = System.currentTimeMillis();

		// TODO: may be a bug or misconfiguration here. Takes 9 SECONDS per
		// request from my phone to complete client.execute. From the emulator
		// it is < 1 second
		Log.d("Worddit", "HttpClient.Execute took " + (end - start) + "ms");
		HttpEntity entity = response.getEntity();

		Log.d("Worddit", response.getStatusLine().toString());

		mStatus = response.getStatusLine().getStatusCode();

		if (entity != null) {
			entity.consumeContent();
		}
	}

	public enum CommandType {
		GET, POST
	};

	/**
	 * Basic test function, and demonstration of how to access the server API
	 */
	public static void Test() {
		try {
			ClientCommand uaCommand = new UserAdd();
			uaCommand.AddArgument("email", "xxxyyyzzz@gmail.com");
			uaCommand.AddArgument("device_id", "000-000-000");
			uaCommand.AddArgument("client_type", "mobile");
			uaCommand.AddArgument("password", "test");
			uaCommand.Post();

			uaCommand = new GameRack();
			uaCommand.SetCustomURIParameter(":id", "somegameid@badf00d");
			uaCommand.Post();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
