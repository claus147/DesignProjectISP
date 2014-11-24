package com.dp1415.ips;

import java.io.IOException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.media.MediaRouter.RouteCategory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MapViewActivity extends Activity {
	
	private static final String TAG = "MapView";
	GoogleMap map;
	boolean isStartMarked = false;
	private Button confirmOrientation;
	private Button redoOrientation;
	private EditText turnAngle;
	private Button goForward;
	private Button turn;
	private PolylineOptions route;
	private LatLng currentLoc = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        confirmOrientation = (Button) findViewById(R.id.confirmOrientation);
		redoOrientation = (Button) findViewById(R.id.redoOrientation);
		turnAngle = (EditText) findViewById(R.id.angle);
		goForward = (Button) findViewById(R.id.goForward);
		turn = (Button) findViewById(R.id.turn);
		
		route = new PolylineOptions();
		//MarkerOptions startLocation = new MarkerOptions();
		
        // Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setCompassEnabled(false);
        
        LatLng mcgill = new LatLng(45.504785,-73.577151);		//lat lon coordinates of McGill
        
        //LatLng dist = new LatLng(45.504885,-73.577051);
        //checking distance
//        map.addMarker(new MarkerOptions()
//                .position(mcgill)
//                .title("mcgill"));
//        map.addMarker(new MarkerOptions()
//        		.position(dist)
//        		.title("new distance"));
        
        
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
            		currentLoc = latLng;	//current loc is the clicked start point
            		            		
            		isStartMarked = true;
            		
            		confirmOrientation.setEnabled(true); 		//can only confirm if we put a point down
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
    	CameraPosition curPos = map.getCameraPosition();
    	CameraPosition newPos = CameraPosition.builder(curPos).target(currentLoc).build();
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
    	confirmOrientation.setVisibility(View.GONE);
    	redoOrientation.setVisibility(View.VISIBLE);
    	
    	turn.setVisibility(View.VISIBLE);
    	turnAngle.setVisibility(View.VISIBLE);
    	goForward.setVisibility(View.VISIBLE);
	}
    
    public void onRedoOrientationClick(View view) {
    	map.getUiSettings().setRotateGesturesEnabled(true);
    	confirmOrientation.setVisibility(View.VISIBLE);
    	redoOrientation.setVisibility(View.GONE);
    	
    	turn.setVisibility(View.GONE);
    	turnAngle.setVisibility(View.GONE);
    	goForward.setVisibility(View.GONE);
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
    
    //for the purpose of testing will add 0.0001 to lat (if moving directly north)
    public void onGoForwardClick(View view) {	
    	
    	CameraPosition curPos = map.getCameraPosition();
    	float bearing = curPos.bearing;
    	double newLat = currentLoc.latitude + 0.0001*Math.cos(Math.toRadians(bearing));	//calculating new angles
    	double newLng = currentLoc.longitude + 0.0001*Math.sin(Math.toRadians(bearing));
    	
    	LatLng newLoc = new LatLng(newLat,newLng);
    	route.add(newLoc);
		map.addPolyline(route);//adding the polyline
		
		CameraPosition newPos = CameraPosition.builder(curPos).target(newLoc).build();	//move camera
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
		
    	currentLoc = newLoc;
    	//currentLoc = new LatLng(currentLoc.latitude,currentLoc.longitude);
    	
	}
    //VERY GOOD RESOURCE http://stackoverflow.com/questions/14320015/android-maps-auto-rotate
    public void onTurnClick(View view) {	
    	
    	CameraPosition curPos = map.getCameraPosition();
    	CameraPosition newPos = CameraPosition.builder(curPos).bearing(curPos.bearing + Float.parseFloat(turnAngle.getText().toString())).build();
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
	}


    
}