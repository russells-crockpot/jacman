package com.gnosis.jacman.engine.items;

import com.gnosis.jacman.engine.Item;

/**
 * Acts as a place holder in tile objects.
 *
 * @author Brendan McGloin
 */
public class NoItem extends Item {
    private static final long serialVersionUID = 0x558621;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getPoints() {
        return 0;
    }

}
