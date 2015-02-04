package com.dp1415.ips;


public class Velocities {
	private double timeStamp;
	private double[]velocities = new double[3];
	private double initialAccelX; 
	private double initialAccelY; 
	private double initialAccelZ; 
	private double initialVelX;
	private double initialVelY;
	private double initialVelZ;
	private Accelerations accelStates;
	
	//constructor 
	public Velocities(double[] accelValues, double[] rotateValues,double timeStamp,double initialAccelX,
			double initialAccelY,double initialAccelZ,double initialVelX,double initialVelY, double initialVelZ){
		accelStates = new Accelerations(accelValues,rotateValues);
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
	
	public final double getX(){
		velocities[0] = integration(initialAccelX,accelStates.getX())+initialVelX;
		return velocities[0];
	}
	
	public final double getY(){
		velocities[1] = integration(initialAccelY,accelStates.getY())+initialVelY;
		return velocities[1];
	}
	
	public final double getZ(){
		velocities[2] = integration(initialAccelZ,accelStates.getZ())+initialVelZ;
		return velocities[2];
	}
	
	public final Accelerations getAcceleration(){
		return accelStates;
	}
	
	public void update(double[] nextAccelValues, double[] nextRotateValues){
		//update current state accelerations to be next state initial acceleration
		initialAccelX = accelStates.getX();
		initialAccelY = accelStates.getY();
		initialAccelZ = accelStates.getZ();
		
		//update the acceleration with current data from sensors
		accelStates.update(nextAccelValues, nextRotateValues);
		
		//update current state velocity to be next state initial velocity
		initialVelX = velocities[0];
		initialVelY = velocities[1];
		initialVelZ = velocities[2];
	}
}
