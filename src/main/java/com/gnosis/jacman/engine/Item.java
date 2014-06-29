/**
 * 
 */
package com.gnosis.jacman.engine;

import java.io.Serializable;

/**
 * Each tile can contain a single item. All such items must be
 * a child of this class.
 * 
 * @author Brendan McGloin
 */
@SuppressWarnings("serial")
public abstract class Item implements Serializable{
	
	/**
	 * Returns how many points the player receives when the eat the
	 * item.
	 * 
	 * @return How many points the item is worth
	 */
	public abstract int getPoints();
	
	/**
	 * Returns the name of the item
	 * 
	 * @return the item's name
	 */
	public abstract String getName();
	
}
