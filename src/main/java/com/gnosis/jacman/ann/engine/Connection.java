package com.gnosis.jacman.ann.engine;

import java.io.Serializable;

import com.gnosis.jacman.engine.Globals;

public class Connection implements Serializable {

    private static final long serialVersionUID = 5456;
    private static final double WEIGHT_MODIFIER = 0.5;

    private Neuron sender, reciever;
    private double weight, previousIncrement;

    public Connection(Neuron sender, Neuron receiver, double weight) {
        this.sender = sender;
        this.reciever = receiver;
        this.weight = weight;
        previousIncrement = 0;
    }

    public void adjustWeight(double adjustment) {
        previousIncrement = adjustment;
        weight += adjustment;
    }

    public void randomize() {
        previousIncrement = 0;
        weight = (WEIGHT_MODIFIER - Globals.RNG.nextDouble());
    }

    /**
     * @return the sender
     */
    public Neuron getSender() {
        return sender;
    }
    /**
     * @param sender the sender to set
     */
    public void setSender(Neuron sender) {
        this.sender = sender;
    }
    /**
     * @return the receiver
     */
    public Neuron getReceiver() {
        return reciever;
    }
    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(Neuron receiver) {
        this.reciever = receiver;
    }
    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }
    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String toString() {
        return String.format("%.3f", weight);
    }
    /**
     * @return the reciever
     */
    public Neuron getReciever() {
        return reciever;
    }
    /**
     * @param reciever the reciever to set
     */
    public void setReciever(Neuron reciever) {
        this.reciever = reciever;
    }
    /**
     * @return the previousIncrement
     */
    public double getPreviousIncrement() {
        return previousIncrement;
    }
    /**
     * @param previousIncrement the previousIncrement to set
     */
    public void setPreviousIncrement(double previousIncrement) {
        this.previousIncrement = previousIncrement;
    }

}
