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
		this.setVelX(velX);
		this.setVelY(velY);
		this.setVelZ(velZ);
		this.setAccelX(accelX);
		this.setAccelY(accelY);
		this.setAccelZ(accelZ);
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
	public double getVelX() {
		return velX;
	}

	public void setVelX(double velX) {
		this.velX = velX;
	}

	public double getVelY() {
		return velY;
	}

	public void setVelY(double velY) {
		this.velY = velY;
	}

	public double getVelZ() {
		return velZ;
	}

	public void setVelZ(double velZ) {
		this.velZ = velZ;
	}

	public double getAccelX() {
		return accelX;
	}

	public void setAccelX(double accelX) {
		this.accelX = accelX;
	}

	public double getAccelY() {
		return accelY;
	}

	public void setAccelY(double accelY) {
		this.accelY = accelY;
	}

	public double getAccelZ() {
		return accelZ;
	}

	public void setAccelZ(double accelZ) {
		this.accelZ = accelZ;
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
