package com.dp1415.ips;

public class particle {

	private double distX,distY,distZ,velX,velY,velZ,accelX,accelY,accelZ,qX,qY,qZ,qS,weight;
	private long time;
	
	public particle(double distX, 
			double distY, double distZ, double velX,double velY,
			double velZ, double accelX, double accelY, double accelZ,
			double qX, double qY, double qZ, double qS,
			long time, double weight){

		this.distX = distX;
		this.distY = distY;
		this.distZ = distZ;
		this.velX = velX;
		this.velY = velY;
		this.velZ = velZ;
		this.accelX = accelX;
		this.accelY = accelY;
		this.accelZ = accelZ;
		this.qX = qX;
		this.qY = qY;
		this.qZ = qZ;
		this.qS = qS;
		this.time = time;	
		this.weight = weight;
		
	}
	
	public final double getWeight(){
		return weight;
	}
	public void setWeight(double newWeight){
		weight = newWeight;	
	}
	
	public final double getDistX(){
		return distX;
	}
	public final double getDistY(){
		return distY;
	}
	public final double getDistZ(){
		return distZ;
	}
	public final double getQX(){
		return qX;
	}
	public final double getQY(){
		return qY;
	}
	public final double getQZ(){
		return qZ;
	}
	public final double getQS(){
		return qS;
	}

}
