package com.dp1415.ips;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.os.Bundle;

public class MapViewActivity extends Activity {
	GoogleMap map;
	boolean isStartMarked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        LatLng mcgill = new LatLng(45.504785,-73.577151);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mcgill, 15));
        map.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
            	if (!isStartMarked){
            		map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Start Location")
                    .draggable(true));
            		            		
            		isStartMarked = true;
            	} else {	//do add polyline (path)
            		map.addPolyline(new PolylineOptions()
            		.add(latLng));
            		
            		//map.
            	}
            	
            }
        });
    }

    
}