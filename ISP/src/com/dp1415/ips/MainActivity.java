package com.dp1415.ips;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	private FileWriter writer;
	private float[] accelValues;
	private float[] gyroValues;
	private float[] magnetValues;
	private double latitude;
	private double longitude;
	private long initialTime;
	
	
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
	    accelValues = event.values;
	    long timer = System.nanoTime() - initialTime;
	    double timerInMs = (double)timer / 1000000.0;
	    // Movement
	    accelX.setText(String.valueOf(values[0]));
	    accelY.setText(String.valueOf(values[1]));
	    accelZ.setText(String.valueOf(values[2]));
	    if(writer!=null){
			try {
				Log.i("Rita_Check", "write accel");
				writer.write(timerInMs + "ms ," + values[0] + "," + values[1] + "," + values[2] + 
						"," + gyroValues[0] + "," + gyroValues[1] + "," + gyroValues[2] + 
						"," + magnetValues[0] + "," + magnetValues[1] + "," + magnetValues[2] + 
						"," + latitude + "," + longitude + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void getGyrometer(SensorEvent event) {
	    float[] values = event.values;
	    gyroValues = event.values;
	    long timer = System.nanoTime() - initialTime;
	    double timerInMs = (double)timer / 1000000.0;
	    // Movement
	    gyroX.setText(String.valueOf(values[0]));
	    gyroY.setText(String.valueOf(values[1]));
	    gyroZ.setText(String.valueOf(values[2]));
		if(writer!=null){
			try {
				Log.i("Rita_Check", "write gyro");
				writer.write(timerInMs + "ms ," + accelValues[0] + "," + accelValues[1] + "," + accelValues[2] + 
						"," + values[0] + "," + values[1] + "," + values[2] + 
						"," + magnetValues[0] + "," + magnetValues[1] + "," + magnetValues[2]  + 
						"," + latitude + "," + longitude + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
	}
	
	private void getMagnetometer(SensorEvent event) {
	    float[] values = event.values;
	    magnetValues = event.values;
	    long timer = System.nanoTime() - initialTime;
	    double timerInMs = (double)timer / 1000000.0;
	    // Movement
	    magnetX.setText(String.valueOf(values[0]));
	    magnetY.setText(String.valueOf(values[1]));
	    magnetZ.setText(String.valueOf(values[2]));
	    if(writer !=null){
			try {
				Log.i("Rita_Check", "write magnet");
				writer.write(timerInMs + "ms ," + accelValues[0] + "," + accelValues[1] + "," + accelValues[2] + 
						"," + gyroValues[0] + "," + gyroValues[1] + "," + gyroValues[2] + 
						"," + values[0] + "," + values[1] + "," + values[2] + 
						"," + latitude + "," + longitude + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	    sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_NORMAL);
	    sensorManager.registerListener(this,magnetSensor,SensorManager.SENSOR_DELAY_NORMAL);
	    initialTime = System.nanoTime();
			try {
				Log.i("Rita_Check", "new file");
				File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sensorData.csv");
				writer = new FileWriter(outFile,false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (writer!=null){
			try {
				writer.write("Time" +"," + "accelX" +"," + "accelY" + "," + "accelZ" + "," + "gyroX" + "," + "gyroY" + "," + "gyroZ" + "," + "magnetX" + "," + "magnetY" + "," + "magnetZ" + "," + "Latitude" + "," + "Longitude" + "\n" );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		
	}

	public void onStopClick(View view) {
	   try {
		writer.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
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
