package com.gnosis.jacman.ann.engine;

import java.io.Serializable;
import java.util.*;

/**
 * A single neuron in the ANN.
 * 
 * @author Brendan McGloin
 */
public class Neuron implements Serializable{
	private static final long serialVersionUID = 2456;
	
	private HashMap<Neuron, Connection> inputs;
	private HashMap<Neuron, Connection> outputs;
	private Threshold threshold;
	private double momentum;
	private transient double delta, activation;
	
	/**
	 * @return the momentum
	 */
	public double getMomentum() {
		return momentum;
	}

	/**
	 * @param momentum the momentum to set
	 */
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}
	
	public Neuron(double threshold, double momentum) {
		inputs = new HashMap<Neuron, Connection>();
		outputs = new HashMap<Neuron, Connection>();
		this.threshold = new Threshold(threshold);
		this.activation = 0;
		this.momentum = momentum;
	}
	
	public Neuron() {
		inputs = new HashMap<Neuron, Connection>();
		outputs = new HashMap<Neuron, Connection>();
		this.threshold = new Threshold(0);
		this.activation = 0;
	}
	
	public Neuron(boolean outputLayer) {
		inputs = new HashMap<Neuron, Connection>();
		outputs = new HashMap<Neuron, Connection>();
		this.threshold = new Threshold(0);
		this.activation = 0;
	}

	
	/**
	 * @param reciever
	 * @param weight
	 */
	protected void addInput(Neuron sender, Connection connection)
	{
		this.inputs.put(sender, connection);
	}
	
	protected void addOutput(Neuron reciever, Connection connection){
		this.outputs.put(reciever, connection);
	}
	
	public static void connectNeurons(Neuron sender, Neuron reciever, double weight){
		Connection connection = new Connection(sender, reciever, weight);
		reciever.addInput(sender, connection);
		sender.addOutput(reciever, connection);
	}
	
	public void generateDelta(){
		double total = 0;
		//get the total delta of all Neurons in outputs
		for (Neuron output: this.outputs.keySet()){
			total += (output.getDelta() * outputs.get(output).getWeight());
		}
		delta = total * activation * (1 - activation);
	}
	
	public void generateDelta(double target){
		//delta = (target - activation) * activation * (1 - activation)
		delta = (target - this.activation) * this.activation * (1 - this.activation);
	}
	
	public void adjustInputWeights(double learningRate)
	{	
		
		for (Neuron neuron : this.inputs.keySet())
		{
			Connection connection = this.inputs.get(neuron);
			connection.adjustWeight(learningRate * this.delta * neuron.getActivation() + 
					(this.momentum * connection.getPreviousIncrement()));
		}
		
		this.threshold.adjustWeight(learningRate * this.threshold.getActivation() * this.delta + 
				(this.momentum * threshold.getPreviousIncrement()));
	}
	
	public void adjustInputWeights(double expected, double learningRate)
	{
		//go through each input, and adjust the weight accordingly
		for (Neuron neuron : this.inputs.keySet())
		{
			//get the connection for the current input neuron
			Connection connection = this.inputs.get(neuron);
			//set the weight of the connection to be: old weight + (learningRate * delta * the current neuron's activation)
			connection.adjustWeight(learningRate * this.delta * neuron.getActivation() + 
					(this.momentum * connection.getPreviousIncrement()));
		}
		//adjust the threshold as well
		this.threshold.adjustWeight(learningRate * this.threshold.getActivation() * this.delta + 
				(this.momentum * threshold.getPreviousIncrement()));
	}
	
	public void generateActivationLevel()
	{
		double total = 0;
		
		//for every neuron, find it's activation, multiply it by it's connections weight, and add it to the total
		for (Neuron neuron: this.inputs.keySet())
		{
			total += neuron.getActivation() * this.inputs.get(neuron).getWeight();
		}
		//subtract the threshold weight times the threshold's activation
		total += this.threshold.getWeight();
		
		//put the total through the sigmoid function
		this.activation = 1.0 / (1.0 + (Math.exp(-total)));
	}
	
	public void randomize(){
		for (Connection connection: inputs.values()){
			connection.randomize();
		}
		for (Connection connection: outputs.values()){
			connection.randomize();
		}
		threshold.randomize();
	}
	
	/**
	 * @return the inputs
	 */
	public HashMap<Neuron, Connection> getInputs() {
		return inputs;
	}

	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold.getActivation() * threshold.getWeight();
	}

	/**
	 * @return the value
	 */
	public double getActivation() {
		return activation;
	}

	/**
	 * @param value the value to set
	 */
	public void setActivation(double activation) {
		this.activation = activation;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("Activation:\t" + activation + "\n");
		sb.append("Threshold:\t" + threshold + "\n");
		sb.append("Delta:\t" + delta);
		
		return sb.toString();
	}
	
	public synchronized String getWeightData() {
		StringBuilder sb = new StringBuilder();
		int count = 1;
		for (Neuron neuron: inputs.keySet()){
			sb.append(String.format("%d w=%.3f a=%.3f\n", count, inputs.get(neuron).getWeight(), 
					new Double(neuron.getActivation() * inputs.get(neuron).getWeight())));
			count++;
		}
		
		return sb.toString();
	}

	/**
	 * @return the delta
	 */
	public double getDelta() {
		return delta;
	}

	/**
	 * @param delta the delta to set
	 */
	public void setDelta(double delta) {
		this.delta = delta;
	}

	public HashMap<Neuron, Connection> getOutputs() {
		return outputs;
	}

	public void setOutputs(HashMap<Neuron, Connection> outputs) {
		this.outputs = outputs;
	}
}
