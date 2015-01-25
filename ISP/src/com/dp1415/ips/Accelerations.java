package com.dp1415.ips;

import Jama.Matrix;
import java.util.Arrays;

public class Accelerations {
	private double accelX;
	private double accelY;
	private double accelZ;
	private double rotateX;
	private double rotateY;
	private double rotateZ;
	private double rotateS;
	private double accelValues[];
	private double rotateValues[];
	private double[] accelerations = new double[3];
	
	Matrix quaternion;

	
	// Constructor
	public Accelerations(double[] accelValues, double[] rotateValues) {
		accelX = accelValues[0];
		accelY = accelValues[1];
		accelZ = accelValues[2];
		rotateX = rotateValues[0];
		rotateY = rotateValues[1];
		rotateZ = rotateValues[2];
		rotateS = rotateValues[3];
		this.accelValues = accelValues;
		this.rotateValues = rotateValues;		
	}
	// change phone coodinate to world coordinate
	public double[] phoneToWorldCoordinates() {
		Matrix accel = new Matrix(accelValues,3);
		Matrix worldAccel = getConversionMatrix().times(accel);
		accelerations = worldAccel.getRowPackedCopy();
		return accelerations;
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
	
	public final double getAccelX(){
		return accelerations[0];
	}
	public final double getAccelY(){
		return accelerations[1];
	}
	public final double getAccelZ(){
		return accelerations[2];
	}

}
