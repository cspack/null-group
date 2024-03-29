/*
 * Copyright (c) 2012, SRU Cygnus Nullstring.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

*     Redistributions of source code must retain the above copyright notice, 
		this list of conditions and the following disclaimer.
	
*     Redistributions in binary form must reproduce the above copyright notice, 
     	this list of conditions and the following disclaimer in the documentation 
     	and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */


package edu.sru.nullstring;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import edu.sru.nullstring.Data.CategoryType;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Service.LocadexService;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class LocadexApplication extends Application {

	
	public boolean useLocation = true;
	
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
	
/*
	private DatabaseHelper mDatabaseHelper;
    public final synchronized DatabaseHelper getDatabaseHelper() {
        if (mDatabaseHelper == null) {
        	mDatabaseHelper = new DatabaseHelper(this);
        }
        return mDatabaseHelper;
    }
	
    public final synchronized CategoryType getCurrentCategory() {
	private CategoryType mCurrentCategory;
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
*/
    
	private volatile DatabaseHelper databaseHelper = null;
	public volatile LocadexService service = null;
	
	  @Override
	  public void onCreate() {
	    super.onCreate();
	    
	    
	    // Check for service running
	    if(service == null)
	    {
	    	Log.e("LocadexApplication", "Starting service for it hasn't started yet.");
            Intent serviceIntent = new Intent(this, LocadexService.class);
            ComponentName c = this.startService(serviceIntent);
	    } else {
	    	Log.e("LocadexApplication", "Locadex Service already running.");
	    }
	    
    	SharedPreferences myPrefs = this.getSharedPreferences(SHAREDPREF_TITLE, MODE_WORLD_READABLE);
        useLocation = myPrefs.getBoolean("useLocation", false);
	    
	    
	  }

	  public void saveLocation()
	  {
	    	SharedPreferences myPrefs = this.getSharedPreferences(SHAREDPREF_TITLE, MODE_WORLD_READABLE);
	        SharedPreferences.Editor prefsEditor = myPrefs.edit();
	        prefsEditor.putBoolean("useLocation", useLocation);
	        prefsEditor.commit();

	  }
	  public final String SHAREDPREF_TITLE = "LOCADEX_NOTES_PREFS";
	  
	  @Override
	  public void onTerminate() {
	    if (databaseHelper != null) {
	    
	        
	        OpenHelperManager.releaseHelper();
	      databaseHelper = null;
	    }
	    super.onTerminate();
	  }

	  public DatabaseHelper getHelper() {
	    if (databaseHelper == null) {
	      databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
	    }
	    return databaseHelper;
	  }

}
