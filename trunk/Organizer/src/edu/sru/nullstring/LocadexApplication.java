package edu.sru.nullstring;

import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Application;

public class LocadexApplication extends Application {

	
	// Singletons go here!
	// We will need to make some
	// for the DB and Cached data.

	// Example:
	
	/*
	 *
	 *
	private ServiceQueue mServiceQueue;
    public final synchronized ServiceQueue getServiceQueue() {
        if (mServiceQueue == null) {
            mServiceQueue = new ServiceQueue(this);
        }
        return mServiceQueue;
    }
	 */
	

	private DatabaseHelper mDatabaseHelper;
    public final synchronized DatabaseHelper getDatabaseHelper() {
        if (mDatabaseHelper == null) {
        	mDatabaseHelper = new DatabaseHelper(this);
        }
        return mDatabaseHelper;
    }
	
	
}
