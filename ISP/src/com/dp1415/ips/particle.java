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
	
	public void setDistX(double newDistX){
		distX = newDistX;
	}
	
	public final double getDistY(){
		return distY;
	}
	
	public void setDistY(double newDistY){
		distY = newDistY;
	}
	
	public final double getDistZ(){
		return distZ;
	}
	
	public void setDistZ(double newDistZ){
		distZ = newDistZ;
	}
	
	public final double getVelX(){
		return velX;
	}
	
	public void setVelX(double newVelX){
		velX = newVelX;
	}
	
	public final double getVelY(){
		return velY;
	}
	
	public void setVelY(double newVelY){
		velY = newVelY;
	}
	
	public final double getVelZ(){
		return velZ;
	}
	
	public void setVelZ(double newVelZ){
		velZ = newVelZ;
	}
	
	public final double getAccelX(){
		return accelX;
	}
	
	public void setAccelX(double newAccelX){
		accelX = newAccelX;
	}
	

	public final double getAccelY(){
		return accelY;
	}
	
	public void setAccelY(double newAccelY){
		accelY = newAccelY;
	}
	

	public final double getAccelZ(){
		return accelZ;
	}
	
	
	public void setAccelZ(double newAccelZ){
		accelZ = newAccelZ;
	}
	
	public final double getQX(){
		return qX;
	}
	
	public void setQX(double newQX){
		qX = newQX;
	}
	
	public final double getQY(){
		return qY;
	}
	
	public void setQY(double newQY){
		qY = newQY;
	}
	
	public final double getQZ(){
		return qZ;
	}
	
	public void setQZ(double newQZ){
		qZ = newQZ;
	}
	
	public final double getQS(){
		return qS;
	}
	
	public void setQS(double newQS){
		qS = newQS;
	}
	
	public final long getTime(){
		return time;
	}
	
	public void setTime(long newTime){
		time = newTime;
	}

}
