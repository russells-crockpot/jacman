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
public class PlayerImageCache implements Constants{
	private static final int CACHE_SIZE = 4;
	private static final float LOAD_FACTOR = 1;
	
	public static final BufferedImage DEAD_JACMAN = RESOURCE_LOADER.loadImage("player/DeadJacMan.gif");
	
	private Map<Integer, BufferedImage> cache1, cache2;
	
	public PlayerImageCache(){
		loadImages();
	}
	
	public BufferedImage getImage(int direction, boolean useImage1){
		if (useImage1){
			return cache1.get(direction);
		}
		return cache2.get(direction);
	}
	
	public void loadImages(){
		loadCache1();
		loadCache2();
	}
	
	private void loadCache1(){
		cache1 = new HashMap<Integer, BufferedImage>(CACHE_SIZE, LOAD_FACTOR);
		cache1.put(NORTH, RESOURCE_LOADER.loadImage("player/JacMan N 1.gif"));
		cache1.put(SOUTH, RESOURCE_LOADER.loadImage("player/JacMan S 1.gif"));
		cache1.put(EAST, RESOURCE_LOADER.loadImage("player/JacMan E 1.gif"));
		cache1.put(WEST, RESOURCE_LOADER.loadImage("player/JacMan W 1.gif"));
	}
	private void loadCache2(){
		cache2 = new HashMap<Integer, BufferedImage>(CACHE_SIZE, LOAD_FACTOR);
		cache2.put(NORTH, RESOURCE_LOADER.loadImage("player/JacMan N 2.gif"));
		cache2.put(SOUTH, RESOURCE_LOADER.loadImage("player/JacMan S 2.gif"));
		cache2.put(EAST, RESOURCE_LOADER.loadImage("player/JacMan E 2.gif"));
		cache2.put(WEST, RESOURCE_LOADER.loadImage("player/JacMan W 2.gif"));
	}
}
