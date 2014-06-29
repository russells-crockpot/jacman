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
public class OtherImageCache implements Constants{
	private static final int CACHE_SIZE = 5;
	private static final float LOAD_FACTOR = 1;
	
	public static final String GAME_OVER = "Game over";
	public static final String LIFE = "Life";
	public static final String PAUSED = "Paused";
	
	private Map<String, BufferedImage> cache;
	
	public BufferedImage getImage(String key){
		return cache.get(key);
	}
	
	public void loadImages(){
		cache = new HashMap<String, BufferedImage>(CACHE_SIZE, LOAD_FACTOR);
		
		cache.put(GAME_OVER, RESOURCE_LOADER.loadImage("other/Game over.gif"));
		cache.put(LIFE, RESOURCE_LOADER.loadImage("other/life.gif"));
		cache.put(PAUSED, RESOURCE_LOADER.loadImage("other/Paused.gif"));
	}
	
}
