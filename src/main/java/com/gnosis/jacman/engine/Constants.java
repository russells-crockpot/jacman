/**
 *
 */
package com.gnosis.jacman.engine;

import com.gnosis.jacman.graphics.EnemyImageCache;
import com.gnosis.jacman.graphics.OtherImageCache;
import com.gnosis.jacman.graphics.PlayerImageCache;
import com.gnosis.jacman.graphics.TileImageCache;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Random;

import com.gnosis.jacman.resources.ResourceLoader;

/**
 * This interfaces contains all of the commonly used constants in the game.
 * Instead of having to give a fully qualified name for every call, a class
 * can just implement this interface.
 *
 * @author Brendan McGloin
 */
public interface Constants {

    /**
     * A convenience constant for dealing with the file
     * system.
     */
    String SEPARATOR = System.getProperty("file.separator");

    /**
     * The game's directory
     */
    String WORKING_PATH = new File(System.getProperty("java.class.path")).getParent()+SEPARATOR;

    /**
     * The path to the error log
     */
    String ERROR_LOG_PATH = WORKING_PATH+"error_log.txt";

    /**
     * The key that needs to be pressed in order to select an item from a menu bar (apple button v. ctrl)
     */
    int MENU_SELECTOR_MASK = (System.getProperty("os.name").contains("Mac"))? ActionEvent.META_MASK: ActionEvent.CTRL_MASK;

    /**
     * Used to load resources such as images, sounds (of which there are currently none), et cetera.
     */
    ResourceLoader RESOURCE_LOADER = new ResourceLoader();

    /**
     * How many input neurons there are for each actor, excluding row
     * and column neurons.
     */
    int INPUT_NEURONS_PER_ACTOR = 5;

    //These are the constants for direction. Each one is actually a mask, thus NORTH|WEST is actually North-west.
    //tiles such as FILLED are also a mask.

    /**
     * The value for North
     */
    int NORTH = 0x01;
    /**
     * The value for South
     */
    int SOUTH = 0x02;
    /**
     * The value for East
     */
    int EAST = 0x04;
    /**
     * The value for West
     */
    int WEST = 0x08;
    /**
     * The value for a filled tile (no corridors or blips)
     */
    int FILLED = 0x00;
    /**
     * The value for an open tile. Should only be
     * used in the level editor and for regen tiles
     */
    int OPEN = 0x10;
    /**
     * The value for an empty tile.
     */
    int EMPTY = 0x20;

    /**
     * All of the directions in a handy-dandy array
     */
    int[] DIRECTIONS = {NORTH, SOUTH, EAST, WEST};

    /**
     * The games random number generator
     */
    Random RNG = new Random();

    /**
     * The max value generated by the pRNG for the chance
     * that an enemy will pursue a player when in FSA mode.
     */
    int ENEMY_CHASE_CHANCE_MAX = 10000;

    /**
     * The value for FSA AI mode
     */
    byte FSA_AI_MODE = 0;
    /**
     * The value for ANN AI mode
     */
    byte ANN_AI_MODE = 1;

    //Graphic related constants

    /**
     * The width of a single tile
     */
    int TILE_WIDTH = 40;
    /**
     * The height of a single tile
     */
    int TILE_HEIGHT = 40;

    /**
     * The width of the lives icon on the bottom bar
     */
    int LIVES_ICON_WIDTH = 9;

    /**
     * The height of the lives icon on the bottom bar
     */
    int LIVES_ICON_HEIGHT = 9;

    /**
     * The size of a corridor.
     */
    int CORRIDOR_SIZE = 20;

    /**
     * How much room the player has to move around without being in the EXACT center
     */
    int WIGGLE_ROOM = 4;

    /**
     * The size (width or height) of a wall
     */
    int WALL_SIZE = 10;

    /**
     * The center of a tile, where you would place an item (Currently only a power blip),
     * or a normal blip in the center.
     */
    Point ITEM_CENTER = new Point(TILE_WIDTH/2, TILE_HEIGHT/2);

    /**
     * The center of the North blip
     */
    Point NORTH_BLIP_CENTER = new Point(TILE_WIDTH/2, 2 +WALL_SIZE/2);

    /**
     * The center of the South blip
     */
    Point SOUTH_BLIP_CENTER = new Point(TILE_WIDTH/2, TILE_HEIGHT - (WALL_SIZE/2));

    /**
     * The center of the East blip
     */
    Point EAST_BLIP_CENTER = new Point(TILE_WIDTH - (WALL_SIZE/2), TILE_HEIGHT/2);

    /**
     * The center of the West blip
     */
    Point WEST_BLIP_CENTER = new Point(2 + WALL_SIZE/2, TILE_HEIGHT/2);

    /**
     * How many pixels something moves each step.
     */
    byte MOVEMENT_RATE = 4;

    /**
     * How tall the player image is.
     */
    int PLAYER_HEIGHT = 16;
    /**
     * How wide the player image is
     */
    int PLAYER_WIDTH = 16;

    /**
     * How tall an item image is
     */
    int ITEM_HEIGHT = 14;
    /**
     * How wide an item's image is
     */
    int ITEM_WIDTH = 14;

    /**
     * How wide and tall the image for an enemy is
     */
    int ENEMY_SIZE = 16;

    /**
     * How many moves it takes to switch from image1 to image2
     */
    int MOVES_TO_SWITCH_IMAGE = 4;

    /**
     * Bottom bar height
     */
    int BOTTOM_BAR_HEIGHT = 20;

    /**
     * The diameter, in pixels, of a power blip
     */
    int POWER_BLIP_DIAMETER = 8;

    /**
     * The radius of the JacMan Image
     */
    int PLAYER_RADIUS = 1 + PLAYER_HEIGHT/2;

