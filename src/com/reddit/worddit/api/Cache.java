package com.reddit.worddit.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
	 * Get the "avatar filename" of a provided URL.
	 * Created from the hash of the URL.
	 * @param url to create a hash of.
	 * @return Filename of the avatar.
	 */
	protected String getAvatarFilename(String url) {
		return String.format("%s.png", getHash(url));
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
	 * @return true if the operation succeeded.
	 */
	protected boolean makeFile(File file) {
		if(file.isFile()) return true;
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
		FILE_FRIENDS = "friends.db";
}
