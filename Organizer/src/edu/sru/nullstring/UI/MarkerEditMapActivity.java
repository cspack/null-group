package edu.sru.nullstring.UI;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.MarkerType;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;

public class MarkerEditMapActivity extends MapActivity {
	
	public DatabaseHelper helper = null;
	public MarkerType editItem = null;
	public static final String LOG_TAG = "MarkerEditMapActivity";

	
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

        Activity act = this.getParent();
    	if(act instanceof MarkerEditActivity)
    	{
    		MarkerEditActivity mact = (MarkerEditActivity)act; 
    		editItem = mact.editItem;
    		helper = mact.helper;
    	}
    	else
    	 {
    		// WTF
    		Log.e(LOG_TAG, "The parent isn't a marker edit activity. AW MAN!");
    		return;
    	}
    	
    	
    	
    	MapView map = (MapView)findViewById(R.id.mapview);

    	if(editItem.getMarkerState() == MarkerType.MarkerState.Defined)
    	{
    		//map.getController().setCenter(point)
    	}
    	

    	
    	map.setOnLongClickListener(new OnLongClickListener(){

			public boolean onLongClick(View v) {
				// set location based on point
				return false;
			}
    	
    	});

    }
	
}
