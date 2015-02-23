package com.dp1415.ips;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


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
	
	public final static String SENSOR_INTENT = "com.dp1415.ips.SensorService.SENSOR_READINGS";
	public final static String ACCEL_VALUES = "ACCEL_VALUES";
	public final static String ROTATE_VALUES = "ROTATE_VALUES";
	
	Intent intent = new Intent(SENSOR_INTENT);
	
	
	
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
		while(true);
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
			intent.putExtra(ACCEL_VALUES, accelValues);
		}
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			rotateValues = event.values;
			intent.putExtra(ROTATE_VALUES, rotateValues);
		}
		sendBroadcast(intent);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}



}