package com.reddit.worddit.adapters;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.reddit.worddit.api.APICall;
import com.reddit.worddit.api.APICallback;
import com.reddit.worddit.api.Session;

/**
 * This is an adapter that adapts lists of objects returned by com.reddit.worddit.api.Session
 * into a ListView.
 * 
 * This class abstracts the task of fetching the data initially from the cache or server
 * and providing feedback to the user that a task is happening in the background.
 * 
 * It is expected that subclasses of this actually add on their dataset-specific operations
 * that make subsequent API calls. For example, a list of friends might have a public method
 * which accepts a friendship or drops a friendship. While this is happening, the subclass
 * ought to call markUpdating(int) and markUpdated(int) so that the ListView knows to provide
 * feedback to the user about this asynchronous task.
 * @author pkilgo
 *
 */
public abstract class SessionListAdapter extends BaseAdapter {
	
	/** Context for this ListAdapter for use in resolving strings in this class and subclasses */
	protected Context mContext;
	
	/** Session object to use to make our API calls */
	protected Session mSession;
	
	/** Used in this class and subclasses to inflate UI elements from XML */
	protected LayoutInflater mInflater;

	/** This flag tells us if the ListView is fetching the data from the backend */
	private AtomicBoolean mFetching = new AtomicBoolean(false);
	
	/** These flags should be set to "true" when an item in the list is being operated upon */
	private boolean mLoadingFlags[];
	
	/** The View we display when we are busy fetching the data */
	private View mLoadingView;
	
	/**
	 * Fetches the data from the server or cache.
	 * This is filled in by subclasses of SessionListAdapter.
	 * @param callback to use once the API call has finished
	 */
	abstract protected void fetchData(APICallback callback);
	
	/**
	 * Gets the number of data item rows in this list.
	 * Normally this should be the size of the data payload itself. SessionListAdapter.getCount()
	 * is overridden, and will call this method when it needs to know how many items of data
	 * were fetched from the server.
	 * @return number of items of data (returned from server)
	 */
	abstract public int getItemCount();
	
	/**
	 * Returns the View which tells the user we are fetching the data.
	 * This View will become the only View in the list when we are fetching
	 * data from the server.
	 * @return View which tells the user we are fetching data
	 */
	abstract protected View getLoadingView();
	
	/**
	 * Returns the View which tells the user we are updating a particular item.
	 * This View will replace the normal "data item View" with a loading message
	 * that will be displayed to the user.
	 * @param position in the ListView
	 * @param convertView the cached View
	 * @param parent the parent View
	 * @return a View which tells the user we are changing that item
	 */
	abstract protected View getItemLoadingView(int position, View convertView, ViewGroup parent);
	
	/**
	 * Returns the View which represents an in-memory data item.
	 * The View should represent a data item cached in memory that isn't being
	 * updated by some background task.
	 * @param position in the ListView
	 * @param convertView the cached View
	 * @param parent the parent View
	 * @return View which represents in-memory data item
	 */
	abstract protected View getItemView(int position, View convertView, ViewGroup parent);
	
	/**
	 * Performs subclass-specific actions after the data has been fetched.
	 * This should normally be stuff like stashing the payload in memory where
	 * we could work with it.
	 * @param result boolean indicating the success of the action
	 * @param task the APICall object that completed
	 */
	abstract protected void onFetchComplete(boolean result, APICall task);
	
	/**
	 * Standard constructor.
	 * @param ctx Context to use for the ListView
	 * @param session to use to make API calls
	 */
	public SessionListAdapter(Context ctx, Session session) {
		mContext = ctx;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSession = session;
		repopulate();
	}
	
	/**
	 * Gets the number of rows in this ListView.
	 * This View overrides the count of the subclass when we are fetching the objects
	 * as there will be only one View in the ListView.
	 */
	public int getCount() {
		if(mFetching.get() == true) return 1;
		return getItemCount();
	}
	
	/**
	 * Decides which View should be in a given position in the ListView.
	 * There are a few different cases this method handles.
	 * <ul>
	 * <li>When we are fetching, the ListView will only have one item in its list
	 * 		which is a message to the user that we are fetching.</li>
	 * <li>When a particular item is being updated, we swap that item's view
	 * 		with a View that indicates this item is loading.</li>
	 * <li>When <code>convertView</code> is one of the placeholder Views described
	 * 		above and it shouldn't be anymore, we force the standard item View</li>
	 * <li>When none of these cases are met, we return a standard item View.</li>
	 * </ul>
	 * @param position the position for the View
	 * @param convertView the cached View for this position
	 * @param parent the parent for this View
	 * @return a View for the described position
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mFetching.get() == true) {
			if(mLoadingView == null) {
				mLoadingView = getLoadingView();
			}
			return mLoadingView;
		}
		else if(mLoadingFlags != null && position < mLoadingFlags.length && mLoadingFlags[position] == true) {
			return getItemLoadingView(position, convertView, parent);
		}
		else if(convertView == mLoadingView) {
			return getItemView(position, null, parent);
		}
		
		return getItemView(position, convertView, parent);
	}
	
	/**
	 * Checks if this object is fetching its payload.
	 * This method is atomic.
	 * @return
	 */
	protected boolean isFetching() {
		return mFetching.get();
	}
	
	/**
	 * Marks the item at <code>position</code> as being updated by a background task.
	 * @param position the position of item to be marked as updating 
	 */
	protected void markUpdating(int position) {
		mLoadingFlags[position] = true;
	}
	
	protected void markUpdated(int position) {
		mLoadingFlags[position] = false;
	}

	/**
	 * Gets the updating flag for the given item position.
	 * @param position the position of the flag to check
	 * @return the flag at the given position
	 */
	protected boolean isUpdating(int position) {
		return mLoadingFlags[position];
	}
	
	/**
	 * Performs the fetch operation for this ListView.
	 */
	private void repopulate() {
		// We're working on it already!
		if(mFetching.get() == true) return;
		
		mFetching.set(true);
		mLoadingFlags = null;

		fetchData(new APICallback() {
			@Override
			public void onCallComplete(boolean success, APICall task) {
				onFetchComplete(success,task);
				mFetching.set(false);
				SessionListAdapter.this.notifyDataSetChanged();
				
				// Adjust array sizes.
				mLoadingFlags = new boolean[getCount()];
			}
		});
	}
	
}
