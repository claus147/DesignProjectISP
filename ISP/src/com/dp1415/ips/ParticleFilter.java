package com.dp1415.ips;

public class ParticleFilter {
	private particle[] particles;
	private int numOfParticles;
	//	TO DO:
			//Determine mode
			//Implement dynamic models to get the next state
			//Update the weights
			//Resample
			//Use Random Number Generator to propagate new particle by weight
			//Calculate expectation to get location
			//update it on map

	public void initialize(int numOfParticles,stateVector states){
		// initialize particles
		particles= new particle[numOfParticles];
		// populate all the particles with equal weight and the same initial state.
		for (int i = 0 ; i < numOfParticles; i++){
			particles[i] = new particle(states.getDistance().getX(), states.getDistance().getY(), states.getDistance().getZ(),
					states.getVelocity().getX(), states.getVelocity().getY(), states.getVelocity().getZ(), 
					states.getAcceleration().getX(),states.getAcceleration().getY(),states.getAcceleration().getZ(),
					states.getRotationX(), states.getRotationY(), states.getRotationZ(), states.getRotationS(), states.getTime(), 1/numOfParticles);
		}
		this.numOfParticles= numOfParticles; 
	}
	
	public void normalizeWeight(){
		double totalWeight = 0;
		for (int x = 0; x < numOfParticles; x++){
			totalWeight+=particles[x].getWeight();
		}
		for (int x = 0; x < numOfParticles; x++){
			particles[x].setWeight(particles[x].getWeight()/totalWeight);
		}
	}
	
	public double[] expectation(){
		double[] location = new double[7];
		//location is an array that contains information to be updated in gps
		// index		variables
		//	0			distX
		//	1			distY
		//	2			distZ
		//	3			qX (quaternion X component)
		//	4			qY
		//	5			qZ
		//	6			qS
		for(int x = 0; x < 7; x++){
			location[x] = 0;
		}
		//go through all the particles to calculate the expectation of each component
		for (int x = 0; x < numOfParticles; x++){
			location[0] += particles[x].getDistX()*particles[x].getWeight();
			location[1] += particles[x].getDistY()*particles[x].getWeight();
			location[2] += particles[x].getDistZ()*particles[x].getWeight();
			location[3] += particles[x].getQX()*particles[x].getWeight();
			location[4] += particles[x].getQY()*particles[x].getWeight();
			location[5] += particles[x].getQZ()*particles[x].getWeight();
			location[6] += particles[x].getQS()*particles[x].getWeight();
		}
		return location;
		
	}
	
}
