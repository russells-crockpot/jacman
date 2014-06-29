/**
 * 
 */
package com.gnosis.jacman.graphics;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.gnosis.jacman.engine.Constants;

/**
 * @author Brendan McGloin
 *
 */
public class TileImageCache implements Constants{
	private static final int CACHE_SIZE = 12;
	private static final float LOAD_FACTOR = 1;
	
	private Map<Integer, BufferedImage> cache;
	
	public static final BufferedImage REGEN_TILE = RESOURCE_LOADER.loadImage("tiles/Regen Tile.gif");
	
	public TileImageCache(){
		
	}
	
	public BufferedImage getImage(int walls){
		return cache.get(walls);
	}
	
	public void loadImages(){
		cache = new HashMap<Integer, BufferedImage>(CACHE_SIZE, LOAD_FACTOR);
		
		cache.put(NORTH|SOUTH, RESOURCE_LOADER.loadImage("tiles/NS Tile.gif"));
		cache.put(EAST|WEST, RESOURCE_LOADER.loadImage("tiles/EW Tile.gif"));
		cache.put(NORTH|EAST, RESOURCE_LOADER.loadImage("tiles/NE Tile.gif"));
		cache.put(NORTH|WEST, RESOURCE_LOADER.loadImage("tiles/NW Tile.gif"));
		cache.put(EAST|SOUTH, RESOURCE_LOADER.loadImage("tiles/SE Tile.gif"));
		cache.put(WEST|SOUTH, RESOURCE_LOADER.loadImage("tiles/SW Tile.gif"));
		
		cache.put(NORTH|SOUTH|EAST, RESOURCE_LOADER.loadImage("tiles/NSE Tile.gif"));
		cache.put(NORTH|SOUTH|WEST, RESOURCE_LOADER.loadImage("tiles/NSW Tile.gif"));
		cache.put(NORTH|EAST|WEST, RESOURCE_LOADER.loadImage("tiles/NEW Tile.gif"));
		cache.put(SOUTH|EAST|WEST, RESOURCE_LOADER.loadImage("tiles/SEW Tile.gif"));
		
		cache.put(NORTH|SOUTH|EAST|WEST, RESOURCE_LOADER.loadImage("tiles/NSEW Tile.gif"));
		cache.put(FILLED, RESOURCE_LOADER.loadImage("tiles/Solid Tile.gif"));
	}
}
