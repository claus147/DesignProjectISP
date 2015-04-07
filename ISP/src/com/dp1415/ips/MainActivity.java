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
	
	public final static String MAIN_INTENT = "com.dp1415.ips.MainActivity.WRITE_INFO";
	Intent intent = new Intent(MAIN_INTENT);
	
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
//		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//		accelValues = new float[]{0,0,0}; 
//		rotateValues = new float[]{0,0,0,0}; 
		accelCounter = 0;
		rotateCounter = 0;
		
		
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
		intent.putExtra(SensorService.WRITE, true);
		sendBroadcast(intent);
	}

	public void onStopClick(View view) {
		dataCollection = false;
		startCollection.setVisibility(View.VISIBLE);
		stopCollection.setVisibility(View.GONE);
		
		intent.putExtra(SensorService.WRITE, false);
		sendBroadcast(intent);
	}
	
	
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
