package com.gnosis.jacman.ann.engine;

import java.io.Serializable;

import com.gnosis.jacman.engine.Globals;

/**
 * A nerve cluster represents the neurons for a single ghost, or
 * for the player.
 * 
 * @author Brendan McGloin
 */
public class NerveCluster implements Serializable{
	
	private static final long serialVersionUID = 0x6843250;
	
	private Neuron[] cluster;
	private double momentum;
	
	public NerveCluster(int neurons, double momentum){
		cluster = new Neuron[neurons];
		for (int i = 0; i < neurons; i++){
			cluster[i] = new Neuron(.5 -Globals.RNG.nextDouble(), momentum);
		}
		this.momentum = momentum;
	}
	
	public static void connectClusters(NerveCluster sender, NerveCluster reciever){
		for(Neuron s: sender.cluster){
			for (Neuron r: reciever.cluster){
				Neuron.connectNeurons(s, r, 0.5 - Globals.RNG.nextDouble());
			}
		}
	}
	
	public void adjustWeights(double learningRate){
		for (Neuron neuron: cluster){
			neuron.adjustInputWeights(learningRate);
		}
	}
	
	public void reset(){
		for (Neuron neuron: cluster){
			neuron.randomize();
		}
	}
	
	public void generateActivations(){
		for (Neuron neuron: cluster){
			neuron.generateActivationLevel();
		}
	}
	
	public void generateDeltas(double[] targets){
		for (int i = 0; (i < targets.length)&&(i < cluster.length); i++){
			cluster[i].generateDelta(targets[i]);
		}
	}
	
	public void generateDeltas(){
		for (Neuron neuron: cluster){
			neuron.generateDelta();
		}
	}
	
	public void setActivations(double[] activations){
		for (int i = 0; i < activations.length; i++){
			cluster[i].setActivation(activations[i]);
		}
	}
	
	public double[] getActivations(){
		double[] acts = new double[cluster.length];
		
		for (int i = 0; i < cluster.length; i++){
			acts[i] = cluster[i].getActivation();
		}
		
		return acts;
	}

	public Neuron[] getCluster() {
		return cluster;
	}
	
	public int size(){
		return cluster.length;
	}
	
	public double getMomentum() {
		return momentum;
	}

	public void setMomentum(double momentum) {
		this.momentum = momentum;
		for (Neuron neuron: cluster){
			neuron.setMomentum(momentum);
		}
	}
}
