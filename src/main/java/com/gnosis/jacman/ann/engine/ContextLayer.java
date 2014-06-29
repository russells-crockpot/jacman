package com.gnosis.jacman.ann.engine;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This is the context layer for the ANN that acts as it's
 * 'memory'. It is different from other Neuron Layers in that
 * it needs to copy, and remember previous activation values.
 *
 * @author Brendan McGloin
 */
public class ContextLayer extends NeuronLayer implements Serializable {

    private static final long serialVersionUID = 0x6843252;

    public static final double INITIAL_NEURON_ACTIVATION = 0.5;

    private NeuronLayer layer;
    private transient double[] previousActivations;

    public ContextLayer(NeuronLayer layer, int clusters) {
        super(clusters, layer.getMomentum(), false);
        this.layer = layer;
        for (int i = 0; i < layer.getSize(); i++) {
            this.clusters[i] = new NerveCluster(layer.getCluster(i).size(), this.momentum);
        }
        this.previousActivations = null;
    }

    public ContextLayer(NeuronLayer layer) {
        super(layer.getSize(), layer.getMomentum(), false);
        this.layer = layer;
        for (int i = 0; i < layer.getSize(); i++) {
            this.clusters[i] = new NerveCluster(layer.getCluster(i).size(), this.momentum);
        }
        for (NerveCluster s: this.clusters) {
            for (NerveCluster r: layer.clusters) {
                NerveCluster.connectClusters(s, r);
            }
        }
        this.previousActivations = null;
    }

    public void addCluster(NerveCluster cluster) {
        this.clusters[this.size-1] = new NerveCluster(cluster.size(), this.momentum);
        double[] t = new double[this.clusters[this.size-1].size()];
        Arrays.fill(t, INITIAL_NEURON_ACTIVATION);
        this.clusters[this.size-1].setActivations(t);
    }

    public void copy() {
        previousActivations = this.getActivations();
        for (int i = 0; (i < this.clusters.length)&&(i < layer.clusters.length); i++) {
            this.clusters[i].setActivations(layer.clusters[i].getActivations());
        }
    }

    public void reset() {
        previousActivations = null;
        for (NerveCluster cluster: this.clusters) {
            double[] t = new double[cluster.size()];
            Arrays.fill(t, INITIAL_NEURON_ACTIVATION);
            cluster.setActivations(t);
        }
    }

    /**
     * @return the previousActivations
     */
    protected double[] getPreviousActivations() {
        return previousActivations;
    }


}
