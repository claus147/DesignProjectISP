package com.dp1415.ips;

import java.io.IOException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.media.MediaRouter.RouteCategory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MapViewActivity extends Activity {
	GoogleMap map;
	boolean isStartMarked = false;
	private Button confirmOrientation;
	private Button redoOrientation;
	private PolylineOptions route;
	private LatLng currentLoc = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        confirmOrientation = (Button) findViewById(R.id.confirmOrientation);
		redoOrientation = (Button) findViewById(R.id.redoOrientation);
		route = new PolylineOptions();
		//MarkerOptions startLocation = new MarkerOptions();
		
        // Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setCompassEnabled(false);
        
        LatLng mcgill = new LatLng(45.504785,-73.577151);		//lat lon coordinates of McGill

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mcgill, 15));	//15 is a good zoom level
        map.setOnMapClickListener(new OnMapClickListener() {	//add pin on click
        	
        	//PolylineOptions route = new PolylineOptions();
        	MarkerOptions startLocation = new MarkerOptions();
            @Override
            public void onMapClick(LatLng latLng) {
            	if (!isStartMarked){
            		map.addMarker(startLocation
                    .position(latLng)
                    .title("Start Location")
                    .draggable(true));
            		route.add(latLng);
            		currentLoc = latLng;
            		            		
            		isStartMarked = true;
            	} else {	//do add polyline (path)
            		route.add(latLng);
            		map.addPolyline(route);
            		//map.addMarker(startLocation.draggable(false));

            	}
            	
            }
        });
    }
    
    public void onConfirmOrientationClick(View view) {
    	map.getUiSettings().setRotateGesturesEnabled(false);
    	confirmOrientation.setVisibility(View.GONE);
    	redoOrientation.setVisibility(View.VISIBLE);
	}
    
    public void onRedoOrientationClick(View view) {
    	map.getUiSettings().setRotateGesturesEnabled(true);
    	confirmOrientation.setVisibility(View.VISIBLE);
    	redoOrientation.setVisibility(View.GONE);
	}
    
    //adds a polyline in the direction the camera is facing 1m ahead
    //changes the view of the camera to that location
    //Apparently this works
    /*
     * If your displacements aren't too great (less than a few kilometers) and 
     * you're not right at the poles, use the quick and dirty estimate that 
     * 111,111 meters (111.111 km) in the y direction is 1 degree (of latitude) and 
     * 111,111 * cos(latitude) meters in the x direction is 1 degree (of longitude).
     */
    public void onGoForwardClick(View view) {	
    	
    	//float bearing = map.getCameraPosition().bearing;
    	//currentLoc = new LatLng(currentLoc.latitude,currentLoc.longitude);
    	
	}
    
    public void onGoTurnClick(View view) {
    	
	}


    
}