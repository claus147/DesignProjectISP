package com.dp1415.ips;
//this class creates a state vector matrix from sensor data and converts to global coordinates 
public class stateVector {
	private double accelValues[] = new double[3];
	private double rotateValues[] = new double[4];
	private double initialDistanceX;
	private double initialDistanceY;
	private double initialDistanceZ;
	private double initialAccelX; 
	private double initialAccelY; 
	private double initialAccelZ; 
	private double initialVelX;
	private double initialVelY;
	private double initialVelZ;
	private long updatedTime;
	private double initialTime;
	private double initialInterval = 0;
	private double timeStamp;
	private Velocities velocityStates;
	private Distances distanceStates;
	// Constructor
	public stateVector(float[] accelValues, float[] rotateValues,double initialDistanceX, 
			double initialDistanceY, double initialDistanceZ,
			double initialAccelX, double initialAccelY, double initialAccelZ, 
			double initialVelX,double initialVelY,double initialVelZ,long initialTime){
		
		this.accelValues[0] = (double)accelValues[0];
		this.accelValues[1] = (double)accelValues[1];
		this.accelValues[2] = (double)accelValues[2];
		this.rotateValues[0] = (double)rotateValues[0];
		this.rotateValues[1] = (double)rotateValues[1];
		this.rotateValues[2] = (double)rotateValues[2];
		this.rotateValues[3] = (double)rotateValues[3];
		this.initialDistanceX = initialDistanceX;
		this.initialDistanceY = initialDistanceY;
		this.initialDistanceZ = initialDistanceZ;
		this.initialAccelX = initialAccelX;
		this.initialAccelY = initialAccelY;
		this.initialAccelZ = initialAccelZ;
		this.initialVelX = initialVelX;
		this.initialVelY = initialVelY;
		this.initialVelZ = initialVelZ;
		this.initialTime = initialTime;	
		distanceStates = new Distances(this.accelValues, this.rotateValues, initialInterval,
				initialAccelX, initialAccelY, initialAccelZ,
				initialDistanceX, initialDistanceY, initialDistanceZ,
				initialVelX, initialVelY, initialVelZ);
	}
	
	//return accelerations
	public final Accelerations getAcceleration(){
		return distanceStates.getVelocity().getAcceleration();	
	}
	
	//return velocities 
	public final Velocities getVelocity(){
		return distanceStates.getVelocity();
	}
	
	//return distances 
	public final Distances getDistance(){
		return distanceStates;
	}
	
	
	// 3 methods below will return the Quaternion values 
	public final double getRotationX(){
		return rotateValues[0];
	}
	
	public final double getRotationY(){
		return rotateValues[1];
	}
	
	public final double getRotationZ(){
		return rotateValues[2];
	}
	
	public final double getRotationS(){
		return rotateValues[3];
	}
	public final long getTime(){
		return (long) initialTime;
	}
	
	//update all data in state vector
	public void update(float[] nextAccelValues, float[] nextRotateValues, long currentTime){
		timeStamp = (currentTime - initialTime)/1000000000;
		initialTime = currentTime;
		this.accelValues[0] = (double)nextAccelValues[0];
		this.accelValues[1] = (double)nextAccelValues[1];
		this.accelValues[2] = (double)nextAccelValues[2];
		this.rotateValues[0] = (double)nextRotateValues[0];
		this.rotateValues[1] = (double)nextRotateValues[1];
		this.rotateValues[2] = (double)nextRotateValues[2];
		this.rotateValues[3] = (double)nextRotateValues[3];
		
		distanceStates.update(this.accelValues, this.rotateValues, timeStamp);
	}
	
}
