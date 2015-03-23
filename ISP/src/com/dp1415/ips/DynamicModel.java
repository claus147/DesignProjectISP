package com.dp1415.ips;
import java.util.Random;
import java.lang.Math;

import org.apache.commons.math3.distribution.NormalDistribution;

// this class will determine the dynamic model
public class DynamicModel {
	
	private final int distX=0,distY=1,distZ=2,velX=3,velY=4,velZ=5,accelX=6,accelY=7,accelZ=8,qX=9,qY=10,qZ=11,qS=12,weight=13,mode=14;
	private double[][] currentParticles;
	private int numberOfParticles;
	private double[][] nextParticles;
	private double timeInterval;
	private double[][] possibilityMatrix = {
			{1.0/2, 1.0/2, 0},
			{1.0/3, 1.0/3, 1.0/3},
			{0, 1.0/2, 1.0/2}
	};
	private double[][] modeCDF = possibilityMatrix;
	
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
		STILL(0), CONST(1), ACCEL(2);
		
		private final double value;
		
		private Mode(double value){
			this.value = value;
		}
		
		public double getValue(){
			return value;
		}
	}
	public DynamicModel(){
		for (int i = 0; i < possibilityMatrix.length; i++){
			for(int j = 0; j < possibilityMatrix[i].length; j++){
				if (j == 0 ) modeCDF[i][j] = possibilityMatrix[i][j]; 
				else {
					modeCDF[i][j] = modeCDF[i][j-1] + possibilityMatrix[i][j];
				}
			}
		}
	}
	
	//this method will return the next possible mode for particle
	private int modeAnalysis(double currentMode){
		Random randomGenerator = new Random();
		double random = randomGenerator.nextDouble();
		for (int i=0; i<modeCDF[(int) currentMode].length; i++){
			if (random < modeCDF[(int)currentMode][i]){
				return i;
			}	
		}
		return -1;
	}
	
	//this method will return a particle with next state info
	private double[] particleCalculation(double[] particle, double nextMode){
		double[] nextState = particle;
		switch((int)nextMode){
			case 0: {
				nextState = stationary(particle);
				break;
			}
			case 1: {
				nextState = acceleration(particle);
				break;
			}
			case 2: {
				nextState = constant(particle);
				break;
			}
		}
		return nextState;
	}
	
	public void propogate(double[][] particles, int numberOfParticles, double timeInterval){
		this.currentParticles = particles;
		this.numberOfParticles = numberOfParticles;
		this.timeInterval = timeInterval;
		this.nextParticles = new double[numberOfParticles][];
		
		for(int i = 0; i < this.numberOfParticles; i++){
			// step 1: determine mode
			int nextMode = modeAnalysis(this.currentParticles[i][mode]);
			// step 2: calculate next state
			double[] nextParticle = particleCalculation(this.currentParticles[i], nextMode);
			nextParticles[i] = nextParticle;
		}
	}
	
	//getter method
	public final double[][] getParticles(){
		return nextParticles;
	}
	
	//STILL mode calculation
	private double[] stationary(double[] particle){	
		particle[distX] = particle[distX] + distrDistX.sample();
		particle[distY] = particle[distY] + distrDistY.sample();
		particle[distZ] = particle[distZ] + distrDistZ.sample();
		particle[velX] = particle[velX] + distrVelX.sample();
		particle[velY] = particle[velY] + distrVelY.sample();
		particle[velZ] = particle[velZ] + distrVelZ.sample();
		particle[accelX] = particle[accelX] + distrAccelX.sample();
		particle[accelY] = particle[accelY] + distrAccelY.sample();
		particle[accelZ] = particle[accelZ] + distrAccelZ.sample();

		
		return turnCalculation(particle);
	}
	
	//ACCEL mode calculation
	private double[] acceleration(double[] particle){
		double tempAccelX = particle[accelX];
		double tempAccelY = particle[accelY];
		double tempAccelZ = particle[accelZ];
		double nextVelX =particle[velX] + timeInterval * tempAccelX;
		double nextVelY =particle[velY] + timeInterval * tempAccelY;
		double nextVelZ =particle[velZ] + timeInterval * tempAccelZ;
		double nextDistX = particle[distX] + (particle[velX] + nextVelX) * 0.5 * timeInterval;
		double nextDistY = particle[distY] + (particle[velY] + nextVelY) * 0.5 * timeInterval;
		double nextDistZ = particle[distZ] + (particle[velZ] + nextVelZ) * 0.5 * timeInterval;
		
		particle[distX] = nextDistX + distrDistX.sample();
		particle[distY] = nextDistY + distrDistY.sample();
		particle[distZ] = nextDistZ + distrDistZ.sample();
		particle[velX] = nextVelX + distrVelX.sample();
		particle[velY] = nextVelY + distrVelY.sample();
		particle[velZ] = nextVelZ + distrVelZ.sample();
		particle[accelX] = tempAccelX + distrAccelX.sample();
		particle[accelY] = tempAccelY + distrAccelY.sample();
		particle[accelZ] = tempAccelZ + distrAccelZ.sample();

	
		return turnCalculation(particle);
	}
	
	//CONST mode calculation
	private double[] constant(double[] particle){
		double tempDistX = particle[distX] + particle[velX] * timeInterval;
		double tempDistY = particle[distY] + particle[velY] * timeInterval;
		double tempDistZ = particle[distZ] + particle[velZ] * timeInterval;
		
		particle[distX] = tempDistX + distrDistX.sample();
		particle[distY] = tempDistY + distrDistY.sample();
		particle[distZ] = tempDistZ + distrDistZ.sample();
		particle[velX] = particle[velX]+ distrVelX.sample();
		particle[velY] = particle[velY]+ distrVelY.sample();
		particle[velZ] = particle[velZ]+ distrVelZ.sample();
		particle[accelX] = particle[accelX] + distrAccelX.sample();
		particle[accelY] = particle[accelY] + distrAccelY.sample();
		particle[accelZ] = particle[accelZ] + distrAccelZ.sample();

		
		return turnCalculation(particle);
	}
	
	private double[] turnCalculation(double[] particle){
		Random randomGenerator = new Random();
		double random = randomGenerator.nextDouble();
		double radians = randomGenerator.nextDouble() * Math.PI/6;
		//no turning
		if (random < 1.0/3){
			particle[qX] = particle[qX] + QuaX.sample();
			particle[qY] = particle[qY] + QuaY.sample();
			particle[qZ] = particle[qZ] + QuaZ.sample();
			particle[qS] = particle[qS] + QuaS.sample();
		}
		//left turning
		else if (random < 2.0/3 ){
			double[] q = {particle[qX],particle[qY],particle[qZ],particle[qS]};
			q = leftTurn(q,radians);
			particle[qX] = q[0] + QuaS.sample();
			particle[qY] = q[1] + QuaS.sample();
			particle[qZ] = q[2] + QuaS.sample();
			particle[qS] = q[3] + QuaS.sample();
		}
		//right turning
		else{
			double[] q = {particle[qX],particle[qY],particle[qZ],particle[qS]};
			q = rightTurn(q,radians);
			particle[qX] = q[0] + QuaS.sample();
			particle[qY] = q[1] + QuaS.sample();
			particle[qZ] = q[2] + QuaS.sample();
			particle[qS] = q[3] + QuaS.sample();
		}
		
		double number = Math.sqrt(Math.pow(particle[qX], 2) + Math.pow(particle[qY], 2)
				+ Math.pow(particle[qZ], 2) + Math.pow(particle[qS], 2));
		particle[qX] = particle[qX]/number;
		particle[qY] = particle[qY]/number;
		particle[qZ] = particle[qZ]/number;
		particle[qS] = particle[qS]/number;
		return particle;
	}

	//this method will return the orientation after a certain radian left turn
	private double[] leftTurn(double[] q, double radians){
		double[] rotate = {0,0,Math.sin(radians),Math.cos(radians)};
		q = quaternionMultiplication(rotate,q);
		return q;
		
	}
	
	//this method will return the orientation after a certain radian right turn
	private double[] rightTurn(double[] q, double radians){
		double[] rotate = {0,0,Math.sin(2*Math.PI - radians),Math.cos(2*Math.PI - radians)};
		q = quaternionMultiplication(rotate,q);
		return q;
	}
	
	//this method will operate a quaternion multiplication {x,y,z,s}
	private double[] quaternionMultiplication(double[] fa, double[] sa){
		return new double[]{
			fa[3] * sa[0] + fa[0] * sa[3] + fa[1] * sa[2] - fa[2] * sa[1],
			fa[3] * sa[1] + fa[1] * sa[3] + fa[2] * sa[0] - fa[0] * sa[2],
			fa[3] * sa[2] + fa[2] * sa[3] + fa[0] * sa[1] - fa[1] * sa[0],
			fa[3] * sa[3] - fa[0] * sa[0] - fa[1] * sa[1] - fa[2] * sa[2]
		};
	}
}

