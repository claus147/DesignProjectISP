package com.dp1415.ips;

import java.util.HashMap;
import java.util.Map;
import Jama.Matrix;


//this class creates a state vector matrix from sensor data and converts to global coordinates 
public class stateVector {
	double accelX;
	double accelY;
	double accelZ;
	double rotateX;
	double rotateY;
	double rotateZ;
	double rotateS;
	Matrix quaternion;
	
	
	// Constructor
	public stateVector(float[] accelValues, float[] rotateValues) {
		accelX = accelValues[0];
		accelY = accelValues[1];
		accelZ = accelValues[2];
		rotateX = rotateValues[0];
		rotateY = rotateValues[1];
		rotateZ = rotateValues[2];
		rotateS = rotateValues[3];

		Map<String, Float> stateVector = new HashMap<String, Float>(); 
		//store the state vactors into a hashmap 
		
		//Initialize the state vector assuming initially stop 
		stateVector.put("AccelX", new Float(0));
		stateVector.put("AccelY", new Float(0));
		stateVector.put("AccelZ", new Float(0));
		stateVector.put("VelocityX", new Float(0));
		stateVector.put("VelocityY", new Float(0));
		stateVector.put("VelocityZ", new Float(0));
		stateVector.put("DistanceX", new Float(0));
		stateVector.put("DistanceY", new Float(0));
		stateVector.put("DistanceZ", new Float(0));
		stateVector.put("QuaX", new Float(0));
		stateVector.put("QuaY", new Float(0));
		stateVector.put("QuaZ", new Float(0));
		stateVector.put("QuaS", new Float(1));
		stateVector.put("Time", new Float(0));
		stateVector.put("Mode", new Float(0));
	}
	// change phone coodinate to world coordinate
	public float[] phoneToWorldCoordinates() {
		
		return null;
	}
	//this method will create matrixes.
	private void createMatrix(){
		Matrix quaternion = new Matrix(new double[][]{{rotateX},{rotateY},{rotateZ},{rotateS}});
		
	}
	
	
	
	//public Map<String, Float> updateStateVector(){

		//return 
	//}
}
