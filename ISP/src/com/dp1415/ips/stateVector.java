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
	Matrix q;
	Matrix qc;
	
	Matrix quaternion;
	Map<String, Double> stateVector = new HashMap<String, Double>(); 
	double timeStamp; 
	double[] velocities = new double[3];
	double[] accelerations = new double[3]; 

	
	// Constructor
	public stateVector(float[] accelValues, float[] rotateValues) {
		accelX = accelValues[0];
		accelY = accelValues[1];
		accelZ = accelValues[2];
		rotateX = rotateValues[0];
		rotateY = rotateValues[1];
		rotateZ = rotateValues[2];
		rotateS = rotateValues[3];

		//Initialize the state vector assuming initially stop 
		stateVector.put("AccelX", 0d);
		stateVector.put("AccelY", 0d);
		stateVector.put("AccelZ", 0d);
		stateVector.put("VelocityX", 0d);
		stateVector.put("VelocityY", 0d);
		stateVector.put("VelocityZ", 0d);
		stateVector.put("DistanceX", 0d);
		stateVector.put("DistanceY", 0d);
		stateVector.put("DistanceZ", 0d);
		stateVector.put("QuaX", 0d);
		stateVector.put("QuaY", 0d);
		stateVector.put("QuaZ", 0d);
		stateVector.put("QuaS", 1d);
		stateVector.put("Time", 0d);
		stateVector.put("Mode", 0d);
	}
	// change phone coodinate to world coordinate
	public float[] phoneToWorldCoordinates() {
		
		return null;
	}
	//this method will create matrixes.
	private void createMatrix(){
		q = new Matrix(new double[][]{{rotateX},{rotateY},{rotateZ},{rotateS}});
		qc = new Matrix(new double[][]{{-rotateX},{-rotateY},{-rotateZ},{rotateS}});
	}
	//this method will operate a quaternion multiplication
	private double[] quaternionMultiplication(double[] fa, double[] sa){
		return new double[]{
			fa[3] * sa[0] + fa[0] * sa[3] + fa[1] * sa[2] - fa[2] * sa[1],
			fa[3] * sa[1] + fa[1] * sa[3] + fa[2] * sa[0] - fa[0] * sa[2],
			fa[3] * sa[2] + fa[2] * sa[3] + fa[0] * sa[1] - fa[1] * sa[0],
			fa[3] * sa[3] - fa[0] * sa[0] - fa[1] * sa[1] - fa[2] * sa[2]
		};
	}
	
	public final double[] getVelocities(){
		
		return velocities;
		
	}
	
	public final double[] getAccelerations(){
		
		return accelerations;
		
	}
	
	public Map<String, Double> updateStateVector(){

		return stateVector; 
	}
}
