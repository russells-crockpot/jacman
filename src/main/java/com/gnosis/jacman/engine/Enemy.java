package com.gnosis.jacman.engine;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.gnosis.jacman.ann.engine.NerveCluster;
import com.gnosis.jacman.ann.engine.Neuron;

public class Enemy implements Serializable, Constants{
	
	private static final long serialVersionUID = 0xf5863211;
	
	
	protected transient Point center;
	protected transient int direction;
	protected transient boolean useImage1;
	private transient int changeImageBuffer;
	private transient int timesMovedInDirection;
	private transient boolean stopMoving = false;
	
	/**
	 * This is the chance that this enemy will go towards the goal.
	 * It's expressed ad an in integer between 0 and the MAX_RANDOM
	 * field defined in Globals
	 */
	private int chanceOfGoingTowardsGoal;
	
	private transient boolean alive;
	
	private String color;
	
	private int startingRow, startingCol;
	
	private transient boolean wasInCorridor = false;
	
	private NerveCluster outCluster, inCluster;
	
	public Enemy(){
		wasInCorridor = false;
		alive = true;
		int x = (startingCol * TILE_WIDTH) + (TILE_WIDTH/2);
		int y = (startingRow * TILE_HEIGHT) + (TILE_HEIGHT/2);
		center = new Point(x, y);
		this.timesMovedInDirection = 0;
	}
	
	/**
	 * @param chanceOfGoingTowardsGoal
	 * @param color
	 * @param startingRow
	 * @param startingCol
	 */
	public Enemy(int chanceOfGoingTowardsGoal, String color, int startingRow,
			int startingCol) {
		super();
		this.chanceOfGoingTowardsGoal = chanceOfGoingTowardsGoal;
		this.color = color;
		this.startingRow = startingRow;
		this.startingCol = startingCol;
		alive = true;
		int x = (startingCol * TILE_WIDTH) + (TILE_WIDTH/2);
		int y = (startingRow * TILE_HEIGHT) + (TILE_HEIGHT/2);
		center = new Point(x, y);
		outCluster = null;
		inCluster = null;
		this.timesMovedInDirection = 0;
	}
	
