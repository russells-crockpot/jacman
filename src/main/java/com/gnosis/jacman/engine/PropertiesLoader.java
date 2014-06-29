/**
 * 
 */
package com.gnosis.jacman.engine;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Properties;

/**
 * This loads and saves all of different properties and
 * preferences that the user had from the last time they
 * played the game, such as the training mode, autosave and
 * the game path.
 * 
 * @author Brendan McGloin
 */
public final class PropertiesLoader implements Constants{
	
	private static final String PROPERTIES_PATH = WORKING_PATH+"props.jmp";
	private static final String DEFAULT_GAME_PATH = Preloads.GAME_1_PATH;
	private static final byte DEFAULT_TRAINING_MODE = TRAINING_MODE_OFF;
	private static final boolean DEFAULT_AUTOSAVE = true;
	private static final int DEFAULT_LOOK_AHEAD = 2;
	
	private static final String GAME_PATH_PROP = "previous.game.path";
	private static final String TRAINING_MODE_PROP = "training.mode";
	private static final String TRAINING_LOOK_AHEAD_PROP = "training.lookahead";
	private static final String AUTOSAVE_PROP = "autosave";
	
	private static final String KB_UP = "key.binding.up";
	private static final String KB_DOWN = "key.binding.down";
	private static final String KB_RIGHT = "key.binding.right";
	private static final String KB_LEFT = "key.binding.left";
	private static final String KB_PAUSE = "key.binding.pause";
	
	public static void load(){
		try{
			Properties props = new Properties();
			FileInputStream stream = new FileInputStream(new File(PROPERTIES_PATH));
			props.load(stream);
			if (props.getProperty(TRAINING_MODE_PROP).equals("off")){
				Globals.trainingMode = TRAINING_MODE_OFF;
			}
			else if (props.getProperty(TRAINING_MODE_PROP).equals("during")){
				Globals.trainingMode = TRAINING_MODE_AS_YOU_GO;
			}
			else if (props.getProperty(TRAINING_MODE_PROP).equals("after")){
				Globals.trainingMode = TRAINING_MODE_AFTER;
			}
			else{
				Globals.trainingMode = DEFAULT_TRAINING_MODE;
			}
			if (props.containsKey(AUTOSAVE_PROP)){
				if(props.getProperty(AUTOSAVE_PROP).equals("on")){
					Globals.autosave = true;
				}
				else {
					Globals.autosave = false;
				}
			}
			else {
				Globals.autosave = DEFAULT_AUTOSAVE;
			}
			if (props.containsKey(TRAINING_LOOK_AHEAD_PROP)){
				Globals.lookAhead = Integer.parseInt(props.getProperty(TRAINING_LOOK_AHEAD_PROP));
			}
			else {
				Globals.lookAhead = DEFAULT_LOOK_AHEAD;
			}
			
			Globals.currentGamePath = props.getProperty(GAME_PATH_PROP);
			
			int up, down, right, left, pause;
			if(props.contains(KB_UP)){
				up = Integer.parseInt(props.getProperty(KB_UP));
			}
			else{
				up = KeyEvent.VK_UP;
			}
			if (props.contains(KB_DOWN)){
				down = Integer.parseInt(props.getProperty(KB_DOWN));
			}
			else{
				down = KeyEvent.VK_DOWN;
			}
			if(props.contains(KB_RIGHT)){
				right = Integer.parseInt(props.getProperty(KB_RIGHT));
			}
			else{
				right = KeyEvent.VK_RIGHT;
			}
			if(props.contains(KB_LEFT)){
				left = Integer.parseInt(props.getProperty(KB_LEFT));
			}
			else{
				left = KeyEvent.VK_LEFT;
			}
			if(props.contains(KB_PAUSE)){
				pause = Integer.parseInt(props.getProperty(KB_PAUSE));
			}
			else{
				pause = KeyEvent.VK_SPACE;
			}
			Globals.keyBindings = new KeyBindings(up, down, right, left, pause);
		} catch (IOException e){
			Globals.trainingMode = DEFAULT_TRAINING_MODE;
			Globals.currentGamePath = DEFAULT_GAME_PATH;
			Globals.autosave = DEFAULT_AUTOSAVE;
			Globals.lookAhead = DEFAULT_LOOK_AHEAD;
		}
	}
	
	public static void save(){
		try{
			Properties props = new Properties();
			props.setProperty(GAME_PATH_PROP, Globals.currentGamePath);
			String mode = "";
			switch (Globals.trainingMode){
			case TRAINING_MODE_OFF: mode = "off"; break;
			case TRAINING_MODE_AFTER: mode = "after"; break;
			case TRAINING_MODE_AS_YOU_GO: mode = "during"; break;
			}
			props.setProperty(TRAINING_MODE_PROP, mode);
			props.setProperty(AUTOSAVE_PROP, (Globals.autosave)? "on":"off");
			props.setProperty(TRAINING_LOOK_AHEAD_PROP, Integer.toString(Globals.lookAhead));
			
			props.setProperty(KB_UP, Integer.toString(Globals.keyBindings.getUp()));
			props.setProperty(KB_DOWN, Integer.toString(Globals.keyBindings.getDown()));
			props.setProperty(KB_RIGHT, Integer.toString(Globals.keyBindings.getRight()));
			props.setProperty(KB_LEFT, Integer.toString(Globals.keyBindings.getLeft()));
			props.setProperty(KB_PAUSE, Integer.toString(Globals.keyBindings.getPause()));
			
			FileOutputStream stream = new FileOutputStream(new File(PROPERTIES_PATH));
			props.store(stream, "Properties for JacMan");
			stream.close();
		} catch (IOException e){
			//TODO LOGGER.log(WARNING, "In PropertiesLoader.save():" +e.getMessage());
		}
	}
}
