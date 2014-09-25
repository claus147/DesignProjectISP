package com.dp1415.ips;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements SensorEventListener{

	private TextView gpsLat;
	private TextView gpsLon;
	private TextView accelX;
	private TextView accelY;
	private TextView accelZ;
	private TextView gyroX;
	private TextView gyroY;
	private TextView gyroZ;
	private TextView magnetX;
	private TextView magnetY;
	private TextView magnetZ;
	private SensorManager sensorManager;
	private Sensor accelSensor;
	private Sensor gyroSensor;
	private Sensor magnetSensor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gpsLat = (TextView) findViewById(R.id.gpsLatData);
		gpsLon = (TextView) findViewById(R.id.gpsLonData);
		accelX = (TextView) findViewById(R.id.accelXData);
		accelY = (TextView) findViewById(R.id.accelYData);
		accelZ = (TextView) findViewById(R.id.accelZData);
		gyroX = (TextView) findViewById(R.id.gyroXData);
		gyroY = (TextView) findViewById(R.id.gyroYData);
		gyroZ = (TextView) findViewById(R.id.gyroZData);
		magnetX = (TextView) findViewById(R.id.magnetXData);
		magnetY = (TextView) findViewById(R.id.magnetYData);
		magnetZ = (TextView) findViewById(R.id.magnetZData);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		GPS locationListener = new GPS(gpsLat, gpsLon);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void getAccelerometer(SensorEvent event) {
	    float[] values = event.values;
	    // Movement
	    accelX.setText(String.valueOf(values[0]));
	    accelY.setText(String.valueOf(values[1]));
	    accelZ.setText(String.valueOf(values[2]));
	}
	
	private void getGyrometer(SensorEvent event) {
	    float[] values = event.values;
	    // Movement
	    gyroX.setText(String.valueOf(values[0]));
	    gyroY.setText(String.valueOf(values[1]));
	    gyroZ.setText(String.valueOf(values[2]));
	}
	
	private void getMagnetometer(SensorEvent event) {
	    float[] values = event.values;
	    // Movement
	    magnetX.setText(String.valueOf(values[0]));
	    magnetY.setText(String.valueOf(values[1]));
	    magnetZ.setText(String.valueOf(values[2]));
	}
	
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			getGyrometer(event);
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			getMagnetometer(event);
		}
	}
	

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
