package com.dp1415.ips;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

public class ParticleFilter {
	
	private stateVector sv = null; //dont want too populate it now, no data to pop with
	
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
		// populate all the particles with equal weight and the same initial state.
		for (int i = 0 ; i < numOfParticles; i++){
			particles[i] = new particle(states.getDistance().getX(), states.getDistance().getY(), states.getDistance().getZ(),
					states.getVelocity().getX(), states.getVelocity().getY(), states.getVelocity().getZ(), 
					states.getAcceleration().getX(),states.getAcceleration().getY(),states.getAcceleration().getZ(),
					states.getRotationX(), states.getRotationY(), states.getRotationZ(), states.getRotationS(), states.getTime(), 1/numOfParticles);
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
	
	/**Update the StateVector to be the most up to date, take the Accel and Rot values and 
	 * model them as normal distributions with a given mean (not yet determined)
	 * 
	 * iterate through each particle and update the weights accordingly by multiplying the
	 * prob of the particle compared to the SV (for all the Accel and Rot) and multiplying
	 * everything together
	 * 
	 */
	
	public void updateWeights(){
		float [] nextAccelValues = null, nextRotateValues = null; //just so no errors, should be passed in somehow
		
		double sdAX = 1, sdAY = 1, sdAZ = 1, sdQX = 1, sdQY = 1, sdQZ = 1,sdQS = 1;//standard deviations
		
		sv.update(nextAccelValues, nextRotateValues); //update the values to use
		
		//get normal distributions of each measurement
		NormalDistribution distrAccelX = new NormalDistribution(sv.getAcceleration().getX(),sdAX);
		NormalDistribution distrAccelY = new NormalDistribution(sv.getAcceleration().getY(),sdAY);
		NormalDistribution distrAccelZ = new NormalDistribution(sv.getAcceleration().getZ(),sdAZ);
		NormalDistribution distrQX = new NormalDistribution(sv.getRotationX(),sdQX);
		NormalDistribution distrQY = new NormalDistribution(sv.getRotationY(),sdQY);
		NormalDistribution distrQZ = new NormalDistribution(sv.getRotationZ(),sdQZ);
		NormalDistribution distrQS = new NormalDistribution(sv.getRotationS(),sdQZ);
		
		//do actual new weight assignment
		double newWeight = 0;
		for (int i = 0; i < particles.length; i++){
			newWeight = getProbAndMult(distrAccelX, particles[i].getAccelX())*
					getProbAndMult(distrAccelY, particles[i].getAccelY())*
					getProbAndMult(distrAccelZ, particles[i].getAccelZ())*
					getProbAndMult(distrQX, particles[i].getQX())*
					getProbAndMult(distrQY, particles[i].getQY())*
					getProbAndMult(distrQZ, particles[i].getQZ())*
					getProbAndMult(distrQS, particles[i].getQS());
			particles[i].setWeight(newWeight);
		}
	}

	//helper for updateWeights() - to shorten the line basically
	//this might not be correct... not sure
	private double getProbAndMult(NormalDistribution distr, double particleItem){
		return particleItem*(1 - distr.cumulativeProbability(particleItem)); //0.5 is most likely 0 is least likely
	}
	
}
