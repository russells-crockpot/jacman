/**
 * 
 */
package com.gnosis.jacman.engine;

import java.awt.Color;



/**
 * While Java doesn't have a Global scope, in the case of a video game,
 * this is a major hindrance, especially when different objects are in
 * different objects, or when the one method may or may not change the
 * value given. In the end, this creates a great deal of overhead 
 * (which, in a video game, is unacceptable). To overcome this, this
 * class acts like a global scope. It is filled with static variables 
 * that can be changed by the user, thus reducing the overhead, and
 * simplifying code writing.
 * 
 * @author Brendan McGloin
 */
public abstract class Globals implements Constants{
	
	public static KeyBindings keyBindings = null;
	
	/**
	 * How far ahead the ANN looks when training
	 */
	public static int lookAhead = 0;
	
	/**
	 * If autosave (saves the ANN, not the score or player position) is
	 * on, this is true
	 */
	public static boolean autosave = true;
	
	/**
	 * The current game folder's path
	 */
	public static String currentGamePath = null;
	
	/**
	 * The current AI mode
	 */
	public static byte aiMode = FSA_AI_MODE;
	
	public static Color backgroundColor = Color.BLACK;
	public static Color wallColor = Color.WHITE;
	public static Color penColor = Color.GREEN;
	public static Color enemyDoorColor = Color.RED;
	public static Color playerDoorColor = Color.CYAN;
	public static Color blipColor = Color.WHITE;
	
	/**
	 * The current state of the finite state machine.
	 */
	public static byte state = ENEMY_HUNTER_STATE;
	
	/**
	 * How many milliseconds between each frame refresh. Gets smaller with each
	 * level.
	 */
	public static int speed = 100;
	
	/**
	 * How many blips are left on the board
	 */
	public static int blipsLeft;
	
	/**
	 * How much time is left until the ghost go from being the hunted to the
	 * hunters.
	 */
	public static long timeUntilLeaveingHuntedState = TIME_IN_HUNTED_STATE;
	
	/**
	 * The current method of training the artificial neural network
	 */
	public static int trainingMode = TRAINING_MODE_OFF;
	
	/**
	 * The current game (board, actors, ANN, et cetera)
	 */
	public static Game game = null;
	
	public static int timesPowerBlipWasEaten = 0;
	public static int timesEnemyWasEaten = 0;
	public static int timesPlayerWasEaten = 0;
	public static int timesEnemyRegenerated = 0;
}
