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