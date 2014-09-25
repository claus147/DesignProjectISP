package com.dp1415.ips;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

public class GPS implements LocationListener {

	public TextView latitude;  
    public TextView longitude;  
    
    GPS(TextView lat, TextView lon){
    	this.latitude=lat;
    	this.longitude=lon;
    }
    
	@Override
	public void onLocationChanged(Location loc) {
        latitude.setText(String.valueOf(loc.getLatitude()));  
        longitude.setText(String.valueOf(loc.getLongitude()));  
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