	public void reset(){
		alive = true;
		int x = (startingCol * TILE_WIDTH) + (TILE_WIDTH/2);
		int y = (startingRow * TILE_HEIGHT) + (TILE_HEIGHT/2);
		center = new Point(x, y);
		this.timesMovedInDirection = 0;
	}
	
	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}
	
	public void moveAsPlayer(int moveOptions, int goal) {
		//check to see if you can move the way you want the player to
		if ((goal&moveOptions)> 0){
			direction = goal;
		}
		//if not, we check to see if moving forward is ok
		else if ((direction&moveOptions) <= 0){
			//check to see if were at a corner
			if (moveOptions == (NORTH|EAST)){
				if (direction == NORTH){
					direction = EAST;
				}
				else if (direction == EAST){
					direction = NORTH;
				}
			}
			//NW
			else if (moveOptions == (NORTH|WEST)){
				if (direction == NORTH){
					direction = WEST;
				}
				else if (direction == WEST){
					direction = NORTH;
				}
			}
			//SW
			else if (moveOptions == (SOUTH|WEST)){
				if (direction == SOUTH){
					direction = WEST;
				}
				else if (direction == WEST){
					direction = SOUTH;
				}
			}
			//SE
			else if (moveOptions == (SOUTH|EAST)){
				if (direction == SOUTH){
					direction = EAST;
				}
				else if (direction == EAST){
					direction = SOUTH;
				}
			}
			int options = moveOptions;
			//make sure you don't go backwards
			switch (direction){
			case NORTH: options &=(~SOUTH); break;
			case SOUTH: options &=(~NORTH); break;
			case EAST: options &=(~WEST); break;
			case WEST: options &=(~EAST); break;
			}
			direction = chooseRandomDirection(options);
		}
		moveForward();
	}


	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		//regenerating
		if (alive&&(!this.alive)){
			Globals.game.enemyRegenerated(this);
		}
		//dying
		else if (this.alive&&(!alive)){
			Globals.game.enemyKilled(this);
		}
		this.alive = alive;
	}

	protected void moveForward(){
		switch (direction){
		case NORTH: center.y = center.y - Globals.MOVEMENT_RATE; break;
		case SOUTH: center.y = center.y + Globals.MOVEMENT_RATE; break;
		case EAST: center.x = center.x + Globals.MOVEMENT_RATE; break;
		case WEST: center.x = center.x - Globals.MOVEMENT_RATE; break;
		}
		changeImageBuffer++;
		if (changeImageBuffer > Globals.MOVES_TO_SWITCH_IMAGE){
			changeImageBuffer = 0;
			if (useImage1){
				useImage1 = false;
			}
			else{
				useImage1 = true;
			}
		}
	}
	
	protected static int chooseRandomDirection(int options){
		List<Integer> choices = new ArrayList<Integer>(4);
		if ((options&NORTH) > 0){
			choices.add(NORTH);
		}
		if ((options&SOUTH) > 0){
			choices.add(SOUTH);
		}
		if ((options&EAST) > 0){
			choices.add(EAST);
		}
		if ((options&WEST) > 0){
			choices.add(WEST);
		}
		if (choices.isEmpty()){
			//this should never happen, but just in case
			return 0;
		}
		return choices.get(Globals.RNG.nextInt(choices.size()));
	}
	
	/**
	 * @return the center
	 */
	public Point getCenter() {
		return center;
	}

	/**
	 * @param center the center to set
	 */
	public void setCenter(Point center) {
		this.center = center;
	}

	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	
	
	public void move(int moveOptions, Point goal, boolean inRegenPen){
		if (stopMoving){
			return;
		}
		//Globals.LOGGER.info(Tester.printDirections(moveOptions));
		if (Globals.aiMode == Globals.FSA_AI_MODE){
			//first we need to get our prime choices
			int[] choices = this.getBestDirection(goal);
			
			//if we're in the regen pen, then we just want to move straight towards the point
			if (inRegenPen){
				direction = choices[0];
				moveForward();
				return;
			}
			//now we want to check if running away
			if ((Globals.state == ENEMY_HUNTED_STATE) && (alive)){
				//we are, so reverse the order of the choices
				int[] temp = choices.clone();
				choices[0] = temp[3];
				choices[1] = temp[2];
				choices[2] = temp[1];
				choices[3] = temp[0];
			}
			//from here on out, we can proceed as normal. First we need to check if we're
			//at a junction. A junction is anything other then a straight corridor
			
			if (moveOptions == (NORTH|SOUTH)){
				if ((direction != NORTH) && (direction != SOUTH)){
					//This should never happen
				}
				wasInCorridor = true;
				moveForward();
				return;
			}
			else if (moveOptions == (EAST|WEST)){
				if ((direction != EAST) && (direction != WEST)){
					//This should never happen
				}
				wasInCorridor = true;
				moveForward();
				return;
			}
			if (wasInCorridor){
				wasInCorridor = false;
				int chance = chanceOfGoingTowardsGoal + ((alive)? 0: (chanceOfGoingTowardsGoal/3));
				//We are at a junction, so now we must decide whether or not we should chase
				if (chance > Globals.RNG.nextInt(Globals.ENEMY_CHASE_CHANCE_MAX)){
					//we're going towards our goal, so now we need to go through the choices array
					//and check if we can move that direction
					for (int choice: choices){
						if ((choice&moveOptions) > 0){
							direction = choice;
							moveForward();
							return;
						}
					}
					//This should never happen
				}
				else{
					//we decide to take a random direction
					direction = chooseRandomDirection(moveOptions);
					moveForward();
					return;
				}
			}
			else{
				moveForward();
			}
		}
		else if (Globals.aiMode == Globals.ANN_AI_MODE){
			if (inRegenPen){
				int[] choices = this.getBestDirection(goal);
				direction = choices[0];
				moveForward();
				return;
			}
			
			if ((moveOptions == (NORTH|SOUTH))||(moveOptions == (EAST|WEST))){
				wasInCorridor = true;
			}
			else if (wasInCorridor){
				wasInCorridor = false;
				int highestDirection = 0;
				double highestDirectionValue = -1;
				for (int i = 0; i < outCluster.getCluster().length; i++){
					int t = 0;
					switch (i){
					case 0: t = NORTH; break;
					case 1: t = SOUTH; break;
					case 2: t = EAST; break;
					case 3: t = WEST; break;
					default: System.err.println("Iteration in move in enemy went too high."); //consider exception
					}
					if ((outCluster.getCluster()[i].getActivation() > highestDirectionValue)
							&& ((t&moveOptions) > 0)){
						highestDirection = t;
						highestDirectionValue = outCluster.getCluster()[i].getActivation();
					}
				}
				direction = highestDirection;
			}
			moveForward();
		}
	}
	
	public void setInputValues(Board board){
		Neuron[] cluster = inCluster.getCluster();
		int c = 0;
		cluster[c].setActivation((alive)? 1:0); c++;
		int moveOptions = board.getMoveOptions(this);
		cluster[c].setActivation(((moveOptions&Globals.NORTH) > 0)? 1:0); c++;
		cluster[c].setActivation(((moveOptions&Globals.SOUTH) > 0)? 1:0); c++;
		cluster[c].setActivation(((moveOptions&Globals.EAST) > 0)? 1:0); c++;
		cluster[c].setActivation(((moveOptions&Globals.WEST) > 0)? 1:0); c++;
		int row = center.y/Globals.TILE_SIZE.height;
		int col = center.x/Globals.TILE_SIZE.width;
		for (int i = 0; i < board.getRows(); i++, c++){
			cluster[c].setActivation((i == row)? 1:0);
		}
		for (int i = 0; i < board.getColumns(); i++, c++){
			cluster[c].setActivation((i == col)? 1:0);
		}
	}
	
	public int[] getBestDirection(Point point){
		
		/* negative x = east
		 * positive x = west
		 * negative y = south
		 * positive y = north
		 */
		int[] choices = new int[4];
		
		
		int x = center.x - point.x;
		int y = center.y - point.y;
		//first check to see if x is equal zero
		if (x == 0){
			if (y < 0){
				choices[0] = SOUTH;
				choices[2] = WEST;
				choices[3] = EAST;
				choices[1] = NORTH;
			}
			else{
				choices[0] = NORTH;
				choices[2] = EAST;
				choices[3] = WEST;
				choices[1] = SOUTH;
			}
			return choices;
		}
		else if (y == 0){
			if (x < 0){
				choices[0] = EAST;
				choices[2] = NORTH;
				choices[3] = SOUTH;
				choices[1] = WEST;
			}
			else{
				choices[0] = WEST;
				choices[2] = SOUTH;
				choices[3] = NORTH;
				choices[1] = EAST;
			}
			return choices;
		}
		//if the point is to the east
		if (x < 0){
			//if the point is down
			if (y < 0){
				//since both are negative, we check to see which is smaller
				if (x < y){
					//x is smaller, but since both are negative, we want to go with y
					choices[0] = SOUTH;
				}
				else {
					choices[0] = EAST;
				}
			}
			//if the point is up
			else {
				//check to see which is smaller, however, since x is negative, we need to take it's abs val first
				if (Math.abs(x) > y){
					choices[0] = SOUTH;
				}
				else{
					choices[0] = EAST;
				}
			}
		}
		//if the point is to the west
		else {
			//if the point is down
			if (y < 0){
				//since Y is negative, we need to see if it's abs val is larger than x
				if(Math.abs(y) > x){
					//it is, so our first choice is to go south
					choices[0] = SOUTH;
				}
				else {
					choices[0] = WEST;
				}
			}
			//if the point is up
			else {
				if (y > x){
					choices[0] = NORTH;
				}
				else{
					choices[0] = WEST;
				}
			}
		}
		
		//if the first option, then the next choice would be the x
		if ((choices[0] == NORTH) || (choices[0] == SOUTH)){
			if (x < 0){
				choices[1] = EAST;
				choices[2] = WEST;
			}
			else {
				choices[1] = WEST;
				choices[2] = EAST;
			}
		}
		else {
			if (y < 0){
				choices[1] = SOUTH;
				choices[2] = NORTH;
			}
			else {
				choices[1] = NORTH;
				choices[2] = SOUTH;
			}
		}
		
		//now add the final item
		switch (choices[0]){
		case NORTH: choices[3] = SOUTH; break;
		case SOUTH: choices[3] = NORTH; break;
		case EAST: choices[3] = WEST; break;
		case WEST: choices[3] = EAST; break;
		}
		
		return choices;
	}

	/**
	 * @return the startingRow
	 */
	public int getStartingRow() {
		return startingRow;
	}



	/**
	 * @param startingRow the startingRow to set
	 */
	public void setStartingRow(int startingRow) {
		this.startingRow = startingRow;
	}



	/**
	 * @return the startingCol
	 */
	public int getStartingCol() {
		return startingCol;
	}



	/**
	 * @param startingCol the startingCol to set
	 */
	public void setStartingCol(int startingCol) {
		this.startingCol = startingCol;
	}
	
	
	public Point getTilePlace(){
		int x = center.x % TILE_WIDTH;
		int y = center.y % TILE_HEIGHT;
		return new Point(x, y);
	}

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		
		if (Globals.state == ENEMY_HUNTER_STATE){
			return ENEMY_IMAGE_CACHE.getImage(color, useImage1);
		}
		else{
			return ENEMY_IMAGE_CACHE.getImage(RUNNING, useImage1);
		}
	}
	
	public Rectangle getRectangle(){
		return new Rectangle(center.x-8, center.y-8, 14, 14);
	}

	public NerveCluster getOutCluster() {
		return outCluster;
	}

	public void setOutCluster(NerveCluster outCluster) {
		this.outCluster = outCluster;
	}

	public NerveCluster getInCluster() {
		return inCluster;
	}

	public void setInCluster(NerveCluster inCluster) {
		this.inCluster = inCluster;
	}

	public int getChanceOfGoingTowardsGoal() {
		return chanceOfGoingTowardsGoal;
	}
	
	/**
	 * @return the timesMovedInDirection
	 */
	public int getTimesMovedInDirection() {
		return timesMovedInDirection;
	}

	/**
	 * @param timesMovedInDirection the timesMovedInDirection to set
	 */
	public void setTimesMovedInDirection(int timesMovedInDirection) {
		this.timesMovedInDirection = timesMovedInDirection;
	}

	public boolean isStopMoving() {
		return stopMoving;
	}

	public void setStopMoving(boolean stopMoving) {
		this.stopMoving = stopMoving;
	}
}
