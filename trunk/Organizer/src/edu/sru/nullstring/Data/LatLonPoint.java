package edu.sru.nullstring.Data;

import com.google.android.maps.GeoPoint;

public class LatLonPoint extends GeoPoint {
	private double latitude, longitude;
    public LatLonPoint(double latitude, double longitude) {
        super((int) (latitude * 1E6), (int) (longitude * 1E6));
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public LatLonPoint(GeoPoint extend) {
        super(extend.getLatitudeE6(), extend.getLongitudeE6());
        // set lat lon
        latitude = (1.0 * extend.getLatitudeE6()) / 1E6;
        longitude = (1.0 * extend.getLongitudeE6()) / 1E6;
    }

    public double getLatitude()
    {
    	return latitude;
    }
    
    public double getLongitude()
    {
    	return longitude;
    }
}
