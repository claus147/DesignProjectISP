package com.dp1415.ips;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import com.dp1415.ips.DynamicModel.Mode;

public class ParticleFilter {
	
//	private particle[] particles;
	// Lets make particles an array of doubles instead of an object
	private final int distX=0,distY=1,distZ=2,velX=3,velY=4,velZ=5,accelX=6,accelY=7,accelZ=8,qX=9,qY=10,qZ=11,qS=12,weight=13,mode=14;
	private int numOfParticles;
	private double timeInterval = 0.05; // particle filter is called every 50ms. NOT TRUE.  
	private double[][] particles,resampled;
	
	
	public ParticleFilter(){
		
	}

	public void initialize(int numOfParticles,stateVector states){
		// initialize particles
		this.numOfParticles= numOfParticles;
		particles= new double[numOfParticles][15];
		
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
			particles[i][distX] = distrDistX.sample();
			particles[i][distY] = distrDistY.sample(); 
			particles[i][distZ] = distrDistZ.sample();
			particles[i][velX] = distrVelX.sample();
			particles[i][velY] = distrVelY.sample(); 
			particles[i][velZ] = distrVelZ.sample(); 
			particles[i][accelX] = distrAccelX.sample();
			particles[i][accelY] = distrAccelY.sample();
			particles[i][accelZ] = distrAccelZ.sample();
			particles[i][qX] = distrQX.sample();
			particles[i][qY] = distrQY.sample();
			particles[i][qZ] = distrQZ.sample();
			particles[i][qS] = distrQS.sample(); 
			particles[i][weight] = 1.0/numOfParticles;
		}
	}
	
	public void propagate(){
		
	}
	public void normalizeWeight(){
		double totalWeight = 0;
		for (int x = 0; x < numOfParticles; x++){
			totalWeight+=particles[x][weight];
		}
		for (int x = 0; x < numOfParticles; x++){
			particles[x][weight] = (particles[x][weight]/totalWeight);
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
			expectation[0] += particles[x][distX]*particles[x][weight];
			expectation[1] += particles[x][distY]*particles[x][weight];
			expectation[2] += particles[x][distZ]*particles[x][weight];
			expectation[3] += particles[x][qX]*particles[x][weight];
			expectation[4] += particles[x][qY]*particles[x][weight];
			expectation[5] += particles[x][qZ]*particles[x][weight];
			expectation[6] += particles[x][qS]*particles[x][weight];
		}
		return expectation;
		
	}
	
	
	public void resample(){
		//create a new set of particles
		resampled = new double[numOfParticles][];
//		for (int i = 0 ; i < numOfParticles; i++){
//			resampled[i] = new particle(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,(long)0.0,0.0, Mode.STILL);
//		}
		
		//construct CDF
		double[] cdf = new double[numOfParticles];
		for (int i=0;i<numOfParticles;i++){
			if (i==0){
				cdf[i]=particles[i][weight];
			}
			else{
				cdf[i]=cdf[i-1]+particles[i][weight];
			}
			//TODO: make cdf[numOfParticles]=1?
			
			//as well as make a copy of original particles
			resampled[i]=particles[i].clone();
		}
		
		// create random number generator
		Random randomGenerator = new Random();

		for (int x=0;x<numOfParticles;x++){
			double random=randomGenerator.nextDouble();//this generates a number between 0 and 1
			for (int y=0 ; y<numOfParticles; y++){
				if (random<cdf[y]){
					particles[x] = resampled[y].clone();
				}
			}
			particles[x][weight]=(1.0/numOfParticles); //equalize the weights
		}
		
//		for (int x=0;x<numOfParticles;x++){
//			double random=randomGenerator.nextDouble();//this generates a number between 0 and 1
//			int low = 0;
//			int high = numOfParticles;
//			int mid = 0;
//			//binary search to find the particle
//			while (low<=high){
//				mid = low + (high-low)/2;
//				if (random<cdf[mid]){
//					high = mid;
//				}
//				else if (low>cdf[mid]){
//					low = mid+1;
//				}
//				else{
//					break;
//				}
//			}
//			particles[x]=resampled[mid].clone();
//			particles[x][weight]=(1.0/numOfParticles); //equalize the weights
//		}
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
			newWeight = particles[i][weight]*
					distrAccelX.density(particles[i][accelX])*
					distrAccelY.density(particles[i][accelY])*
					distrAccelZ.density(particles[i][accelZ])*
					distrQX.density(particles[i][qX])*
					distrQY.density(particles[i][qY])*
					distrQZ.density(particles[i][qZ])*
					distrQS.density(particles[i][qS]);
			particles[i][weight]=newWeight;
		}
	}

	
}
