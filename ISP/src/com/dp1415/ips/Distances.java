package com.dp1415.ips;

public class Distances {
	private double timeStamp;
	private double[]distances = new double[3];
	private double initialDistanceX; 
	private double initialDistanceY; 
	private double initialDistanceZ; 
	private double initialVelX;
	private double initialVelY;
	private double initialVelZ;
	private Velocities velocityStates;
	private float accelValues[];
	private float rotateValues[];
	private double accelInDouble[];
	private double rotateInDouble[];
	
	private void AccelToDouble(){
		accelInDouble[0] = Double.parseDouble(new Float(accelValues[0]).toString());
		accelInDouble[1] = Double.parseDouble(new Float(accelValues[1]).toString());
		accelInDouble[2] = Double.parseDouble(new Float(accelValues[2]).toString());
	}
	
	private void RotateToDouble(){
		rotateInDouble[0] = Double.parseDouble(new Float(rotateValues[0]).toString());
		rotateInDouble[1] = Double.parseDouble(new Float(rotateValues[1]).toString());
		rotateInDouble[2] = Double.parseDouble(new Float(rotateValues[2]).toString());
	}
	
	//constructor
	public Distances(double[] accelInDouble, double[] rotateInDouble, double timeStamp, double initialAccelX,
			double initialAccelY,double initialAccelZ, double initialDistanceX,
			double initialDistanceY, double initialDistanceZ,double initialVelX, 
			double initialVelY,double initialVelZ){
		velocityStates = new Velocities(accelInDouble,rotateInDouble, timeStamp, initialAccelX, 
				initialAccelY,initialAccelZ,initialVelX,initialVelY,initialVelZ);
		this. timeStamp = timeStamp; 
		this. initialDistanceX = initialDistanceX;
		this. initialDistanceY = initialDistanceY;
		this. initialDistanceZ = initialDistanceZ;
		this.initialVelX = initialVelX;
		this.initialVelY = initialVelY;
		this.initialVelZ = initialVelZ;
	}
	
	//integrate accelerations over timeStamp to get current velocity 
	//integrate using Trapezoial rule 	
	private double integration(double initialVel,double finalVel){
		double distance= timeStamp*(initialVel+finalVel)/2;
		return distance;
	}
	
	public final double getX(){
		distances[0] = integration(initialVelX,velocityStates.getX())+initialDistanceX;
		return distances[0];
	}
	
	public final double getY(){
		distances[1] = integration(initialVelY,velocityStates.getY())+initialDistanceY;
		return distances[1];
	}
	
	public final double getZ(){
		distances[2] = integration(initialVelZ,velocityStates.getY())+initialDistanceZ;
		return distances[2];
	}
	
	public final Velocities getVelocity(){
		return velocityStates;
	}
	
	public void update(double[] nextAccelValues, double[] nextRotateValues){
		//update the current state velocity to be next state initial velocity
		initialVelX = velocityStates.getX();
		initialVelY = velocityStates.getY();
		initialVelZ = velocityStates.getZ();
		
		//update the velocity with current data from sensors
		velocityStates.update(nextAccelValues, nextRotateValues);
		
		//update the current state distance to be next state initial distance
		initialDistanceX = distances[0];
		initialDistanceY = distances[1];
		initialDistanceZ = distances[2];
	}
}
