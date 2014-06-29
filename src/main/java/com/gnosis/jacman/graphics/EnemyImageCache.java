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
public class EnemyImageCache implements Constants{

	private static final int IMAGES_PER_MAP = 9;
	private static final float LOAD_FACTOR = 1;

	private Map<String, BufferedImage> image1s, image2s;
	public static final BufferedImage EYES = RESOURCE_LOADER.loadImage("enemy/ghost eyes.gif");

	public EnemyImageCache(){
	}

	public BufferedImage getImage(String color, boolean useImage1){
		if(useImage1){
			return image1s.get(color);
		}
		else{
			return image2s.get(color);
		}
	}

	public void loadImages(){
		loadImage1s();
		loadImage2s();
	}

	private void loadImage1s(){
		image1s = new HashMap<String, BufferedImage>(IMAGES_PER_MAP, LOAD_FACTOR);

		image1s.put(CYAN, RESOURCE_LOADER.loadImage("enemy/"+CYAN+" ghost 1.gif"));
		image1s.put(GREEN, RESOURCE_LOADER.loadImage("enemy/"+GREEN+" ghost 1.gif"));
		image1s.put(ORANGE, RESOURCE_LOADER.loadImage("enemy/"+ORANGE+" ghost 1.gif"));
		image1s.put(PINK, RESOURCE_LOADER.loadImage("enemy/"+PINK+" ghost 1.gif"));
		image1s.put(PURPLE, RESOURCE_LOADER.loadImage("enemy/"+PURPLE+" ghost 1.gif"));
		image1s.put(RED, RESOURCE_LOADER.loadImage("enemy/"+RED+" ghost 1.gif"));
		image1s.put(WHITE, RESOURCE_LOADER.loadImage("enemy/"+WHITE+" ghost 1.gif"));
		image1s.put(YELLOW, RESOURCE_LOADER.loadImage("enemy/"+YELLOW+" ghost 1.gif"));
		image1s.put(RUNNING, RESOURCE_LOADER.loadImage("enemy/"+RUNNING+" ghost 1.gif"));
	}

	private void loadImage2s(){
		image2s = new HashMap<String, BufferedImage>(IMAGES_PER_MAP, LOAD_FACTOR);

		image2s.put(CYAN, RESOURCE_LOADER.loadImage("enemy/"+CYAN+" ghost 2.gif"));
		image2s.put(GREEN, RESOURCE_LOADER.loadImage("enemy/"+GREEN+" ghost 2.gif"));
		image2s.put(ORANGE, RESOURCE_LOADER.loadImage("enemy/"+ORANGE+" ghost 2.gif"));
		image2s.put(PINK, RESOURCE_LOADER.loadImage("enemy/"+PINK+" ghost 2.gif"));
		image2s.put(PURPLE, RESOURCE_LOADER.loadImage("enemy/"+PURPLE+" ghost 2.gif"));
		image2s.put(RED, RESOURCE_LOADER.loadImage("enemy/"+RED+" ghost 2.gif"));
		image2s.put(WHITE, RESOURCE_LOADER.loadImage("enemy/"+WHITE+" ghost 1.gif"));
		image2s.put(YELLOW, RESOURCE_LOADER.loadImage("enemy/"+YELLOW+" ghost 2.gif"));
		image2s.put(RUNNING, RESOURCE_LOADER.loadImage("enemy/"+RUNNING+" ghost 2.gif"));
	}
}
