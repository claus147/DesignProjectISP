package com.dp1415.ips;


//this class creates a state vectors from sensor data and converts to global coordinates 
public class stateVector {
	// Constructor
	public stateVector(float[] accelValues, float[] rotateValues) {
		float accelX = accelValues[0];
		float accelY = accelValues[1];
		float accelZ = accelValues[2];
		float rotateX = rotateValues[0];
		float rotateY = rotateValues[1];
		float rotateZ = rotateValues[2];
		float rotateS = rotateValues[3];
	}
	// change phone coodinate to world coordinate
	public float[] phoneToWorldCoordinates() {
		return null;
		
	}
}
