package com.dp1415.ips;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;










import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity{

	private TextView gpsLat;
	private TextView gpsLon;
	private TextView accelX;
	private TextView accelY;
	private TextView accelZ;
	private TextView rotateX;
	private TextView rotateY;
	private TextView rotateZ;
	private TextView rotateS;
	private Button startCollection;
	private Button stopCollection;
	private SensorManager sensorManager;
	private Sensor accelSensor;
	private Sensor rotateSensor;
	private FileWriter writer;
	private float[] accelValues;
	private float[] rotateValues;
	private double latitude;
	private double longitude;
	private long initialTime;
	private boolean dataCollection = false;
	Handler handle = new Handler();
	private Accelerations initialAccel;
	private Velocities initialVel;
	private Distances initialDis;
	private ParticleFilter particleFilter;
	private stateVector stateVector;
	
	Intent i;
	MyReceiver myReceiver=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gpsLat = (TextView) findViewById(R.id.gpsLatData);
		gpsLon = (TextView) findViewById(R.id.gpsLonData);
		accelX = (TextView) findViewById(R.id.accelXData);
		accelY = (TextView) findViewById(R.id.accelYData);
		accelZ = (TextView) findViewById(R.id.accelZData);
		rotateX = (TextView) findViewById(R.id.rotateXData);
		rotateY = (TextView) findViewById(R.id.rotateYData);
		rotateZ = (TextView) findViewById(R.id.rotateZData);
		rotateS= (TextView) findViewById(R.id.rotateSData);
		startCollection = (Button) findViewById(R.id.startCollect);
		stopCollection = (Button) findViewById(R.id.stopCollect);

		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		
	    LocationListener locationListener = new LocationListener() {
	        public void onLocationChanged(Location location) {
	        	latitude = location.getLatitude();
	        	longitude = location.getLongitude();
	        	gpsLat.setText(String.valueOf(location.getLatitude()));  
	    		gpsLon.setText(String.valueOf(location.getLongitude())); 
	        }

	        public void onStatusChanged(String provider, int status, Bundle extras) {}

	        public void onProviderEnabled(String provider) {}

	        public void onProviderDisabled(String provider) {}
	      };
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);
		
	    //to receive broadcast
	    i= new Intent(this, com.dp1415.ips.SensorService.class);
