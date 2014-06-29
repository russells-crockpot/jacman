/**
 *
 */
package com.gnosis.jacman.engine.items;

import com.gnosis.jacman.ann.engine.Neuron;
import com.gnosis.jacman.engine.Constants;
import com.gnosis.jacman.engine.Item;

/**
 * A power blip is a larger blip that is worth more points than a
 * normal blip, and allows the player to eat the ghosts.
 *
 * @author Brendan McGloin
 */
public class PowerBlip extends Item implements Constants {

    private static final long serialVersionUID = 0x4856fea0;

    private Neuron neuron;
    private transient boolean exists = true;

    public PowerBlip() {
        exists = true;
    }

    /* (non-Javadoc)
     * @see engine.Item#getName()
     */
    public String getName() {
        return "Power blip";
    }

    /* (non-Javadoc)
     * @see engine.Item#getPoints()
     */
    public int getPoints() {
        return 300;
    }

    /**
     * @return the neuron
     */
    public Neuron getNeuron() {
        return neuron;
    }

    /**
     * @param neuron the neuron to set
     */
    public void setNeuron(Neuron neuron) {
        this.neuron = neuron;
        this.neuron.setActivation((exists)? 1 : 0);
    }

    public boolean exists() {
        return exists;
    }

    /**
     * @return the exists
     */
    public boolean isExists() {
        return exists;
    }

    /**
     * @param exists the exists to set
     */
    public void setExists(boolean exists) {
        //HACK
        if (neuron != null) {
            if (exists) {
                neuron.setActivation(1);
            } else {
                neuron.setActivation(0);
            }

        } else {
            //TODO LOGGER.log(SEVERE, "power blip Neuron is null");
        }
        this.exists = exists;
    }

}
