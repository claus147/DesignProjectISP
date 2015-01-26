package com.dp1415.ips;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;





import android.support.v7.app.ActionBarActivity;
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
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		rotateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, rotateSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
	            //openSettings();
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
	}
	
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			accelValues = event.values;
		    // Movement
		    accelX.setText(String.valueOf(accelValues[0]));
		    accelY.setText(String.valueOf(accelValues[1]));
		    accelZ.setText(String.valueOf(accelValues[2]));			
		}
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			rotateValues = event.values;
		    // Movement
			rotateX.setText(String.valueOf(rotateValues[0]));
			rotateY.setText(String.valueOf(rotateValues[1]));
			rotateZ.setText(String.valueOf(rotateValues[2]));	
			rotateS.setText(String.valueOf(rotateValues[3]));	
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public void initializeParticles (int numOfParticles, double[][] particle){
		//particle [0~100][] = particle , [][0] = weight, [][1] = X, [][2] = Y, [][3] = Z
		particle = new double[numOfParticles][3];
		for (int x = 0; x < numOfParticles; x++ ){
			//Gaussian distribution 
			particle[x][0]= (double)(1/numOfParticles);
			particle[x][1]= 0/*some gaussian distribution*/;
			particle[x][2]= 0/*some gaussian distribution*/;
			particle[x][3]= 0/*some gaussian distribution*/;
		}
	}
	
	public void particleFilter (double[] state, double[][] particle, int numOfParticles){
		//Propagate
		//Determine mode
		//Implement dynamic models to get the next state
		//Update the weights
		//Normalize the weight
		double totalWeight = 0;
		for (int x = 0; x < numOfParticles; x++)
			totalWeight+=particle[x][0];
		for (int x = 0; x < numOfParticles; x++)
			particle[x][0]=particle[x][0]/totalWeight;
		//Resample
		//Use Random Number Generator to propagate new particle by weight
		//Calculate expectation to get location
		//update it on map
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
}
