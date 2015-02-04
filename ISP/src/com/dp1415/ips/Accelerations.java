package com.dp1415.ips;

import Jama.Matrix;
import java.util.Arrays;

public class Accelerations {
	private double accelValues[];
	private double rotateValues[];
	private double[] accelerations = new double[3];
	
	// Constructor
	public Accelerations(double[] accelValues, double[] rotateValues) {
		this.accelValues = accelValues;
		this.rotateValues = rotateValues;
		accelerations = phoneToWorldCoordinates();
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
	
	public final double getX(){
		return accelerations[0];
	}
	
	public final double getY(){
		return accelerations[1];
	}
	
	public final double getZ(){
		return accelerations[2];
	}
	
	public void update(double[] nextAccelValues, double[] nextRotateValues){
		accelValues = nextAccelValues;
		rotateValues = nextRotateValues;
		accelerations = phoneToWorldCoordinates();
	}

}
