package com.dp1415.ips;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;










import android.support.v7.app.ActionBarActivity;








import android.support.v7.app.ActionBarActivity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
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
	private FileWriter writer;
	private float[] accelValues = new float[]{0,0,0};
	private float[] rotateValues = new float[]{0,0,0,0}; 
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
	private double[] expectation;
	private int accelCounter,rotateCounter;
	
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
//		accelValues = new float[]{0,0,0}; 
//		rotateValues = new float[]{0,0,0,0}; 
		accelCounter = 0;
		rotateCounter = 0;
		
		
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
				writer.write("Time (ms)"+ "," + "accelX" +"," + "accelY" + "," + "accelZ"+"," + "velocityX" +"," + "velocityY" + "," + "velocityZ"+ "," 
				+"distanceX" +"," + "distanceY" + "," + "distanceZ"+ "," +"QuaX"+ "," +"QuaY" +"," + "QuaZ" + "," + "QuaS" + "," + 
						"E[DistX]" + "," + "E[DistY]" + "," + "E[DistZ]" + "," + "E[QuaX]" + "," + "E[QuaY]" + "," + "E[QuaZ]" + "," + "E[QuaS]"+ "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		// set initial time and call the recursive loop
//		accelCounter = 0;
//		rotateCounter = 0;
		initialTime = System.nanoTime();
		stateVector = new stateVector(accelAverage(), rotateAverage(), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, System.nanoTime());
		particleFilter = new ParticleFilter();
		particleFilter.initialize(100, stateVector);
		particleFilter.propagate();
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
	
	public float[] accelAverage(){
		float[] accel = new float[3]; 
		for (int x = 0 ; x < 3; x++){
			if (accelCounter!=0){
				accel[x] = accelValues[x]/accelCounter;
				accelValues[x] = 0;
			}
			else{
				accel[x] = 0;
			}
		}
//		accelX.setText(String.valueOf(accel[0]));
//	    accelY.setText(String.valueOf(accel[1]));
//	    accelZ.setText(String.valueOf(accel[2]));	
		accelCounter = 0;
		return accel;
	}
	
	public float[] rotateAverage(){
		float[] rotate = new float[4]; 
		for (int x = 0 ; x < 4; x++){
			if (rotateCounter!=0){
				rotate[x] = rotateValues[x]/rotateCounter;
				rotateValues[x] = 0;
			}
			else{
				rotate[x] = 0;
			}
		}
//		rotateX.setText(String.valueOf(rotate[0]));
//		rotateY.setText(String.valueOf(rotate[1]));
//		rotateZ.setText(String.valueOf(rotate[2]));	
//		rotateS.setText(String.valueOf(rotate[3]));	
		rotateCounter = 0;
		return rotate;
	}
	
	
	Runnable collectionLoop = new Runnable() {
	    @Override
	    public void run(){
	    	stateVector.update(accelAverage(), rotateAverage(), System.nanoTime());
	    	particleFilter.updateWeights(stateVector);
	    	particleFilter.normalizeWeight();
//	    	particleFilter.resample();
	    	double[] expectation = particleFilter.expectation();
	    	particleFilter.propagate();
		    
		    if(writer !=null){
			try {
				long timer = System.nanoTime() - initialTime;
			    double timerInMs = (double)timer / 1000000.0;

			    //write all the sensor data
				writer.write(
						timerInMs + "," + 
						stateVector.getAcceleration().getX() + "," + 
						stateVector.getAcceleration().getY() + "," + 
						stateVector.getAcceleration().getZ() + "," + 
						stateVector.getVelocity().getX() + "," + 
						stateVector.getVelocity().getY() + "," + 
						stateVector.getVelocity().getZ() + "," + 
						stateVector.getDistance().getX() + "," + 
						stateVector.getDistance().getY() + "," + 
						stateVector.getDistance().getZ() + "," + 
						stateVector.getRotationX() + "," + 
						stateVector.getRotationY() + "," + 
						stateVector.getRotationZ() + "," + 
						stateVector.getRotationS() + "," + 
						expectation[0] + "," + 
						expectation[1] + "," + 
						expectation[2] + "," + 
						expectation[3] + "," + 
						expectation[4] + "," + 
						expectation[5] + "," + 
						expectation[6] + "," + 
						"\n");
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
		particleFilter = new ParticleFilter();
		stateVector = new stateVector(accelAverage(), rotateAverage(), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, System.nanoTime());
		particleFilter.initialize(100, stateVector);
		particleFilter.propagate();
		//start the particle filter loop
		handle.post(collector);
	}
	
	
	//this is for particle filter
	Runnable collector = new Runnable() {
	    @Override
	    public void run(){

	    	stateVector.update(accelAverage(), rotateAverage(), System.nanoTime());
	    	particleFilter.updateWeights(stateVector);
	    	particleFilter.normalizeWeight();
	    	particleFilter.resample();
	    	//particleFilter.expectation();
	    	particleFilter.propagate();
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
		    	accelCounter++;
	        }
	        if (intent.hasExtra(SensorService.ROTATE_VALUES)){
		    	rotateValues = intent.getFloatArrayExtra(SensorService.ROTATE_VALUES); 
		    	rotateCounter++;
	        }
	    }

	} 
	
	//added to make the data display more smoothly
	Runnable dataDisplay = new Runnable() {
	    @Override
	    public void run(){

	    	accelX.setText(String.valueOf(accelValues[0]));
			accelY.setText(String.valueOf(accelValues[1]));
			accelZ.setText(String.valueOf(accelValues[2]));
			rotateX.setText(String.valueOf(rotateValues[0]));
			rotateY.setText(String.valueOf(rotateValues[1]));
			rotateZ.setText(String.valueOf(rotateValues[2]));	
			rotateS.setText(String.valueOf(rotateValues[3]));
		    if (true){
		    	//calls itself every 50ms delay until stop button
		    	handle.postDelayed(dataDisplay,50);
		    }
	    }
	};
	
	@Override 
	public void onResume(){
	    super.onResume();
	    Log.e( "MA", "onResume/registering receiver" );  
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();      
        intentFilter.addAction(SensorService.SENSOR_INTENT);
        //bindService(i, null, 0);
        if (!isMyServiceRunning(SensorService.class)){
        	startService(i); 
        }
        registerReceiver(myReceiver, intentFilter); 
        handle.post(dataDisplay);  
	}

	@Override 
	public void onPause(){
	    super.onPause();
	    Log.e( "MA", "onPause/unregistering receiver" ); 
	    //stopService(i);

	    if (myReceiver != null){
	    	unregisterReceiver(myReceiver);
	    	myReceiver = null;
	    }  
	    handle.removeCallbacks(dataDisplay);
	}

	@Override
	protected void onStop(){
	    super.onStop();
	    Log.e( "MA", "onStop" );
	    //stopService(i);
	    if (myReceiver != null) {
	    	unregisterReceiver (myReceiver);
	    	myReceiver = null;
	    }
	    handle.removeCallbacks(dataDisplay);
	    
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

}
