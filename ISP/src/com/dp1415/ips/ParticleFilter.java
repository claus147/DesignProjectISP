package com.dp1415.ips;

import java.util.Random;

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
	
	public void resample(){
		//create a new set of particles
		particle[] resampled = new particle[numOfParticles];

		//construct CDF
		double[] cdf = new double[numOfParticles];
		for (int i=0;i<numOfParticles;i++){
			if (i==0){
				cdf[i]=particles[i].getWeight();
			}
			else{
				cdf[i]=cdf[i-1]+particles[i].getWeight();
			}
			//TODO: make cdf[numOfParticles]=1?
		}
		
		// create random number generator
		Random randomGenerator = new Random();

		for (int x=0;x<numOfParticles;x++){
			double random=randomGenerator.nextDouble();//this generates a number between 0 and 1
			int low = 0;
			int high = numOfParticles;
			int mid = 0;
			//binary search to find the particle
			while (low<=high){
				mid = low + (high-low)/2;
				if (random<cdf[mid]){
					high = mid;
				}
				else if (low>cdf[mid]){
					low = mid+1;
				}
				else{
					break;
				}
			}
			resampled[x] = particles[mid];
		}
		particles = resampled;
	}
	
	/**take a particle as input, compare its measurement model with actual measurements 
	 * weight accordingly
	 * 
	 */
	public double updateWeight(particle particle){
		double newWeight = 0;
		//need to get sensor reading (looking at MainActivity) with MM
		//need to get the actual probability, need a threshold most likely
		//give a measurement return prob given mean (real reading) and s.d.
		//need to fully understand what the normal distribution formula actually shows
		
		return newWeight;
	}
	
}
