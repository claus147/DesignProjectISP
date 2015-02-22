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
			// step 2: calculate next state
		}
		
	}
	
	//this method will return the next possible mode for particle
	private Mode modelAnalysis(Mode currentMode){
		return null;
	}
	
	//this method will return a particle with next state info
	private particle particleCalculation(particle particle){
		return null;
	}
	
	//getter method
	private final particle[] getParticles(){
		return nextParticles;
	}
}
