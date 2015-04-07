package com.dp1415.ips;
import java.util.Random;
import java.lang.Math;
import org.apache.commons.math3.distribution.NormalDistribution;
import Jama.Matrix;
import Jama.CholeskyDecomposition;

// this class will determine the dynamic model
public class DynamicModel {
	
	private final int distX=0,distY=1,distZ=2,velX=3,velY=4,velZ=5,accelX=6,accelY=7,accelZ=8,qX=9,qY=10,qZ=11,qS=12,weight=13,mode=14;
	private double[][] currentParticles;
	private int numberOfParticles;
	private double[][] nextParticles;
	private double timeInterval;
	private double[][] possibilityMatrix = {
//			{1.0, 0.0, 0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}};
			{2.0/3, 1.0/3},
			{1.0/3, 2.0/3}
	};
	private double[][] modeCDF = possibilityMatrix;
	private double[] noise = new double[3];

	private NormalDistribution QuaX = new NormalDistribution(0,0.001);
	private NormalDistribution QuaY = new NormalDistribution(0,0.001);
	private NormalDistribution QuaZ = new NormalDistribution(0,0.001);
	private NormalDistribution QuaS = new NormalDistribution(0,0.001);
	
	
	
	public enum Mode {
		STILL(0), ACCEL(1);
		
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
	
	private double[] correlatedNoise(double variance){ 
		double[][] QMatrix = new double[][]{
				{variance*Math.pow(timeInterval,5)/20, variance*Math.pow(timeInterval,4)/8, variance*Math.pow(timeInterval,3)/6},
				{variance*Math.pow(timeInterval,4)/8,  variance*Math.pow(timeInterval,3)/3, variance*Math.pow(timeInterval,2)/2},
				{variance*Math.pow(timeInterval,3)/6,  variance*Math.pow(timeInterval,2)/2, variance*timeInterval}
			};
		Matrix transition = new Matrix(QMatrix);
		CholeskyDecomposition cholesky = new CholeskyDecomposition(transition);
		transition = cholesky.getL();
		
		NormalDistribution accelNoise = new NormalDistribution();
		NormalDistribution velocityNoise = new NormalDistribution();
		NormalDistribution distNoise = new NormalDistribution();
		double[] vectorH = new double[]{distNoise.sample(),velocityNoise.sample(),accelNoise.sample()};
		double[][] GMatrix = transition.getArray();
		noise[0] = GMatrix[0][0]*vectorH[0] + GMatrix[0][1]*vectorH[1] + GMatrix[0][2]*vectorH[2];
		noise[1] = GMatrix[1][0]*vectorH[0] + GMatrix[1][1]*vectorH[1] + GMatrix[1][2]*vectorH[2];
		noise[2] = GMatrix[2][0]*vectorH[0] + GMatrix[2][1]*vectorH[1] + GMatrix[2][2]*vectorH[2];
		return noise;	
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
			//case 2: {
				//nextState = constant(particle);
				//break;
			//}
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
		double varianceX=0.00000001;
		double varianceY=0.00000001;
		double varianceZ=0.00000001;
		
		double[] noiseX = correlatedNoise(varianceX);
		double[] noiseY = correlatedNoise(varianceY);
		double[] noiseZ = correlatedNoise(varianceZ);
		
		particle = turnCalculation(particle);
		
		particle[distX] = particle[distX] + noiseX[0];
		particle[distY] = particle[distY] + noiseY[0];
		particle[distZ] = particle[distZ] + noiseZ[0];
		particle[velX] = particle[velX] + noiseX[1];
		particle[velY] = particle[velY] + noiseY[1];
		particle[velZ] = particle[velZ] + noiseZ[1];
		particle[accelX] = particle[accelX] + noiseX[2];
		particle[accelY] = particle[accelY] + noiseY[2];
		particle[accelZ] = particle[accelZ] + noiseZ[2];
		particle[mode] = Mode.STILL.getValue();
		
		return particle;
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
		
		double varianceX=Math.pow((1.4*timeInterval)/3.0,2);
		double varianceY=Math.pow((1.4*timeInterval)/3.0,2);
		double varianceZ=Math.pow((1.4*timeInterval)/3.0,2);
		
		double[] noiseX = correlatedNoise(varianceX);
		double[] noiseY = correlatedNoise(varianceY);
		double[] noiseZ = correlatedNoise(varianceZ);
		
		particle = turnCalculation(particle);
		
		particle[distX] = nextDistX + noiseX[0];
		particle[distY] = nextDistY + noiseY[0];
		particle[distZ] = nextDistZ + noiseZ[0];
		particle[velX] = nextVelX + noiseX[1];
		particle[velY] = nextVelY + noiseY[1];
		particle[velZ] = nextVelZ + noiseZ[1];
		particle[accelX] = tempAccelX + noiseX[2];
		particle[accelY] = tempAccelY + noiseY[2];
		particle[accelZ] = tempAccelZ + noiseZ[2];
		particle[mode] = Mode.ACCEL.getValue();
	
		return particle;
	}
	
