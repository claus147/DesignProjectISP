package com.dp1415.ips;

// this class will determine the dynamic model
public class DynamicModel {
	private particle[] currentParticles;
	private int numberOfParticles;
	private particle[] nextParticles;
	private double timeInterval;
	
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
		return null;
	}
	
	//ACCEL mode calculation
	private particle acceleration(particle particle){
		return null;
	}
	
	//CONST mode calculation
	private particle constant(particle particle){
		return null;
	}
}
