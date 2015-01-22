package com.dp1415.ips;

import java.util.HashMap;
import java.util.Map;
import Jama.Matrix;
import java.util.Arrays;


//this class creates a state vector matrix from sensor data and converts to global coordinates 
public class stateVector {
	private double accelX;
	private double accelY;
	private double accelZ;
	private double rotateX;
	private double rotateY;
	private double rotateZ;
	private double rotateS;
	private double accelValues[];
	private double rotateValues[];
	
	Matrix quaternion;
	Map<String, Double> stateVector = new HashMap<String, Double>(); 
	double timeStamp; 
	double[] velocities = new double[3];
	double[] accelerations = new double[3]; 

	
	// Constructor
	public stateVector(double[] accelValues, double[] rotateValues) {
		accelX = accelValues[0];
		accelY = accelValues[1];
		accelZ = accelValues[2];
		rotateX = rotateValues[0];
		rotateY = rotateValues[1];
		rotateZ = rotateValues[2];
		rotateS = rotateValues[3];
		this.accelValues = accelValues;
		this.rotateValues = rotateValues;
		
		

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
	public double[] phoneToWorldCoordinates() {
		Matrix accel = new Matrix(accelValues,3);
		Matrix worldAccel = getConversionMatrix().times(accel);
		return worldAccel.getRowPackedCopy();
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
	
	//this method will return a 3x3 conversion matrix
	private Matrix getConversionMatrix(){
		double[] con_rotate = new double[]{-rotateValues[0],-rotateValues[1],-rotateValues[2],rotateValues[3],};
		double[] x_direction = quaternionMultiplication(
				quaternionMultiplication(rotateValues,new double[]{1,0,0,0}),con_rotate);
		double[] y_direction = quaternionMultiplication(
				quaternionMultiplication(rotateValues,new double[]{0,1,0,0}),con_rotate);
		double[] z_direction = quaternionMultiplication(
				quaternionMultiplication(rotateValues,new double[]{0,0,1,0}),con_rotate);
		x_direction = Arrays.copyOf(x_direction, x_direction.length-1);
		y_direction = Arrays.copyOf(y_direction, y_direction.length-1);
		y_direction = Arrays.copyOf(z_direction, z_direction.length-1);
		return new Matrix(new double[][]{x_direction,y_direction,z_direction});
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
