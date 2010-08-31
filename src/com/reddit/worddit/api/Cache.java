package com.reddit.worddit.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.reddit.worddit.api.response.Profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class Cache {
	
	/** The base directory where Worddit stores all its cache info */
	protected File mDirectory;
	
	/** Helper directory reference */
	protected File mAvatars, mProfiles, mFriends;
	
	/**
	 * Constructor to create a Cache in the specified base directory.
	 * @param base directory to put the cache
	 * @throws IOException if we can't make that cache
	 */
	protected Cache(File base) throws IOException {
		makeDirectory(base);
		if(base.canRead() == false || base.canWrite() == false) {
			throw new IOException(String.format("Need read/write access to %s", base.getAbsolutePath()));
		}
		
		mDirectory = base;
		initializeCache();
	}
	
	/**
	 * Returns true if the cache is useable.
	 * @return true if useable
	 */
	public boolean isUsable() {
		return mDirectory.isDirectory() && mDirectory.canWrite()
			&& mAvatars.isDirectory() && mAvatars.canWrite()
			&& mProfiles.isDirectory() && mProfiles.canWrite()
			&& mFriends.isFile() && mFriends.canWrite();
	}
	
	/**
	 * Returns true if the cached avatar exists, is readable, and isn't outdated.
	 * @param url URL to see if we have cached
	 * @return true if the avatar is cached
	 */
	public boolean hasAvatar(String url) {
		String file = getAvatarFilename(url);
		File avatar = new File(mAvatars, file);
		return avatar.isFile() && avatar.canRead() && !isOutdated(avatar);
	}
	
	public boolean hasProfile(String id) {
		String file = getProfileFilename(id);
		File profile = new File(mProfiles, file);
		return profile.isFile() && profile.canRead()
			&& !isOutdated(profile) && isType(profile, Profile.class);
	}
	
	/**
	 * Returns true if we can use the friends cache to return friends.
	 * @return true if we can use it
	 */
	public boolean hasFriends() {
		List<String> ids = readFriends();
		
		if(ids == null)
			return false;
		
		// Make sure we have each profile.
		for(String id : ids) {
			if(hasProfile(id) == false) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the avatar from the cache
	 * @param url URL to read avatar from
	 * @return cached version of the avatar
	 */
	public Bitmap getAvatar(String url) {
		String file = getAvatarFilename(url);
		File avatar = new File(mAvatars, file);
		return BitmapFactory.decodeFile(avatar.getAbsolutePath());
	}
	
	/**
	 * Returns the profile from the cache
	 * @param id ID of the profile
	 * @return cached version of the profile
	 */
	public Profile getProfile(String id) {
		String file = getAvatarFilename(id);
		File profile = new File(mProfiles, file);
		Gson gson = new Gson();
		try {
			return gson.fromJson(
					new InputStreamReader(new FileInputStream(profile)),
					Profile.class);
		} catch (JsonParseException e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public Profile[] getFriends() {
		List<String> ids = readFriends();
		if(ids == null)
			return null;
		
		Profile friends[] = new Profile[ids.size()];
		
		for(int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			if(hasProfile(id) == false)
				return null;
			
			friends[i] = getProfile(id);
		}
		
		return friends;
	}
	
	protected List<String> readFriends() {
		try {
			FileReader reader = new FileReader(mFriends);
			BufferedReader input = new BufferedReader(reader);
			String line;
			ArrayList<String> ids = new ArrayList<String>();
			
			// Read only relevant characters from file.
			// If there are illegal characters, forget it.
			while((line = input.readLine()) != null) {
				char chars[] = line.toCharArray();
				for(char c : chars) {
					if(Cache.FRIEND_FILE_CHARS.indexOf(c) < 0) {
						reader.close();
						return null;
					}
				}
				
				// Add it up!
				ids.add(line);
			}
			
			// Close our reader.
			reader.close();
			
			return ids;
		}
		catch(IOException e) {
			return null;
		}
	}
	
	/**
	 * Saves provided avatar in the cache.
	 * @param url URL to make a hash from
	 * @param avatar Avatar to save in the cache
	 * @return true if the operation succeeded
	 */
	public boolean cacheAvatar(String url, Bitmap avatar) {
		String file = getAvatarFilename(url);
		File out = new File(mAvatars, file);
		
		makeFile(out);
		
		Bitmap resized = resizeBitmap(avatar);
		
		try {
			FileOutputStream stream = new FileOutputStream(out);
			resized.compress(Bitmap.CompressFormat.PNG, 90, stream);
			return true;
		}
		catch(FileNotFoundException e) {
			return false;
		}
	}
	
	public boolean cacheFriends(Profile friends[]) {
		boolean flag = true;
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < friends.length; i++) {
			flag &= cacheProfile(friends[i]);
			buffer.append(friends[i].id);
			
			if(i < friends.length - 1) {
				buffer.append("\n");
			}
		}
		
		// Try to make the new friends list.
		if(makeFile(mFriends,true) == false) return false;
		return write(mFriends, buffer.toString().getBytes());
	}
	
	/**
	 * Caches the passed profiles.
	 * @param profiles The profiles to cache.
	 * @return true if all cached successfully
	 */
	public boolean cacheProfiles(Profile profiles[]) {
		boolean flag = true;
		for(Profile profile : profiles) {
			flag &= cacheProfile(profile);
		}
		return flag;
	}
	
	public boolean cacheProfile(Profile profile) {
		File out = new File(mProfiles, getProfileFilename(profile.id));
		
		// Create new profile, delete if any exists.
		makeFile(out,true);
		
		Gson gson = new Gson();
		String json = gson.toJson(profile);
		return write(out, json.getBytes());
	}
	
	/**
	 * Resizes the provided avatar in keeping with aspect ratio
	 * if the avatar exceeds a maximum dimension.
	 * @param source Avatar to resize
	 * @return the resized avatar, or the original if it didn't need resizing
	 */
	protected Bitmap resizeBitmap(Bitmap source) {
		int width = source.getWidth();
		int height = source.getHeight();
		int newWidth, newHeight;
		float scale;
		
		if(width < MAX_AVATAR_DIM && height < MAX_AVATAR_DIM) return source;
		
		if(width < height) {
			newHeight = MAX_AVATAR_DIM;
			scale = ((float) newHeight) / height;
			newWidth = (int) (scale * width);
		}
		else {
			newWidth = MAX_AVATAR_DIM;
			scale = ((float) newWidth) / width;
			newHeight = (int) (scale * height);
		}
		
		return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
	}
	
	/**
	 * Checks if the provided file is outdated.
	 * @param f The file to check if it is outdated.
	 * @return true if the provided file is outdated.
	 */
	protected boolean isOutdated(File f) {
		return (System.currentTimeMillis() - f.lastModified() > TIME_OUTDATED);
	}
	
	/**
	 * Checks if the JSON contained in f is
	 * parseable and is of the provided type.
	 * @param <T> type to check for
	 * @param f file which contains Json
	 * @param type type to check for
	 * @return true if it is of type and not null
	 */
	protected <T> boolean isType(File f, Class<T> type) {
		try {
			FileInputStream is = new FileInputStream(f);
			Reader r = new InputStreamReader(is);
			Gson gson = new Gson();
			return gson.fromJson(r, type) != null;
		} catch (JsonParseException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Get the "avatar filename" of a provided URL.
	 * Created from the hash of the URL.
	 * @param url to create a hash of.
	 * @return Filename of the avatar.
	 */
	protected String getAvatarFilename(String url) {
		return String.format("%s.png", getHash(url));
	}
	
	/**
	 * Gets the "profile filename" of a provided profile id.
	 * @param id the id of the profile
	 * @return Filename of the profile
	 */
	protected String getProfileFilename(String id) {
		return String.format("%s.json", id.toLowerCase());
	}
	
	/**
	 * Gets a hash of a String in hexadecimal format.
	 * @param name String to take a hash of.
	 * @return Hash of the string.
	 */
	protected String getHash(String name) {
		if(name == null) name = "";
		return Integer.toHexString( name.hashCode() );
	}
	
	/**
	 * Initializes the object, created necessary
	 * directories and helper directory hooks.
	 */
	protected void initializeCache() {
		makeDirectory(mDirectory);
		mAvatars = new File(mDirectory, DIR_AVATARS);
		mProfiles = new File(mDirectory, DIR_PROFILES);
		mFriends = new File(mDirectory, FILE_FRIENDS);
		
		makeDirectory(mAvatars);
		makeDirectory(mProfiles);
		makeFile(mFriends);
	}
	
	protected boolean write(File file, byte bytes[]) {
		try {
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(bytes);
			return true;
		}
		catch(FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Creates a directory if it doesn't already exist,
	 * or deletes if that filename is not a directory.
	 * @param dir Directory to create
	 * @return true if it succeeded
	 */
	protected boolean makeDirectory(File dir) {
		if(dir.isDirectory()) return true;
		if(dir.exists()) dir.delete();
		return dir.mkdir();
	}
	
	/**
	 * Creates specified file if it didn't already
	 * exist, or deletes it if the path specified was
	 * a directory.
	 * @param file The file to create.
	 * @return true upon success
	 */
	protected boolean makeFile(File file) {
		return makeFile(file,false);
	}
	
	/**
	 * Creates specified file or deletes it if the path specified was
	 * a directory.
	 * @param file The file to create.
	 * @param delete Flag if we should delete an existing file
	 * @return true if the operation succeeded.
	 */
	protected boolean makeFile(File file, boolean delete) {
		if(file.isFile() && delete == true) {
			if(file.delete() == false) return false;
			return makeFile(file, delete);
		}
		else if(delete == false) {
			return true;
		}
		
		if(file.isDirectory()) {
			if(deleteDir(file) == false) {
				return false;
			}
		}
		
		try {
			return file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Recursively deletes a directory.
	 * @param dir The directory to delete.
	 * @return true if the operation succeeded
	 */
	protected boolean deleteDir(File dir) {
		if(dir.isDirectory() == false) return false;
		String children[] = dir.list();
		
		for(String child : children) {
			boolean success = deleteDir(new File(dir, child));
			if(success == false) return false;
		}
		
		return dir.delete();
	}
	
	/**
	 * Creates a cache using the default cache directory.
	 * @return the created Cache object
	 * @throws IOException if there is a problem
	 */
	public static Cache makeCache() throws IOException {
		return new Cache(new File(Environment.getExternalStorageDirectory(), DIR_WORDDIT));
	}
	
	/**
	 * Creates a cache based on the specified directory 
	 * @param base The base cache directory to use
	 * @return the created Cache object
	 * @throws IOException if there is a problem
	 */
	public static Cache makeCache(File base) throws IOException {
		return new Cache(base);
	}
	
	public static final long
		TIME_OUTDATED = 1000 * 60 * 60 * 24;
	
	public static final int
		MAX_AVATAR_DIM = 100;
	
	public static final String
		DIR_WORDDIT = "worddit",
		DIR_AVATARS = "avatars",
		DIR_PROFILES = "profiles";
	
	public static final String
		FILE_FRIENDS = "friends.lst";
	
	public static final String
		FRIEND_FILE_CHARS = "ABCDEFabcdef0123456789";
}
