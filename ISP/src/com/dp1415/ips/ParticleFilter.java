package com.dp1415.ips;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

public class ParticleFilter {
	
	private particle[] particles;
	private int numOfParticles;
	private long timeStamp = (long) 0.1; // 100ms
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
		
		// Normal Distribution, assume standard deviation as 1
		NormalDistribution distrDistX = new NormalDistribution(states.getDistance().getX(),1);
		NormalDistribution distrDistY = new NormalDistribution(states.getDistance().getY(),1);
		NormalDistribution distrDistZ = new NormalDistribution(states.getDistance().getZ(),1);
		NormalDistribution distrVelX = new NormalDistribution(states.getVelocity().getX(),1);
		NormalDistribution distrVelY = new NormalDistribution(states.getVelocity().getY(),1);
		NormalDistribution distrVelZ = new NormalDistribution(states.getVelocity().getZ(),1);
		NormalDistribution distrAccelX = new NormalDistribution(states.getAcceleration().getX(),1);
		NormalDistribution distrAccelY = new NormalDistribution(states.getAcceleration().getY(),1);
		NormalDistribution distrAccelZ = new NormalDistribution(states.getAcceleration().getZ(),1);
		NormalDistribution distrQX = new NormalDistribution(states.getRotationX(),1);
		NormalDistribution distrQY = new NormalDistribution(states.getRotationY(),1);
		NormalDistribution distrQZ = new NormalDistribution(states.getRotationZ(),1);
		NormalDistribution distrQS = new NormalDistribution(states.getRotationS(),1);
		
		// populate all the particles with equal weight and the same initial state.
		for (int i = 0 ; i < numOfParticles; i++){
			particles[i] = new particle(
					distrDistX.sample(), 
					distrDistY.sample(), 
					distrDistZ.sample(),
					distrVelX.sample(),
					distrVelY.sample(), 
					distrVelZ.sample(), 
					distrAccelX.sample(),
					distrAccelY.sample(),
					distrAccelZ.sample(),
					distrQX.sample(), 
					distrQY.sample(),
					distrQZ.sample(),
					distrQS.sample(), 
					states.getTime(), 
					1/numOfParticles);
		}
		this.numOfParticles= numOfParticles; 
	}
	
	public void propagate(stateVector states){
		//for constant speed, acceleration = 0
		
		for (int i = 0; i<numOfParticles; i++){
			double speedX = particles[i].getVelX();
			double speedY = particles[i].getVelY();
			double speedZ = particles[i].getVelZ();
			double initDistX = particles[i].getDistX();
			double initDistY = particles[i].getDistY();
			double initDistZ = particles[i].getDistZ();
			double newDistX = initDistX + speedX*timeStamp;
			double newDistY = initDistY + speedY*timeStamp;
			double newDistZ = initDistZ + speedZ*timeStamp;
			long newTime = particles[i].getTime()+timeStamp;
			
			Random randomGenerator = new Random();
			double standardDev = 0.05; // standard Deviation = 0.05
			double noise = standardDev * randomGenerator.nextGaussian();
			
			particles[i].setDistX(newDistX+noise);
			particles[i].setDistY(newDistY+noise);
			particles[i].setDistZ(newDistZ+noise);
			particles[i].setAccelX(0);
			particles[i].setAccelY(0);
			particles[i].setAccelZ(0);
			particles[i].setTime(newTime);
			
		}
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
		double[] expectation = new double[7];
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
			expectation[x] = 0;
		}
		//go through all the particles to calculate the expectation of each component
		for (int x = 0; x < numOfParticles; x++){
			expectation[0] += particles[x].getDistX()*particles[x].getWeight();
			expectation[1] += particles[x].getDistY()*particles[x].getWeight();
			expectation[2] += particles[x].getDistZ()*particles[x].getWeight();
			expectation[3] += particles[x].getQX()*particles[x].getWeight();
			expectation[4] += particles[x].getQY()*particles[x].getWeight();
			expectation[5] += particles[x].getQZ()*particles[x].getWeight();
			expectation[6] += particles[x].getQS()*particles[x].getWeight();
		}
		return expectation;
		
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
			resampled[x].setWeight(1/numOfParticles); //equalize the weights
		}
		particles = resampled;
	}
	
	/**ASSUMES StateVector is the most up to date. 
	 * Take the Accel and Rot values and 
	 * model them as normal distributions with a given mean (not yet determined)
	 * 
	 * iterate through each particle and update the weights accordingly by multiplying the
	 * prob of the particle compared to the SV (for all the Accel and Rot) and multiplying
	 * everything together
	 * 
	 */
	

	public void updateWeights(stateVector sv){
		
		double sdAX = 1, sdAY = 1, sdAZ = 1, sdQX = 1, sdQY = 1, sdQZ = 1,sdQS = 1;//standard deviations
				
		//get normal distributions of each measurement
		NormalDistribution distrAccelX = new NormalDistribution(sv.getAcceleration().getX(),sdAX);
		NormalDistribution distrAccelY = new NormalDistribution(sv.getAcceleration().getY(),sdAY);
		NormalDistribution distrAccelZ = new NormalDistribution(sv.getAcceleration().getZ(),sdAZ);
		NormalDistribution distrQX = new NormalDistribution(sv.getRotationX(),sdQX);
		NormalDistribution distrQY = new NormalDistribution(sv.getRotationY(),sdQY);
		NormalDistribution distrQZ = new NormalDistribution(sv.getRotationZ(),sdQZ);
		NormalDistribution distrQS = new NormalDistribution(sv.getRotationS(),sdQZ);
		
		//NOT DOING ln(weight) yet 
		
		//pdf of measurement at a given estimate is the same as the otherway round. (saving space this way)
		//do actual new weight assignment
		double newWeight = 0;
		for (int i = 0; i < particles.length; i++){
			newWeight = particles[i].getWeight()*
					distrAccelX.density(particles[i].getAccelX())*
					distrAccelY.density(particles[i].getAccelY())*
					distrAccelZ.density(particles[i].getAccelZ())*
					distrQX.density(particles[i].getQX())*
					distrQY.density(particles[i].getQY())*
					distrQZ.density(particles[i].getQZ())*
					distrQS.density(particles[i].getQS());
			particles[i].setWeight(newWeight);
		}
	}

	
}
