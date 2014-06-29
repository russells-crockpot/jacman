/**
 * 
 */
package com.gnosis.jacman.ann.engine;

/**
 * @author Brendan McGloin
 *
 */
public class TestCase {
	
	protected double[] inputs, outputs, previousContextLayerActs;
	protected double lr;
	/**
	 * @param inputs
	 * @param outputs
	 * @param previousContextLayerActs
	 */
	public TestCase(double[] inputs, double[] outputs,
			double[] previousContextLayerActs) {
		this(inputs, outputs, previousContextLayerActs, 0.0);
	}
	
	/**
	 * @param inputs
	 * @param outputs
	 * @param previousContextLayerActs
	 * @param lr
	 */
	public TestCase(double[] inputs, double[] outputs,
			double[] previousContextLayerActs, double lr) {
		super();
		this.inputs = inputs;
		this.outputs = outputs;
		this.previousContextLayerActs = previousContextLayerActs;
		this.lr = lr;
	}
	
	public void apply(ANN net){
		net.getMiddleLayer().setContextLayerActivations(previousContextLayerActs);
		net.propigate(inputs, outputs, lr);
	}

	/**
	 * @return the inputs
	 */
	public double[] getInputs() {
		return inputs;
	}

	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(double[] inputs) {
		this.inputs = inputs;
	}

	/**
	 * @return the outputs
	 */
	public double[] getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the outputs to set
	 */
	public void setOutputs(double[] outputs) {
		this.outputs = outputs;
	}

	/**
	 * @return the previousContextLayerActs
	 */
	public double[] getPreviousContextLayerActs() {
		return previousContextLayerActs;
	}

	/**
	 * @param previousContextLayerActs the previousContextLayerActs to set
	 */
	public void setPreviousContextLayerActs(double[] previousContextLayerActs) {
		this.previousContextLayerActs = previousContextLayerActs;
	}

	/**
	 * @return the lr
	 */
	public double getLr() {
		return lr;
	}

	/**
	 * @param lr the lr to set
	 */
	public void setLr(double lr) {
		this.lr = lr;
	}
	
	
	
	
	
}
