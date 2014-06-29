/**
 * 
 */
package com.gnosis.jacman.editor.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.gnosis.jacman.engine.Constants;

/**
 * @author Brendan McGloin
 *
 */
public class EnemyImages implements Constants {
	private static final Map<String, BufferedImage> CACHE = setupCache();
	
	public static BufferedImage getEnemyImage(String color){
		return CACHE.get(color.toLowerCase());
	}
	
	private static Map<String, BufferedImage> setupCache(){
		Map<String, BufferedImage> cache = new HashMap<String, BufferedImage>();
		
		cache.put("cyan", loadEnemyImage("."+SEPARATOR+"Cyan ghost 1.gif"));
		cache.put("orange", loadEnemyImage("."+SEPARATOR+"Orange ghost 1.gif"));
		cache.put("pink", loadEnemyImage("."+SEPARATOR+"Pink ghost 1.gif"));
		cache.put("purple", loadEnemyImage("."+SEPARATOR+"Purple ghost 1.gif"));
		cache.put("red", loadEnemyImage("."+SEPARATOR+"Red ghost 1.gif"));
		
		return cache;
	}
	
	private static BufferedImage loadEnemyImage(String path){
		try{
			return ImageIO.read(new File(path));
		} catch (IOException e){
			//TODO LOGGER.log(SEVERE, "Couldn't load iamge: " + path);
			return null;
		}
	}
}
