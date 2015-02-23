package com.dp1415.ips;

import java.io.IOException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRouter.RouteCategory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MapViewActivity extends ActionBarActivity{
	
	private static final String TAG = "MapView";
	GoogleMap map;
	private boolean isStartMarked = false;
	private Button confirmOrientation;
	private Button redoOrientation;
	private EditText turnAngle;
	private EditText distance;
	private Button goForward;
	private Button turn;
	private Button move;
	private PolylineOptions route;
	private Polyline line;
	private LatLng currentLoc = null;
	private Marker start = null;
	//private LatLng start = null;
	
	private double [] expectation = null;
	
	public boolean isAutomatic = false; //using formulas to move things on the map
	
	Intent i;
	MyReceiver myReceiver=null;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        //setting all variables with their corresponding R.id's
        confirmOrientation = (Button) findViewById(R.id.confirmOrientation);
		redoOrientation = (Button) findViewById(R.id.redoOrientation);
		turnAngle = (EditText) findViewById(R.id.angle);
		goForward = (Button) findViewById(R.id.goForward);
		turn = (Button) findViewById(R.id.turn);
		distance = (EditText) findViewById(R.id.dist);
		move = (Button) findViewById(R.id.move);
		
		route = new PolylineOptions();
		
        // Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setCompassEnabled(false);
        map.setMyLocationEnabled(false);
        
        //default map display position
        LatLng mcgill = new LatLng(45.504785,-73.577151);		//lat lon coordinates of McGill
      
        //map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mcgill, 15));	//15 is a good zoom level
        map.setOnMapClickListener(new OnMapClickListener() {	//add pin on click
        	
        	//PolylineOptions route = new PolylineOptions();
        	MarkerOptions startLocation = new MarkerOptions();
            @Override
            public void onMapClick(LatLng latLng) {
            	if (!isStartMarked){
            		start = map.addMarker(startLocation
                    .position(latLng)
                    .title("Start Location")
                    .draggable(true));
            		

            		start.setPosition(latLng);	//needed in order to properly update position when repositioning, otherwise it thinks marker always at original start position
            		currentLoc = latLng;	//current loc is the clicked start point
            		            		
            		isStartMarked = true;
            		
            		confirmOrientation.setEnabled(true); 		//can only confirm if we put a point down
            	
            	}
            	//start = latLng;
            	
            }
        });
        
        i= new Intent(this, com.dp1415.ips.SensorService.class);
    }
    
    
    
    public void onConfirmOrientationClick(View view) {
    	currentLoc = start.getPosition();
    	route.add(currentLoc);
    	
    	map.getUiSettings().setRotateGesturesEnabled(false);
    	CameraPosition curPos = map.getCameraPosition();
    	CameraPosition newPos = CameraPosition.builder(curPos).target(currentLoc).build();
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
    	confirmOrientation.setVisibility(View.GONE);
    	redoOrientation.setVisibility(View.VISIBLE);
    	
    	turn.setVisibility(View.VISIBLE);
    	turnAngle.setVisibility(View.VISIBLE);
    	goForward.setVisibility(View.VISIBLE);
    	move.setVisibility(View.VISIBLE);
    	distance.setVisibility(View.VISIBLE);
	}
    
    public void onRedoOrientationClick(View view) {
    	map.getUiSettings().setRotateGesturesEnabled(true);
    	
    	CameraPosition curPos = map.getCameraPosition();
    	
    	CameraPosition newPos = CameraPosition.builder(curPos).target(currentLoc).build();	//move camera (changing only focus, every other option stays same)
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
    	
    	line.remove(); //delete the one line
    	
    	route = new PolylineOptions();//clear old polyline options
    	    	
    	confirmOrientation.setVisibility(View.VISIBLE);
    	redoOrientation.setVisibility(View.GONE);
    	
    	turn.setVisibility(View.GONE);
    	turnAngle.setVisibility(View.GONE);
    	goForward.setVisibility(View.GONE);
    	move.setVisibility(View.GONE);
    	distance.setVisibility(View.GONE);
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
    	if (line != null)
    		line.remove(); // get rid of old line
    	route.add(newLoc);
    	line = map.addPolyline(route);//adding the polyline
		
		CameraPosition newPos = CameraPosition.builder(curPos).target(newLoc).build();	//move camera
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
		
    	currentLoc = newLoc;
    	//currentLoc = new LatLng(currentLoc.latitude,currentLoc.longitude);
    	Toast.makeText(this, Float.toString(map.getCameraPosition().bearing), Toast.LENGTH_SHORT).show();

    	
	}
    //VERY GOOD RESOURCE http://stackoverflow.com/questions/14320015/android-maps-auto-rotate
    public void onTurnClick(View view) {	
    	
    	CameraPosition curPos = map.getCameraPosition();
    	CameraPosition newPos = CameraPosition.builder(curPos).bearing(curPos.bearing + Float.parseFloat(turnAngle.getText().toString())).build();
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
    	
    	Toast.makeText(this, Float.toString(map.getCameraPosition().bearing), Toast.LENGTH_SHORT).show();
	}
    
    //http://stackoverflow.com/questions/2839533/adding-distance-to-a-gps-coordinate
    public void onMoveClick(View view) {	
    	
    	CameraPosition curPos = map.getCameraPosition();
    	float bearing = Float.parseFloat(turnAngle.getText().toString());
//    	double newLat = currentLoc.latitude + (180.0/Math.PI)*(Float.parseFloat(distance.getText().toString())/6378137);//6378137 earths radius at equator
//    	double newLng = currentLoc.longitude + (180.0/Math.PI)*(Float.parseFloat(distance.getText().toString())/6378137);///Math.cos(Math.PI/180.0*currentLoc.longitude);
    	
    	double newLat = currentLoc.latitude + (180.0/Math.PI)*(Float.parseFloat(distance.getText().toString())/6378137.0)*Math.cos(Math.toRadians(bearing));//6378137 earths radius at equator
    	double newLng = currentLoc.longitude + (180.0/Math.PI)*(Float.parseFloat(distance.getText().toString())/6378137.0)*Math.sin(Math.toRadians(bearing));///Math.cos(Math.PI/180.0*currentLoc.longitude);
    	
    	LatLng newLoc = new LatLng(newLat,newLng);
    	if (line != null)
    		line.remove(); // get rid of old line
    	route.add(newLoc);
		line = map.addPolyline(route);//adding the polyline
		
		CameraPosition newPos = CameraPosition.builder(curPos).target(newLoc).build();	//move camera
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
		
    	currentLoc = newLoc;
    	//currentLoc = new LatLng(currentLoc.latitude,currentLoc.longitude);
    	
	}
	
    //display the action bar (menu) from xml file
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
    
    //for action bar interactions (top menu)
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_debug:
	        	Intent intent = new Intent(this,MainActivity.class);
	    		startActivity(intent);
	            return true;
	        case R.id.action_settings:
	        	Intent intent2 = new Intent(this,Settings.class);
	    		startActivity(intent2);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    


    //want to make a method that takes the "expectation" method and uses it to update distance and heading
    //to get bearing need to convert quaternion to euler and take the yaw (rotate abt z)
    
    private void updateLocation(){
    	CameraPosition curPos = map.getCameraPosition();
    	double[] expectation = null;
    	double distX = expectation[0]; //these values will change when linked to other service properly
    	double distY = expectation[1];
    	float bearing = 0; //currently any update will not reflect on camera
    	double newLat = currentLoc.latitude + (180.0/Math.PI)*(distY/6378137);//6378137 earths radius at equator
    	double newLng = currentLoc.longitude + (180.0/Math.PI)*(distX/6378137);
    	LatLng newLoc = new LatLng(newLat,newLng);
    	if (line != null)
    		line.remove(); // get rid of old line
    	route.add(newLoc);
		line = map.addPolyline(route);//adding the polyline
		
		CameraPosition newPos = CameraPosition.builder(curPos).target(newLoc).build();	//move camera
    	map.moveCamera(CameraUpdateFactory.newCameraPosition(newPos));
		
    	currentLoc = newLoc;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
    
    private class MyReceiver extends BroadcastReceiver{
	    @Override
	    public void onReceive(Context context, Intent intent){
	        if (intent.hasExtra(SensorService.EXPECTATION)){
	        	expectation = intent.getDoubleArrayExtra(SensorService.EXPECTATION);
	        	Log.e( "Map", "recieving EXPECTATION" );  
	        }

	    }

	}
    
    
	@Override 
	public void onResume(){
	    super.onResume();
	    Log.e( "Map", "onResume/registering receiver" );  
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();      
        intentFilter.addAction(SensorService.SENSOR_INTENT);
        //bindService(i, null, 0);
        if (!isMyServiceRunning(SensorService.class)){
        	Log.e( "Map", "starting service" );  
        	startService(i); 
        }
        registerReceiver(myReceiver, intentFilter);   
	}

	@Override 
	public void onPause(){
	    super.onPause();
	    Log.e( "Map", "onPause/unregistering receiver" ); 
	    //stopService(i);

	    if (myReceiver != null){
	    	unregisterReceiver(myReceiver);
	    	myReceiver = null;
	    }      
	}

	@Override
	protected void onStop(){
	    super.onStop();
	    Log.e( "Map", "onStop" );
	    //stopService(i);
	    if (myReceiver != null) {
	    	unregisterReceiver (myReceiver);
	    }
	    
	}

    
}