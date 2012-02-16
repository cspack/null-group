package edu.sru.nullstring.UI;

import com.google.android.maps.MapActivity;

import edu.sru.nullstring.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MarkerMapActivity extends MapActivity {

	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);

        //Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        setContentView(R.layout.googlemap);

    }
	
}
