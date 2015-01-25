package com.dp1415.ips;

public class Distances {
	private double velX; 
	private double velY;
	private double velZ;  
	private double timeStamp;
	private double[]distances = new double[3];
	private double initialDistanceX; 
	private double initialDistanceY; 
	private double initialDistanceZ; 
	private double initialVelX;
	private double initialVelY;
	private double initialVelZ;
	
	//constructor
	public Distances(Velocities velocityStates, double timeStamp, double initialDistanceX,
			double initialDistanceY, double initialDistanceZ,double initialVelX, 
			double initialVelY,double initialVelZ){
		velX = velocityStates.getVelocityX(); 
		velY = velocityStates.getVelocityY(); 
		velZ = velocityStates.getVelocityZ(); 
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
	
	public final double getVelocityX(){
		distances[0] = integration(initialVelX,velX)+initialDistanceX;
		return distances[0];
	}
	
	public final double getVelocityY(){
		distances[1] = integration(initialVelY,velY)+initialDistanceY;
		return distances[1];
	}
	
	public final double getVelocityZ(){
		distances[2] = integration(initialVelZ,velZ)+initialDistanceZ;
		return distances[2];
	}
}
