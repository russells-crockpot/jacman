/**
 * 
 */
package com.gnosis.jacman.ann.engine;

/**
 * @author Brendan McGloin
 *
 */
public class NerveClusterTestCase extends TestCase {
	
	private int clusterIndex;
	
	/**
	 * @param inputs
	 * @param outputs
	 * @param previousContextLayerActs
	 */
	public NerveClusterTestCase(double[] inputs, double[] outputs,
			double[] previousContextLayerActs, int clusterIndex) {
		super(inputs, outputs, previousContextLayerActs);
		this.clusterIndex = clusterIndex;
	}

	/**
	 * @param inputs
	 * @param outputs
	 * @param previousContextLayerActs
	 * @param lr
	 */
	public NerveClusterTestCase(double[] inputs, double[] outputs,
			double[] previousContextLayerActs, double lr,  int clusterIndex) {
		super(inputs, outputs, previousContextLayerActs, lr);
		this.clusterIndex = clusterIndex;
	}

	/* (non-Javadoc)
	 * @see ann.engine.TestCase2#apply(ann.engine.ANN2)
	 */
	@Override
	public void apply(ANN net) {
		//TODO figure out a way to do this where is also adjusts the input layer weights
		net.getMiddleLayer().setContextLayerActivations(previousContextLayerActs);
		net.process(inputs);
		NerveCluster cluster = net.getOutputLayer().getCluster(clusterIndex);
		cluster.generateDeltas(outputs);
		cluster.adjustWeights(lr);
	}
	
}
