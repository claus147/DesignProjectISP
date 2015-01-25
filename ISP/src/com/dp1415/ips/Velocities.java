package com.dp1415.ips;


public class Velocities {
	private double accelX; 
	private double accelY;
	private double accelZ; 
	private double timeStamp;
	private double[]velocities = new double[3];
	private double initialAccelX; 
	private double initialAccelY; 
	private double initialAccelZ; 
	private double initialVelX;
	private double initialVelY;
	private double initialVelZ;
	
	//constructor 
	public Velocities(Accelerations accelStates,double timeStamp,double initialAccelX,
			double initialAccelY,double initialAccelZ,double initialVelX,double initialVelY, double initialVelZ){
		accelX = accelStates.getAccelX();
		accelY = accelStates.getAccelY();
		accelZ = accelStates.getAccelZ();
		this.timeStamp = timeStamp;
		this.initialAccelX = initialAccelX;
		this.initialAccelY = initialAccelY;
		this.initialAccelZ = initialAccelZ;
		this.initialVelX = initialVelX;
		this.initialVelY = initialVelY;
		this.initialVelZ = initialVelZ;
		
	}
	
	//integrate accelerations over timeStamp to get current velocity 
	//integrate using Trapezoial rule 
	private double integration(double initialAccel,double finalAccel){
		
		double velocity= timeStamp*(initialAccel+finalAccel)/2;
		return velocity;
	}
	
	public final double getVelocityX(){
		velocities[0] = integration(initialAccelX,accelX)+initialVelX;
		return velocities[0];
	}
	
	public final double getVelocityY(){
		velocities[1] = integration(initialAccelY,accelY)+initialVelY;
		return velocities[1];
	}
	
	public final double getVelocityZ(){
		velocities[2] = integration(initialAccelZ,accelZ)+initialVelZ;
		return velocities[2];
	}
}
