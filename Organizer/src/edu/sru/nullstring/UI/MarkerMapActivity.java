package edu.sru.nullstring.UI;

import com.google.android.maps.MapActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MarkerMapActivity extends MapActivity {

	/* Begin required orm lite code */
	private DatabaseHelper databaseHelper = null;

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (databaseHelper != null) {
	        OpenHelperManager.releaseHelper();
	        databaseHelper = null;
	    }
	}

	private DatabaseHelper getHelper() {
	    if (databaseHelper == null) {
	        databaseHelper =
	            OpenHelperManager.getHelper(this, DatabaseHelper.class);
	    }
	    return databaseHelper;
	}
	/* End required orm lite code */
	
	/* Begin required maplist code */
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

	/* End required maplist code */


    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);

        //Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        setContentView(R.layout.googlemap);

    }
	
}
