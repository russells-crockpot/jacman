/**
 * 
 */
package com.gnosis.jacman.ann.engine;

import java.io.Serializable;

/**
 * This is you're standard Simple Recursive Artificial Neural Network.
 * For the specification on what neuron represents what, please read
 * the NeuronPecs.txt file.
 * 
 * @author Brendan McGloin
 */
public class ANN implements Serializable{
	
	private static final long serialVersionUID = 0x123890;
	
	private NeuronLayer inputLayer, outputLayer, middleLayer;

	/**
	 * @param inputLayer
	 * @param outputLayer
	 * @param middleLayer
	 */
	public ANN(NeuronLayer inputLayer, NeuronLayer outputLayer,
			NeuronLayer middleLayer) {
		super();
		this.inputLayer = inputLayer;
		this.outputLayer = outputLayer;
		this.middleLayer = middleLayer;
		connectLayers();
	}
	
	public double[] propigate(double[] inVals, double[] targets, double learningRate){
		inputLayer.setActivations(inVals);
		generateActivations();
		double[] out = outputLayer.getActivations();
		generateDeltas(targets);
		adjustWeights(learningRate);
		return out;
	}
	
	public double[] process(double[] inVals){
		inputLayer.setActivations(inVals);
		generateActivations();
		return outputLayer.getActivations();
	}
	
	public void resetContextLayers(){
		middleLayer.resetContextLayer();
	}
	
	public void adjustWeights(double learningRate){
		middleLayer.copyToContextLayer();
		
		outputLayer.adjustWeights(learningRate);
		middleLayer.adjustWeights(learningRate);
	}
	
	public void generateActivations(){
		middleLayer.generateActivations();
		outputLayer.generateActivations();
	}
	
	public void generateDeltas(double[] targets){
		outputLayer.generateDeltas(targets);
		middleLayer.generateDeltas();
	}
	
	private void connectLayers(){
		NeuronLayer.connectLayers(inputLayer, middleLayer);
		NeuronLayer.connectLayers(middleLayer, outputLayer);
	}

	public NeuronLayer getInputLayer() {
		return inputLayer;
	}

	public NeuronLayer getOutputLayer() {
		return outputLayer;
	}

	public NeuronLayer getMiddleLayer() {
		return middleLayer;
	}

	public void setInputLayer(NeuronLayer inputLayer) {
		this.inputLayer = inputLayer;
	}

	public void setOutputLayer(NeuronLayer outputLayer) {
		this.outputLayer = outputLayer;
	}

	public void setMiddleLayer(NeuronLayer middleLayer) {
		this.middleLayer = middleLayer;
	}
	
	public void setMomentum(double momentum){
		outputLayer.setMomentum(momentum);
		middleLayer.setMomentum(momentum);
		inputLayer.setMomentum(momentum);
	}
	
	
	public double getMomentum(){
		return outputLayer.getMomentum();
	}
	
	public void reset(){
		inputLayer.reset();
		middleLayer.reset();
		outputLayer.reset();
	}
}
