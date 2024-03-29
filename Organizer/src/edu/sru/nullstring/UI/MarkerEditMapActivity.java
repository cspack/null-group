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

import java.sql.SQLException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import edu.sru.nullstring.R;
import edu.sru.nullstring.Data.CategoryAdapter;
import edu.sru.nullstring.Data.DatabaseHelper;
import edu.sru.nullstring.Data.MapItemization;
import edu.sru.nullstring.Data.MarkerType;
import edu.sru.nullstring.Data.MarkerType.MarkerState;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector.OnGestureListener;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;

public class MarkerEditMapActivity extends MapActivity {
	
	public DatabaseHelper helper = null;
	public MarkerType editItem = null;
	public static final String LOG_TAG = "MarkerEditMapActivity";
	private MapGestureDetectorOverlay mapGestures = new MapGestureDetectorOverlay();
	private MapView map;
	private MyLocationOverlay myLocationOverlay;
	
	/* Begin required maplist code */
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}

	/* End required maplist code */
	
	

public class MapGestureDetectorOverlay extends Overlay implements OnGestureListener {
		 private GestureDetector gestureDetector;
		 private OnGestureListener onGestureListener;

		 public MapGestureDetectorOverlay() {
		  gestureDetector = new GestureDetector((android.view.GestureDetector.OnGestureListener) this);
		 }

		 public MapGestureDetectorOverlay(OnGestureListener onGestureListener) {
		  this();
		  setOnGestureListener(onGestureListener);
		 }

		 @Override
		 public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		  if (gestureDetector.onTouchEvent(event)) {
		   return true;
		  }
		  return false;
		 }

		 public boolean isLongpressEnabled() {
		  return gestureDetector.isLongpressEnabled();
		 }

		 public void setIsLongpressEnabled(boolean isLongpressEnabled) {
		  gestureDetector.setIsLongpressEnabled(isLongpressEnabled);
		 }

		 public OnGestureListener getOnGestureListener() {
		  return onGestureListener;
		 }
		 
		 

		 public void setOnGestureListener(OnGestureListener onGestureListener) {
		  this.onGestureListener = onGestureListener;
		 }

		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			if(e.getPointerCount() != 1) return;


            GeoPoint longpressLocation = map.getProjection().fromPixels((int)e.getX(),
                    (int)e.getY());

            editItem.setLocation(longpressLocation);
            redrawLocationOverlay();
            
            try {
				editItem.update();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
		}

	
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
    	
    	
    	
    	map = (MapView)findViewById(R.id.mapview);

    	if(editItem.getMarkerState() == MarkerType.MarkerState.Defined)
    	{
    		GeoPoint point = editItem.getLocation();
    		map.getController().setCenter(point);
    	}
    	
    	myLocationOverlay = new MyLocationOverlay(this, map);
    	map.getOverlays().add(myLocationOverlay);



	myLocationOverlay.runOnFirstFix(new Runnable() {
		public void run() {
			map.getController().animateTo(
			  myLocationOverlay.getMyLocation());
		 	}
	});

    	
    	mapGestures.setIsLongpressEnabled(true);
        map.setBuiltInZoomControls(true);
    	map.getOverlays().add(mapGestures);

    	redrawLocationOverlay();

    }

    
    MapItemization markerCollection = null;
    public void redrawLocationOverlay()
    {
    	if(editItem.getMarkerState() == MarkerType.MarkerState.Defined)
    	{
    		// recreate
    		if(markerCollection != null)
    		{
    			map.getOverlays().remove(markerCollection);
    		}
    		
    		GeoPoint loc = editItem.getLocation();
    		OverlayItem markerPoint = new OverlayItem(loc, "Marker Position", "This is the position");
            Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
            markerCollection = new MapItemization(this, drawable);
            markerCollection.addOverlay(markerPoint);
    		map.getOverlays().add(markerCollection);
    		
            int lat = loc.getLatitudeE6();
            int lon = loc.getLongitudeE6();
            int minLat = lat, maxLat = lat, minLon = lon, maxLon = lon;

            int bufLat = 3000;
            int bufLon = 3000;
      	  	minLat -= bufLat;
	      	maxLat += bufLat;
	
	      	minLon -= bufLon;
	      	maxLon += bufLon;

      	  map.getController().zoomToSpan(Math.abs(maxLat - minLat), Math.abs(maxLon - minLon));
            map.getController().animateTo(new GeoPoint( (maxLat + minLat)/2, 
            (maxLon + minLon)/2 )); 

    	}
    	
    }
    
    
    public void refreshData()
    {

    	if(editItem.getMarkerState() == MarkerState.Defined)
    	{
    		GeoPoint point = editItem.getLocation();	
    		map.getController().setCenter(point);
    		redrawLocationOverlay();
    	}
    	
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// This is super important, activity will be paused and removed w/out being destroyed
		refreshData();

	}
    
}
