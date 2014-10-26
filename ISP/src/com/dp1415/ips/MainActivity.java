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
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
	private TextView orientX;
	private TextView orientY;
	private TextView orientZ;
	private Button startCollection;
	private Button stopCollection;
	private SensorManager sensorManager;
	private Sensor accelSensor;
	private Sensor gyroSensor;
	private Sensor magnetSensor;
	private FileWriter writer;
	private float[] accelValues;
	private float[] gyroValues;
	private float[] magnetValues;
	private double[] orientValues;
	private double latitude;
	private double longitude;
	private long initialTime;
	private boolean dataCollection = false;
	Handler handle = new Handler();
	
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
		orientX = (TextView) findViewById(R.id.orientXData);		//orientation labels to work with
		orientY = (TextView) findViewById(R.id.orientYData);		//orientation labels to work with
		orientZ = (TextView) findViewById(R.id.orientZData);		//orientation labels to work with
		startCollection = (Button) findViewById(R.id.startCollect);
		stopCollection = (Button) findViewById(R.id.stopCollect);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
				File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sensorData.csv");
				writer = new FileWriter(outFile,false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (writer!=null){
			try {
				writer.write("Time (ms)" +"," + "accelX" +"," + "accelY" + "," + "accelZ" + "," + "gyroX" + "," + "gyroY" + "," + "gyroZ" + "," + "magnetX" + "," + "magnetY" + "," + "magnetZ" + "," + "Latitude" + "," + "Longitude" + "," + "orientX" + "," + "orientY" + "," + "orientZ" + "\n" );
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
	}

	
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accelValues = event.values;
		    // Movement
		    accelX.setText(String.valueOf(accelValues[0]));
		    accelY.setText(String.valueOf(accelValues[1]));
		    accelZ.setText(String.valueOf(accelValues[2]));			
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			gyroValues = event.values;
		    // Movement
		    gyroX.setText(String.valueOf(gyroValues[0]));
		    gyroY.setText(String.valueOf(gyroValues[1]));
		    gyroZ.setText(String.valueOf(gyroValues[2]));		
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magnetValues = event.values;
		    // Movement
		    magnetX.setText(String.valueOf(magnetValues[0]));
		    magnetY.setText(String.valueOf(magnetValues[1]));
		    magnetZ.setText(String.valueOf(magnetValues[2]));	
		}
		if (accelValues != null && magnetValues != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, accelValues, magnetValues);
			if (success) {
				float orientation[] = new float[3];
				orientValues = new double[3];
				//get orientation
				SensorManager.getOrientation(R, orientation);
				//convert radians to degrees
				orientValues[0] = Math.toDegrees(orientation[0]);
				orientValues[1] = Math.toDegrees(orientation[1]);
				orientValues[2] = Math.toDegrees(orientation[2]);
				
			    orientX.setText(String.valueOf(orientValues[0]));
			    orientY.setText(String.valueOf(orientValues[1]));
			    orientZ.setText(String.valueOf(orientValues[2]));	
			}
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	Runnable collectionLoop = new Runnable() {
	    @Override
	    public void run(){
		    if(writer !=null){
			try {
				long timer = System.nanoTime() - initialTime;
			    double timerInMs = (double)timer / 1000000.0;
			    //write all the sensor data
				writer.write(timerInMs + "," + accelValues[0] + "," + accelValues[1] + "," + accelValues[2] + 
						"," + gyroValues[0] + "," + gyroValues[1] + "," + gyroValues[2] + 
						"," + magnetValues[0] + "," + magnetValues[1] + "," + magnetValues[2] + 
						"," + latitude + "," + longitude + "," + orientValues[0] + "," + orientValues[1] + "," + orientValues[2] + "\n");
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
}
