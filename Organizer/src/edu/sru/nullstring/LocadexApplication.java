package edu.sru.nullstring;

import java.sql.SQLException;
import java.util.List;

import edu.sru.nullstring.Data.CategoryType;
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
	
	private CategoryType mCurrentCategory;
    public final synchronized CategoryType getCurrentCategory() {
        if (mCurrentCategory == null) {
        	List<CategoryType> sel;
			try {
				sel = mDatabaseHelper.getCategoryDao().queryForEq("current", true);
	        	if(sel.size() > 0)
	        	{
	        		// first selected result
	        		mCurrentCategory = sel.get(0);
	        	}
	        	else
	        	{
	        		// no first results, query for first result
	        		sel = mDatabaseHelper.getCategoryDao().queryForAll();
		        	if(sel.size() > 0)
		        	{
		        		mCurrentCategory = sel.get(0);
		        	}
		        	else
		        	{
		        		// no results, create one.
		        		mCurrentCategory = new CategoryType(mDatabaseHelper.getCategoryDao());
		        		mCurrentCategory.setTitle("Unsorted");
		        		mCurrentCategory.create();
		        	}
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
        }
        return mCurrentCategory;
    }

    
}
