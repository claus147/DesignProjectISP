package com.dp1415.ips;

//this class creates a state vector matrix from sensor data and converts to global coordinates 
public class stateVector {
	private double accelValues[];
	private double rotateValues[];
	private double initialDistanceX;
	private double initialDistanceY;
	private double initialDistanceZ;
	private double initialAccelX; 
	private double initialAccelY; 
	private double initialAccelZ; 
	private double initialVelX;
	private double initialVelY;
	private double initialVelZ;
	private long initialTime;
	private Accelerations accelStates= new Accelerations(accelValues,rotateValues);
	private double testInterval = 0.001; //set timeStamp to 1ms for testing purpose
	private long timeStamp;
	private long updatedTime;
	private Velocities velocityStates = new Velocities(accelStates,testInterval, initialAccelX, 
			initialAccelY,initialAccelZ,initialVelX,initialVelY,initialVelZ);
	private Distances distanceStates = new Distances(velocityStates, testInterval, initialDistanceX,
			initialDistanceY,initialDistanceZ,initialVelX,initialVelY,initialVelZ);

	// Constructor
	public stateVector(double[] accelValues, double[] rotateValues,double initialDistanceX, 
			double initialDistanceY, double initialDistanceZ,double initialAccelX, double initialAccelY, 
			double initialVelX,double initialVelY,double initialVelZ, double initialAccelZ,long initialTime){
		
		this.accelValues = accelValues;
		this.rotateValues = rotateValues;
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
	}
	
	//return accelerations
	public final Accelerations getAcceleration(){
		return accelStates;	
	}
	
	//return velocities 
	public final Velocities getVelocity(){
		return velocityStates;
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

	//return time since moving
	public final long getTime(){
		timeStamp = (long) (testInterval);
		updatedTime = initialTime + timeStamp; 
		return updatedTime;
	}
	
	// return current mode (will add)

}
