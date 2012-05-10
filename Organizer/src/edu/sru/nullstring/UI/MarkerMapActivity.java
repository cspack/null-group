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

package edu.sru.nullstring.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.MapItemization;
import edu.sru.nullstring.Data.MarkerAdapter;
import edu.sru.nullstring.Data.MarkerType;
import edu.sru.nullstring.Data.MarkerType.MarkerState;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MarkerMapActivity extends MapActivity {

	/* Begin required orm lite code */
	private DatabaseHelper databaseHelper = null;
	private MapView map;

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

	private MarkerAdapter mapPoints = null;
	private DatabaseHelper helper = null;
	
	private List<MarkerType> items = new ArrayList<MarkerType>();
	public void refreshMarkers()
	{
		
		//this: ugly, redundant, bad software engineering, but thorough
		
		mapPoints.refreshData();
		
		items.clear();
		
		int size = mapPoints.getCount();
		for(int i = 0;i<size;i++)
		{
			items.add(mapPoints.getMarker(i));
		}
		
		if(size == 0)
		{
			map.setVisibility(View.GONE);
			TextView noPoints = (TextView)findViewById(R.id.nopoints);
			noPoints.setVisibility(View.VISIBLE);
		}else
		{

			map.setVisibility(View.VISIBLE);
			TextView noPoints = (TextView)findViewById(R.id.nopoints);
			noPoints.setVisibility(View.GONE);
		}
		
		redrawLocationOverlay();
		
	}
	
    MapItemization markerCollection = null;
    public void redrawLocationOverlay()
    {

        	// recreate
    		if(markerCollection != null)
    		{
    			map.getOverlays().remove(markerCollection);
    		}

    		// don't move map if no items.
            if(items.size() >= 1)
            {
    		
            Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
            markerCollection = new MapItemization(this, drawable);
            markerCollection.setOnFocusChangeListener(new OnFocusChangeListener()
            {

				public void onFocusChanged(ItemizedOverlay overlay,
						OverlayItem newFocus) {
					
					if(newFocus == null) return;
					// TODO Auto-generated method stub
					int markerId = Integer.parseInt(newFocus.getSnippet());
					
					for(MarkerType m : items)
					{
						if(m.getID() == markerId && mainActivity != null)
						{
					        mainActivity.openEditorActivity(m);	
						}
					}
					
				}
            	
            });
            
            int minLat = Integer.MAX_VALUE;
            int maxLat = Integer.MIN_VALUE;
            int minLon = Integer.MAX_VALUE;
            int maxLon = Integer.MIN_VALUE;

            for(MarkerType m : items)
    		{
    		
            	if(m.getMarkerState() == MarkerState.Defined)
            	{
		    		GeoPoint loc = m.getLocation();
		    		
	                  int lat = loc.getLatitudeE6();
	                  int lon = loc.getLongitudeE6();

	                  maxLat = Math.max(lat, maxLat);
	                  minLat = Math.min(lat, minLat);
	                  maxLon = Math.max(lon, maxLon);
	                  minLon = Math.min(lon, minLon);

	                  int bufLat = 3000;
	                  int bufLon = 3000;
	                  if(maxLat-minLat < bufLat)
	                  {
	                	  minLat -= bufLat;
	                	  maxLat += bufLat;
	                  }
	                  if(maxLon-minLon < bufLon)
	                  {
	                	  minLon -= bufLon;
	                	  maxLon += bufLon;
	                  }

	                  
		    		OverlayItem markerPoint = new OverlayItem(loc, m.getTitle(), Integer.toString(m.getID()));
		            markerCollection.addOverlay(markerPoint);
    		}

            map.getOverlays().add(markerCollection);
    		
	            map.getController().zoomToSpan(Math.abs(maxLat - minLat), Math.abs(maxLon - minLon));
	            map.getController().animateTo(new GeoPoint( (maxLat + minLat)/2, 
	            (maxLon + minLon)/2 )); 
            }
    		
    		
    	}
            
            
    }
    	
    
            private MarkerMainActivity mainActivity;
	

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);

        //Remove title bar
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        setContentView(R.layout.googlemap);

        
        // Setup Database
        
        helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        
        mainActivity = (MarkerMainActivity)this.getParent();

        
        mapPoints = new MarkerAdapter(this,
        		android.R.layout.simple_list_item_1, helper);

        map = (MapView)findViewById(R.id.mapview);
        // tweak settings
        map.setBuiltInZoomControls(true);

    }

	@Override
	protected void onResume() {
		super.onResume();

		
		// This one doesn't show my location, clear overlays.
		map.getOverlays().clear();
		
        // Refresh DB        
		refreshMarkers();
	}
    
    
	
}
