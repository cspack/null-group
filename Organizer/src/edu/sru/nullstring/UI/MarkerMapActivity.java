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
import android.view.Window;

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
