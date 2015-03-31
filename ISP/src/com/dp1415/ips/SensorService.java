package com.dp1415.ips;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;




import android.app.IntentService;
import android.app.Service;
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
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


/*
 * web site below shows how to properly disconnect broadcasts and sensors onResume() etc
 * http://stackoverflow.com/questions/9953226/android-broadcasting-sensor-data-from-a-service-to-an-activity
 */
public class SensorService extends IntentService implements SensorEventListener{

	private float[] accelValues;
	private float[] rotateValues;
	
	private SensorManager sensorManager;
	private Sensor accelSensor;
	private Sensor rotateSensor;
	
	private ParticleFilter particleFilter;
	private stateVector stateVector;
	Handler handle = new Handler();
	private int accelCounter,rotateCounter;
	
	private boolean writing = false;
	private long initialTime;
	private FileWriter writer;
	MainActivityReceiver mainAcReceiver=null;
	MapActivityReceiver mapReceiver = null;
	
	private boolean doReset = false;
	
	public final static String SENSOR_INTENT = "com.dp1415.ips.SensorService.SENSOR_READINGS";
	public final static String ACCEL_VALUES = "ACCEL_VALUES";
	public final static String ROTATE_VALUES = "ROTATE_VALUES";
	public final static String EXPECTATION = "EXPECTATION";
	public final static String WRITE = "WRITE";
	public final static String RESET = "RESET";
	
	Intent intent = new Intent(SENSOR_INTENT);
	
	Intent i;
	Intent j;
	
	
	
	/**
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public SensorService() {
		super("SensorService");
	}
	
	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns, IntentService
	 * stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		 Log.e( "SS", "onHandleIntent" );
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		rotateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, rotateSensor, SensorManager.SENSOR_DELAY_FASTEST);		
		
		resetParticleFilter();
		Log.e( "SS", "after init weight: " + particleFilter.getWeights() );
		handle.post(collectionLoop);
		
		mainAcReceiver = new MainActivityReceiver();
		IntentFilter intentFilter = new IntentFilter();      
        intentFilter.addAction(MainActivity.MAIN_INTENT);
        i= new Intent(this, com.dp1415.ips.MainActivity.class);
        registerReceiver(mainAcReceiver, intentFilter);
        
        mapReceiver = new MapActivityReceiver();
		IntentFilter mapIntentFilter = new IntentFilter();      
        mapIntentFilter.addAction(MapViewActivity.MAP_INTENT);
        j= new Intent(this, com.dp1415.ips.MapViewActivity.class);
        registerReceiver(mapReceiver, mapIntentFilter);
		
		while(true);
	}
	
	private void resetParticleFilter(){
		accelCounter = 0;
		rotateCounter = 0;
		initialTime = System.nanoTime();
		stateVector = new stateVector(accelAverage(), rotateAverage(), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, System.nanoTime());
		particleFilter = new ParticleFilter();
		particleFilter.initialize(100, stateVector);
		particleFilter.propagate();
	}
	
	@Override
	public void onDestroy() {
		Log.e( "SS", "onDestroy" );
	    sensorManager.unregisterListener(this);       
	    super.onDestroy();
	}

	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			accelValues = event.values;
			for (int x = 0 ; x < 3; x++){
				accelValues[x] += event.values[x];
			}
			intent.putExtra(ACCEL_VALUES, accelValues);
			accelCounter++;
		}
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			rotateValues = event.values;
			for (int x = 0 ; x < 4; x++){
				rotateValues[x] += event.values[x];
			}
			intent.putExtra(ROTATE_VALUES, rotateValues);
			rotateCounter++;
		}
		sendBroadcast(intent);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
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
		rotateCounter = 0;
		return rotate;
	}
	
	
	Runnable collectionLoop = new Runnable() {
	    @Override
	    public void run(){
	    	Log.e( "SS", "before update weight: " + particleFilter.getWeights() );
	    	if (doReset){
	    		resetParticleFilter();
	    		doReset = false;
	    	}
	    	
	    	stateVector.update(accelAverage(), rotateAverage(), System.nanoTime());
	    	particleFilter.updateWeights(stateVector);
	    	Log.e( "SS", "after update weight: " + particleFilter.getWeights() );
	    	particleFilter.normalizeWeight();
	    	Log.e( "SS", "after normalise " + particleFilter.getWeights() );
	    	particleFilter.resample();
	    	double[] expectation = particleFilter.expectation();
	    	
	    	intent.putExtra(EXPECTATION, expectation);
	    	
	    	particleFilter.propagate();
		    
	    	//the saving of info into CSV file
	    	if(writing && writer !=null){
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
		    handle.postDelayed(collectionLoop,200);
		    	
	    }
	};
	
	
	private class MainActivityReceiver extends BroadcastReceiver{
	    @Override
	    public void onReceive(Context context, Intent intent){
	        if (intent.hasExtra(WRITE)){
	        	Log.e( "SS", "recieving WRITE" );  
	        	//Toast.makeText(getApplicationContext(), "we are here", Toast.LENGTH_SHORT).show();
	        	if (!writing){
		        	try {
						writing = true;
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
	        	} else {
	        		try {
						writer.close();
						writing = false;
						Toast.makeText(getApplicationContext(), "Data write successful", Toast.LENGTH_SHORT).show(); //popup notification
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        }
	    } 
	    
	}
	private class MapActivityReceiver extends BroadcastReceiver{
	    @Override
	    public void onReceive(Context context, Intent intent){
	    	if (intent.hasExtra(RESET)){
	    		doReset = true;
	    	}
	    }
    }


}