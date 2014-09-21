package com.dp1415.ips;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gpsLat = (TextView) findViewById(R.id.gpsLat);
		gpsLon = (TextView) findViewById(R.id.gpsLon);
		accelX = (TextView) findViewById(R.id.accelX);
		accelY = (TextView) findViewById(R.id.accelY);
		accelZ = (TextView) findViewById(R.id.accelZ);
		gyroX = (TextView) findViewById(R.id.gyroX);
		gyroY = (TextView) findViewById(R.id.gyroY);
		gyroZ = (TextView) findViewById(R.id.gyroZ);
		magnetX = (TextView) findViewById(R.id.magnetX);
		magnetY = (TextView) findViewById(R.id.magnetY);
		magnetZ = (TextView) findViewById(R.id.magnetZ);

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
}