	//CONST mode calculation
	/*
	 * private double[] constant(double[] particle){
		double tempDistX = particle[distX] + particle[velX] * timeInterval;
		double tempDistY = particle[distY] + particle[velY] * timeInterval;
		double tempDistZ = particle[distZ] + particle[velZ] * timeInterval;
		
		double varianceX=0.0001;
		double varianceY=0.0001;
		double varianceZ=0.0001;
		
		double[] noiseX = correlatedNoise(varianceX);
		double[] noiseY = correlatedNoise(varianceY);
		double[] noiseZ = correlatedNoise(varianceZ);
		
		particle = turnCalculation(particle);
		
		particle[distX] = tempDistX + noiseX[0];
		particle[distY] = tempDistY + noiseY[0];
		particle[distZ] = tempDistZ + noiseZ[0];
		particle[velX] = particle[velX]+ noiseX[1];
		particle[velY] = particle[velY]+ noiseY[1];
		particle[velZ] = particle[velZ]+ noiseZ[1];
		particle[accelX] = particle[accelX] + noiseX[2];
		particle[accelY] = particle[accelY] + noiseY[2];
		particle[accelZ] = particle[accelZ] + noiseZ[2];

		
		return particle;
	}
	
	*/
	
	//this method will add turn mode and return orientation after turns
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
			//calculate new acceleration direction after quaternion changed
			double[] rotate = {0,0,Math.sin(radians),Math.cos(radians)};
			double[] rotate_con = {0,0,-Math.sin(radians),Math.cos(radians)};
			double[] accel_old = {particle[accelX],particle[accelY],particle[accelZ],0};
			double[] accel_new = quaternionMultiplication(
					quaternionMultiplication(rotate,accel_old),rotate_con);
			particle[accelX] = accel_new[0];
			particle[accelY] = accel_new[1];
			particle[accelZ] = accel_new[2];
			
		}
		//right turning
		else{
			double[] q = {particle[qX],particle[qY],particle[qZ],particle[qS]};
			q = rightTurn(q,radians);
			particle[qX] = q[0] + QuaS.sample();
			particle[qY] = q[1] + QuaS.sample();
			particle[qZ] = q[2] + QuaS.sample();
			particle[qS] = q[3] + QuaS.sample();
			//calculate new acceleration direction after quaternion changed
			double[] rotate = {0,0,Math.sin(2*Math.PI - radians),Math.cos(2*Math.PI - radians)};
			double[] rotate_con = {0,0,-Math.sin(2*Math.PI - radians),Math.cos(2*Math.PI - radians)};
			double[] accel_old = {particle[accelX],particle[accelY],particle[accelZ],0};
			double[] accel_new = quaternionMultiplication(
					quaternionMultiplication(rotate,accel_old),rotate_con);
			particle[accelX] = accel_new[0];
			particle[accelY] = accel_new[1];
			particle[accelZ] = accel_new[2];
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

