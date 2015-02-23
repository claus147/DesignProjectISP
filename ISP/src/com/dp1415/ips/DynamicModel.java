package com.dp1415.ips;
import org.apache.commons.math3.distribution.NormalDistribution;

// this class will determine the dynamic model
public class DynamicModel {
	private particle[] currentParticles;
	private int numberOfParticles;
	private particle[] nextParticles;
	private double timeInterval;
	private NormalDistribution distrDistX = new NormalDistribution();
	private NormalDistribution distrDistY = new NormalDistribution();
	private NormalDistribution distrDistZ = new NormalDistribution();
	private NormalDistribution distrVelX = new NormalDistribution();
	private NormalDistribution distrVelY = new NormalDistribution();
	private NormalDistribution distrVelZ = new NormalDistribution();
	private NormalDistribution distrAccelX = new NormalDistribution();
	private NormalDistribution distrAccelY = new NormalDistribution();
	private NormalDistribution distrAccelZ = new NormalDistribution();
	private NormalDistribution QuaX = new NormalDistribution();
	private NormalDistribution QuaY = new NormalDistribution();
	private NormalDistribution QuaZ = new NormalDistribution();
	private NormalDistribution QuaS = new NormalDistribution();
	
	
	public enum Mode {
		STILL, CONST, ACCEL;
	}
	public DynamicModel(particle[] particles, int numberOfParticles, double timeInterval){
		this.currentParticles = particles;
		this.numberOfParticles = numberOfParticles;
		this.timeInterval = timeInterval;
		
		for(int i = 0; i < this.numberOfParticles; i++){
			// step 1: determine mode
			Mode nextMode = modeAnalysis(this.currentParticles[i].getMode());
			// step 2: calculate next state
			particle nextParticle = particleCalculation(this.currentParticles[i], nextMode);
			nextParticles[i] = nextParticle;
		}
		
	}
	
	//this method will return the next possible mode for particle
	private Mode modeAnalysis(Mode currentMode){
		return null;
	}
	
	//this method will return a particle with next state info
	private particle particleCalculation(particle particle, Mode nextMode){
		particle nextState = particle;
		switch(nextMode){
			case STILL: {
				nextState = stationary(particle);
				break;
			}
			case ACCEL: {
				nextState = acceleration(particle);
				break;
			}
			case CONST: {
				nextState = constant(particle);
				break;
			}
		}
		return nextState;
	}
	
	//getter method
	private final particle[] getParticles(){
		return nextParticles;
	}
	
	//STILL mode calculation
	private particle stationary(particle particle){
		
		double newTime = particle.getTime()+timeInterval;
		
		particle.setDistX(particle.getDistX() + distrDistX.sample());
		particle.setDistY(particle.getDistY() + distrDistY.sample());
		particle.setDistZ(particle.getDistZ() + distrDistZ.sample());
		particle.setVelX(particle.getVelX() + distrVelX.sample());
		particle.setVelY(particle.getVelY() + distrVelY.sample());
		particle.setVelZ(particle.getVelZ() + distrVelZ.sample());
		particle.setAccelX(particle.getAccelX() + distrAccelX.sample());
		particle.setAccelY(particle.getAccelY() + distrAccelY.sample());
		particle.setAccelZ(particle.getAccelZ() + distrAccelZ.sample());
		particle.setQX(particle.getQX() + QuaX.sample());
		particle.setQY(particle.getQY() + QuaY.sample());
		particle.setQZ(particle.getQZ() + QuaZ.sample());
		particle.setQS(particle.getQS() + QuaS.sample());
		particle.setTime(newTime);
		
		return particle;
	}
	
	//ACCEL mode calculation
	private particle acceleration(particle particle){
		double newTime = particle.getTime()+timeInterval;
		double accelX = particle.getAccelX();
		double accelY = particle.getAccelY();
		double accelZ = particle.getAccelZ();
		double nextVelX =particle.getVelX() + timeInterval * accelX;
		double nextVelY =particle.getVelY() + timeInterval * accelY;
		double nextVelZ =particle.getVelZ() + timeInterval * accelZ;
		double nextDistX = particle.getDistX() + (particle.getVelX() + nextVelX) * 0.5 * timeInterval;
		double nextDistY = particle.getDistX() + (particle.getVelY() + nextVelY) * 0.5 * timeInterval;
		double nextDistZ = particle.getDistX() + (particle.getVelZ() + nextVelZ) * 0.5 * timeInterval;
		
		particle.setDistX(nextDistX + distrDistX.sample());
		particle.setDistY(nextDistY + distrDistY.sample());
		particle.setDistZ(nextDistZ + distrDistZ.sample());
		particle.setVelX(nextVelX + distrVelX.sample());
		particle.setVelY(nextVelY + distrVelY.sample());
		particle.setVelZ(nextVelZ + distrVelZ.sample());
		particle.setAccelX(accelX + distrAccelX.sample());
		particle.setAccelY(accelY + distrAccelY.sample());
		particle.setAccelZ(accelZ + distrAccelZ.sample());
		particle.setQX(particle.getQX() + QuaX.sample());
		particle.setQY(particle.getQY() + QuaY.sample());
		particle.setQZ(particle.getQZ() + QuaZ.sample());
		particle.setQS(particle.getQS() + QuaS.sample());
		particle.setTime(newTime);
	
		return particle;
	}
	
	//CONST mode calculation
	private particle constant(particle particle){
		double distX = particle.getDistX()+particle.getVelX()*timeInterval;
		double distY = particle.getDistY()+particle.getVelY()*timeInterval;
		double distZ = particle.getDistZ()+particle.getVelZ()*timeInterval;
		double newTime = particle.getTime()+timeInterval;
		
		particle.setDistX(distX + distrDistX.sample());
		particle.setDistY(distY + distrDistY.sample());
		particle.setDistZ(distZ + distrDistZ.sample());
		particle.setVelX(particle.getVelX() + distrVelX.sample());
		particle.setVelY(particle.getVelY() + distrVelY.sample());
		particle.setVelZ(particle.getVelZ() + distrVelZ.sample());
		particle.setAccelX(particle.getAccelX() + distrAccelX.sample());
		particle.setAccelY(particle.getAccelY() + distrAccelY.sample());
		particle.setAccelZ(particle.getAccelZ() + distrAccelZ.sample());
		particle.setQX(particle.getQX() + QuaX.sample());
		particle.setQY(particle.getQY() + QuaY.sample());
		particle.setQZ(particle.getQZ() + QuaZ.sample());
		particle.setQS(particle.getQS() + QuaS.sample());
		particle.setTime(newTime);
		return particle;
	}
}
