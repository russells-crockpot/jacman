/**
 *
 */
package com.gnosis.jacman.ann.engine;

import java.io.Serializable;

/**
 * Traditionally, a neuron layer is, a list or array of neurons connected
 * to at least one other layer, however, in this case, it is a list for
 * nerve clusters, each of which, is a list of neurons, so the effect is
 * ultimately the same, it's just the implementation that is different.
 *
 * @author Brendan McGloin
 */
public class NeuronLayer implements Serializable {

    private static final long serialVersionUID = 0x6843251;

    protected NerveCluster[] clusters;
    protected double momentum;
    protected int size, totalSize;
    private ContextLayer contextLayer;

    public NeuronLayer(NerveCluster[] clusters, boolean makeContextLayer) {
        this.clusters = clusters;
        this.momentum = clusters[0].getMomentum();
        this.size = clusters.length;
        if (makeContextLayer) {
            this.contextLayer = new ContextLayer(this);
        } else {
            this.contextLayer = null;
        }
        totalSize = 0;
        for (NerveCluster cluster: clusters) {
            totalSize += cluster.size();
        }
    }

    public NeuronLayer(int clusters, double momentum, boolean makeContextLayer) {
        this.clusters = new NerveCluster[clusters];
        this.momentum = momentum;
        this.size = 0;
        this.totalSize = 0;
        if (makeContextLayer) {
            this.contextLayer = new ContextLayer(this, clusters);
        } else {
            this.contextLayer = null;
        }
    }

    public static void connectLayers(NeuronLayer sender, NeuronLayer reciever) {
        for (NerveCluster s: sender.clusters) {
            for (NerveCluster r: reciever.clusters) {
                NerveCluster.connectClusters(s, r);
            }
        }
    }

    public void reset() {
        for (NerveCluster cluster: clusters) {
            cluster.reset();
        }
        if (contextLayer != null) {
            contextLayer.reset();
        }
    }

    public void generateActivations() {
        for (NerveCluster cluster: clusters) {
            cluster.generateActivations();
        }
    }

    public void addCluster(NerveCluster cluster) {
        clusters[size-1] = cluster;
        size++;
    }

    public NerveCluster getCluster(int index) {
        return clusters[index];
    }

    public void adjustWeights(double learningRate) {
        for (NerveCluster cluster: clusters) {
            cluster.adjustWeights(learningRate);
        }
    }

    public void generateDeltas(double[] targets) {
        int c = 0;
        for (int i = 0; i < clusters.length; i++) {
            for (int j = 0; j < clusters[i].getCluster().length; j++, c++) {
                clusters[i].getCluster()[j].setActivation(targets[c]);
            }
        }
    }

    public void generateDeltas() {
        for (NerveCluster cluster: clusters) {
            cluster.generateDeltas();
        }
    }

    public void setActivations(double[] acts) {
        int c = 0;
        for (int i = 0; i < size; i ++) {
            for (int j = 0; j < clusters[i].size(); j++, c++) {
                clusters[i].getCluster()[j].setActivation(acts[c]);
            }
        }
    }

    public double[] getActivations() {
        double[] acts = new double[totalSize];
        int c = 0;
        for (int i = 0; i < size; i ++) {
            for (int j = 0; j < clusters[i].size(); j++, c++) {
                acts[c] = clusters[i].getCluster()[j].getActivation();
            }
        }

        return acts;
    }

    public int getSize() {
        return clusters.length;
    }

    public double getMomentum() {
        return momentum;
    }

    public void setMomentum(double momentum) {
        this.momentum = momentum;
        for (NerveCluster cluster: clusters) {
            cluster.setMomentum(momentum);
        }
    }

    public void resetContextLayer() {
        if (contextLayer != null) {
            contextLayer.reset();
        }
    }

    public void copyToContextLayer() {
        if (this.contextLayer != null) {
            contextLayer.copy();
        }
    }

    public NerveCluster[] getClusters() {
        return clusters;
    }

    public void setClusters(NerveCluster[] clusters) {
        this.clusters = clusters;
    }

    public ContextLayer getContextLayer() {
        return contextLayer;
    }

    public void setContextLayer(ContextLayer contextLayer) {
        this.contextLayer = contextLayer;
    }

    public double[] getContextLayerPreviousActs() {
        if (contextLayer == null) {
            return null;
        }
        return contextLayer.getPreviousActivations();
    }

    public void setContextLayerActivations(double[] acts) {
        if (this.contextLayer != null) {
            this.contextLayer.setActivations(acts);
        }
    }
}