    /**
     * The dimensions of a tile.
     */
    public static final Dimension TILE_SIZE = new Dimension(TILE_WIDTH, TILE_HEIGHT);

    /**
     * The dimensions of the player
     */
    public static final Dimension PLAYER_SIZE = new Dimension(PLAYER_WIDTH, PLAYER_HEIGHT);

    //States of the FSA

    /**
     * The state value when the enemy is the hunter
     */
    byte ENEMY_HUNTER_STATE = 0;

    /**
     * The state value when the enemy is the hunted
     */
    byte ENEMY_HUNTED_STATE = 1;

    //Points and other game related variables

    /**
     * Points gained whenever a normal blip is eaten
     */
    int POINTS_FOR_BLIP = 50;
    /**
     * The number of points it takes to gain a new life
     */
    int POINTS_TO_GET_AN_EXTRA_LIFE = 100000;

    /**
     * The maximum number of lives a player can have
     */
    byte MAX_LIVES = 99;
    /**
     * The number of lives that a player starts with
     */
    byte STARTING_LIVES = 3;

    /**
     * How many points a player gets when they kill an enemy
     */
    int POINTS_FOR_KILLING_ENEMY = 3000;

    /**
     * How long a player will stay in a hunted state
     */
    long TIME_IN_HUNTED_STATE = 3000;

    /**
     * The learning rate for a large event
     */
    double BIG_LR = 0.35;

    /**
     * The learning rate for a small event
     */
    double LITTLE_LR = 0.075;

    /**
     * Value when ANN training is off
     */
    int TRAINING_MODE_OFF = 0;

    /**
     * Value when the ANN gets trained after the player is done with the game
     */
    int TRAINING_MODE_AFTER = 1;

    /**
     * Value when the ANN is trained as the player plays the game
     */
    int TRAINING_MODE_AS_YOU_GO = 2;


    /**
     * The maximum blips that can be in a row
     */
    byte MAX_BLIPS_IN_ROW = 3;
    /**
     * The maximum blips that can be in a column
     */
    byte MAX_BLIPS_IN_COLUMN = 3;

    /**
     * The default color for an enemy
     */
    Color DEFAULT_ENEMY_COLOR = Color.MAGENTA;

    /**
     * The number of output neurons for each enemy
     */
    int NEURONS_IN_OUT_CLUSTER = 4;

    /**
     * The diameter, in pixels, of a normal blip
     */
    int BLIP_DIAMETER = 3;


    /**
     * How many moves it takes until
     */
    int MOVES_UNTIL_NEW_TILE = TILE_WIDTH/MOVEMENT_RATE;

    //A good event means good for the enemy, not the player. Like the directional
    //variables, they are technically masks

    /**
     * Value that indicates a bad event for the enemy
     */
    int BAD_EVENT = 1;
    /**
     * Value that indicates a good event for the enemy
     */
    int GOOD_EVENT = 2;
    /**
     * Value that indicates a smaller learning rate should be used
     */
    int LITTLE_LR_EVENT = 4;
    /**
     * Value that indicates a larger learning rate should be used
     */
    int BIG_LR_EVENT = 8;

    double GOOD_EVENT_LR_MODIFIER = 1.5;
    double BAD_EVENT_LR_MODIFIER = 0.7;

    int PLAYER_KILLED_EVENT = 0x10;
    int ENEMY_REGENERATED_EVENT = 0x20;
    int ENEMY_KILLED_EVENT = 0x40;
    int POWER_BLIP_EATEN_EVENT = 0x80;

    double ENEMY_EATEN_EVENT_MOD = 0.590909090909;
    double POWER_BLIP_EATEN_EVENT_MOD = 0.19298246;

    double FIRST = 1.0;
    double SECOND = 0.75;
    double THIRD = 0.25;
    double LAST = 0.0;

    /**
     * The modification made to the speed whenever a player beats a level
     */
    double SPEED_MOD = .875;

    /**
     * The prefix for a tile image in the image cache
     */
    String TILE_PREFIX = "Tile ";
    /**
     * The prefix for a player image in the image cache
     */
    String PLAYER_PREFIX ="JacMan ";
    /**
     * The prefix for a item image in the image cache
     */
    String ITEM_PERFIX ="Item ";
    /**
     * The prefix for a other image in the image cache
     */
    String OTHER_PREFIX ="Other ";
    /**
     * The prefix for an enemy image in the image cache
     */
    String ENEMY_PREFIX ="Enemy ";
    /**
     * The prefix for an arrow image in the image cache
     */
    String ARROW_PREFIX ="Arrow ";

    /**
     * Value for the color Cyan
     */
    String CYAN = "Cyan";
    /**
     * Value for the color Green
     */
    String GREEN = "Green";
    /**
     * Value for the color Orange
     */
    String ORANGE = "Orange";
    /**
     * Value for the color Pink
     */
    String PINK = "Pink";
    /**
     * Value for the color Purple
     */
    String PURPLE = "Purple";
    /**
     * Value for the color Red
     */
    String RED = "Red";
    /**
     * Value for the color White
     */
    String WHITE = "White";
    /**
     * Value for the color Yellow
     */
    String YELLOW = "Yellow";

    String RUNNING = "Running";

    //Different image caches loaded at runtime to allow faster execution

    EnemyImageCache ENEMY_IMAGE_CACHE = new EnemyImageCache();

    TileImageCache TILE_IMAGE_CACHE = new TileImageCache();

    PlayerImageCache PLAYER_IMAGE_CACHE = new PlayerImageCache();

    OtherImageCache OTHER_IMAGE_CACHE = new OtherImageCache();
}