//		myReceiver = new MyReceiver();
//        IntentFilter intentFilter = new IntentFilter();      
//        intentFilter.addAction(SensorService.SENSOR_INTENT);
//        startService(i);  
//        registerReceiver(myReceiver, intentFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_map:
	        	Intent intent = new Intent(this,MapViewActivity.class);
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
	
	
	public boolean enoughExternalStorage(){
		//check if external storage is enough for read and write
		String state = Environment.getExternalStorageState();
		 if (Environment.MEDIA_MOUNTED.equals(state)) {
		        return true;
		    }
		    return false;
	}

	public void onStartClick(View view) {
		dataCollection = true;
		startCollection.setVisibility(View.GONE);
		stopCollection.setVisibility(View.VISIBLE);
			try {
				File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "stateVector.csv");
				writer = new FileWriter(outFile,false);
				Toast.makeText(getApplicationContext(), "Data being written to " + outFile.toString(), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (writer!=null){
			try {
				//writer.write("Time (ms)" +"," + "accelX" +"," + "accelY" + "," + "accelZ" + "," + "rotateX" + "," + "rotateY" + "," + "rotateZ" + "," + "rotateS" + "," + "Latitude" + "," + "Longitude" + "\n" );
				writer.write("Time (ms)"+ "," + "accelX" +"," + "accelY" + "," + "accelZ"+ "velocityX" +"," + "velocityY" + "," + "velocityZ"
				+"distanceX" +"," + "distanceY" + "," + "distanceZ"+"QuaX"+"QuaY" +"," + "QuaZ" + "," + "QuaS");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		// set initial time and call the recursive loop
		initialTime = System.nanoTime();
		handle.post(collectionLoop);
	}

	public void onStopClick(View view) {
		dataCollection = false;
		startCollection.setVisibility(View.VISIBLE);
		stopCollection.setVisibility(View.GONE);
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), "Data write successful", Toast.LENGTH_SHORT).show(); //popup notification
	}
	
//	public void onSensorChanged(SensorEvent event) {
//		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
//			accelValues = event.values;
//		    // Movement
//		    accelX.setText(String.valueOf(accelValues[0]));
//		    accelY.setText(String.valueOf(accelValues[1]));
//		    accelZ.setText(String.valueOf(accelValues[2]));			
//		}
//		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
//			rotateValues = event.values;
//		    // Movement
//			rotateX.setText(String.valueOf(rotateValues[0]));
//			rotateY.setText(String.valueOf(rotateValues[1]));
//			rotateZ.setText(String.valueOf(rotateValues[2]));	
//			rotateS.setText(String.valueOf(rotateValues[3]));	
//		}
//	}
//	
//	@Override
//	public void onAccuracyChanged(Sensor arg0, int arg1) {
//		// TODO Auto-generated method stub
//		
//	}
	
	
	Runnable collectionLoop = new Runnable() {
	    @Override
	    public void run(){
		    if(writer !=null){
			try {
				long timer = System.nanoTime() - initialTime;
			    double timerInMs = (double)timer / 1000000.0;
			    //write all the sensor data
				writer.write(timerInMs + "," + accelValues[0] + "," + accelValues[1] + "," + accelValues[2] + 
						"," + rotateValues[0] + "," + rotateValues[1] + "," + rotateValues[2] + 
						"," + rotateValues[3] + "," + latitude + "," + longitude + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		    if (dataCollection){
		    	//calls itself every 100ms delay until stop button
		    	handle.postDelayed(collectionLoop,50);
		    }
	    }
	};
	
	
	public void startParticleFilter(){
		//create state vector
		stateVector = new stateVector(accelValues, rotateValues, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, (long) 0);
		particleFilter.initialize(100, stateVector);
		particleFilter.propagate(stateVector);
		//start the particle filter loop
		handle.post(collector);
	}
	
	
	//this is for particle filter
	Runnable collector = new Runnable() {
	    @Override
	    public void run(){

	    	stateVector.update(accelValues, rotateValues);
	    	particleFilter.updateWeights(stateVector);
	    	particleFilter.normalizeWeight();
	    	particleFilter.resample();
	    	particleFilter.expectation();
	    	particleFilter.propagate(stateVector);
		    if (true){
		    	//calls itself every 50ms delay until stop button
		    	handle.postDelayed(collector,50);
		    }
	    }
	};
	
	
	private class MyReceiver extends BroadcastReceiver{
	    @Override
	    public void onReceive(Context context, Intent intent){
	        if (intent.hasExtra(SensorService.ACCEL_VALUES)){
		    	accelValues = intent.getFloatArrayExtra(SensorService.ACCEL_VALUES);        
		        accelX.setText(String.valueOf(accelValues[0]));
			    accelY.setText(String.valueOf(accelValues[1]));
			    accelZ.setText(String.valueOf(accelValues[2]));
	        }
	        if (intent.hasExtra(SensorService.ROTATE_VALUES)){
		    	rotateValues = intent.getFloatArrayExtra(SensorService.ROTATE_VALUES);        
		    	rotateX.setText(String.valueOf(rotateValues[0]));
				rotateY.setText(String.valueOf(rotateValues[1]));
				rotateZ.setText(String.valueOf(rotateValues[2]));	
				rotateS.setText(String.valueOf(rotateValues[3]));
	        }
	        try {
				Thread.sleep(0,100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	} 
	
	@Override 
	public void onResume(){
	    super.onResume();
	    Log.e( "MA", "onResume/registering receiver" );  
	    //Register BroadcastReceiver to receive accelerometer data from service
	    //if (myReceiver == null){
	        myReceiver = new MyReceiver();
	        IntentFilter intentFilter = new IntentFilter();      
	        intentFilter.addAction(SensorService.SENSOR_INTENT);	        
	        startService(i);  
	        registerReceiver(myReceiver, intentFilter);
	    //}     
	}

	@Override 
	public void onPause(){
	    super.onPause();
	    Log.e( "MA", "onPause/unregistering receiver" ); 
	    stopService(i);

	    if (myReceiver != null){
	    	unregisterReceiver(myReceiver);
	    	myReceiver = null;
	    }      
	}

	@Override
	protected void onStop(){
	    super.onStop();
	    Log.e( "MA", "onStop" );
	    stopService(i);
	    if (myReceiver != null) {
	    	unregisterReceiver (myReceiver);
	    }
	    
	}
}
