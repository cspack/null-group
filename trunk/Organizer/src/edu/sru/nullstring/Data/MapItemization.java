package edu.sru.nullstring.Data;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import edu.sru.nullstring.UI.MarkerEditMapActivity;

public class MapItemization extends ItemizedOverlay<OverlayItem> {

    /**
	 * 
	 */
	private final Context context;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    public MapItemization(Context context, Drawable defaultMarker) {

        super(boundCenterBottom(defaultMarker));
        // super(defaultMarker);
		this.context = context;

    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    @Override
    public int size() {
        return mOverlays.size();
    }


    
    
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event, mapView);
	}
	
	
    public OnFocusChangeListener tapListener = null;
	
		@Override
	protected boolean onTap(int index) {
		boolean result = super.onTap(index);
		
		OverlayItem i = mOverlays.get(index);
		if(tapListener != null)
			tapListener.onFocusChanged(this, i);
		return result;
	}

	public void setOnTapListener(OnFocusChangeListener l) {
		this.tapListener = l;
	}
    
    

}