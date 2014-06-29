/**
 * 
 */
package com.gnosis.jacman.engine;

import java.awt.Point;
import java.io.Serializable;
import com.gnosis.jacman.engine.items.NoItem;
import com.gnosis.jacman.engine.items.PowerBlip;

/**
 * This represents a single tile on the board. Tiles can be junctions,
 * corridors, et cetera. It also contains information and methods
 * relating to how enemies and players can move within the tile.
 * 
 * @author Brendan McGloin
 */
public final class Tile implements Serializable, Constants{
	
	private static final long serialVersionUID = 0xf5863213;
	public static final int BLIP_RANGE = 3;
	
	private int enemyMoveOptions, playerMoveOptions;
	private transient Item item;
	private Item initialItem;
	private transient BlipCluster cluster;
	private BlipCluster initialCluster;
	private boolean regen = false;
	private int regenDoor;
	
	

	/**
	 * @param enemyMoveOptions
	 * @param playerMoveOptions
	 */
	public Tile(int enemyMoveOptions, int playerMoveOptions, Item item) {
		super();
		this.enemyMoveOptions = enemyMoveOptions;
		this.playerMoveOptions = playerMoveOptions;
		if (item == null){
			this.item = new NoItem();
		}
		else{
			this.item = item;
		}
		
		this.initialItem = this.item;
		cluster = new BlipCluster();
		makeBlips();
		regenDoor = 0;
	}
	
	/**
	 * @param enemyMoveOptions
	 * @param playerMoveOptions
	 * @param item
	 * @param regen
	 */
	public Tile(int enemyMoveOptions, int playerMoveOptions, Item item,
			boolean regen) {
		super();
		this.enemyMoveOptions = enemyMoveOptions;
		this.playerMoveOptions = playerMoveOptions;
		if (item == null){
			this.item = new NoItem();
		}
		else{
			this.item = item;
		}
		
		this.initialItem = this.item;
		this.regen = regen;
		cluster = new BlipCluster();
		makeBlips();
		regenDoor = 0;
	}

	/**
	 * @param enemyMoveOptions
	 * @param playerMoveOptions
	 */
	public Tile(int enemyMoveOptions, int playerMoveOptions) {
		super();
		this.enemyMoveOptions = enemyMoveOptions;
		this.playerMoveOptions = playerMoveOptions;
		this.item = new NoItem();
		this.initialItem = this.item;
		cluster = new BlipCluster();
		regenDoor = 0;
		makeBlips();
	}
	
	public Tile(int moveOptions){
		this(moveOptions, moveOptions);
	}
	
	public Tile(int moveOptions, Item item){
		this(moveOptions, moveOptions, item);
	}
	
	public void reset(){
		makeBlips();
		this.item = this.initialItem;
		if (item instanceof PowerBlip) {
			PowerBlip pb = (PowerBlip) item;
			pb.setExists(true);
		}
	}
	
	public void setMoveOptions(int options){
		this.enemyMoveOptions = options;
		this.playerMoveOptions = options;
	}
	
	public void makeBlips(){
		if (regen){
			return;
		}
		else if (playerMoveOptions == FILLED){
			return;
		}
		if (item == null){
			item = initialItem;
		}
		if (initialCluster == null){
			createInitialBlips();
		}
		cluster = initialCluster.clone();
	}
	
	public void createInitialBlips(){
		if (regen){
			return;
		}
		else if (playerMoveOptions == FILLED){
			return;
		}
		initialCluster = new BlipCluster();
		int walls = enemyMoveOptions&playerMoveOptions;
		//first check to see if this tile has an item
		if (item instanceof NoItem){
			//it doesn't, so we make a center blip
			initialCluster.addBlip(1, 1);
		}
		//now go through each side to see if a blip is needed
		if ((walls&NORTH) > 0){
			initialCluster.addBlip(0, 1);
		}
		if ((walls&SOUTH) > 0){
			initialCluster.addBlip(2, 1);
		}
		if ((walls&EAST) > 0){
			initialCluster.addBlip(1, 2);
		}
		if ((walls&WEST) > 0){
			initialCluster.addBlip(1, 0);
		}
	}
	
	/**
	 * @return the enemyMoveOptions
	 */
	public int getEnemyMoveOptions() {
		return enemyMoveOptions;
	}

	/**
	 * @return the playerMoveOptions
	 */
	public int getPlayerMoveOptions() {
		return playerMoveOptions;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		if (item == null){
			this.item = this.initialItem;
		}
		return this.item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("PMO:\t");
		if ((playerMoveOptions&NORTH) > 0){
			sb.append("N");
		}
		if ((playerMoveOptions&SOUTH) > 0){
			sb.append("S");
		}
		if ((playerMoveOptions&EAST) > 0){
			sb.append("E");
		}
		if ((playerMoveOptions&WEST) > 0){
			sb.append("W");
		}
		sb.append("\nEMO:\t");
		if ((enemyMoveOptions&NORTH) > 0){
			sb.append("N");
		}
		if ((enemyMoveOptions&SOUTH) > 0){
			sb.append("S");
		}
		if ((enemyMoveOptions&EAST) > 0){
			sb.append("E");
		}
		if ((enemyMoveOptions&WEST) > 0){
			sb.append("W");
		}
		sb.append("\nItem:\t");
		if (item instanceof NoItem){
			sb.append("none");
		}
		else {
			sb.append(item.getName());
		}
		
		
		return sb.toString();
	}

	/**
	 * @return the cluster
	 */
	public BlipCluster getCluster() {
		if (cluster == null){
			cluster = new BlipCluster();
			makeBlips();
		}
		return cluster;
	}
	
	public int getEnemyOptions(Enemy enemy){
		Point place = enemy.getTilePlace();
		
		if (regen){
			//check to see if the enemy would be hitting any walls
			if ((place.x >= WALL_SIZE+(CORRIDOR_SIZE/2)-(PLAYER_WIDTH/2))&&(place.x <= WALL_SIZE + (CORRIDOR_SIZE)-(PLAYER_HEIGHT/2))&&
					(place.y >= WALL_SIZE+(CORRIDOR_SIZE/2)-(PLAYER_HEIGHT/2))&&(place.y <= WALL_SIZE + (CORRIDOR_SIZE)-(PLAYER_HEIGHT/2))){
				if (regenDoor > 0){
					if (regenDoor == NORTH){
						if ((place.y <= TILE_HEIGHT - (ENEMY_SIZE/2) + MOVEMENT_RATE)&& //If the enemy is right next to a wall
								(!((place.x >= WALL_SIZE+(ENEMY_SIZE/2))&&
								(place.x <= WALL_SIZE+CORRIDOR_SIZE-(ENEMY_SIZE/2))))){ //And they are not in the center
							return SOUTH|EAST|WEST;
						}
						else {
							return NORTH|SOUTH|EAST|WEST;
						}
					}
				}
				return NORTH|SOUTH|EAST|WEST;
			}
			else {
				return enemyMoveOptions;
			}
		}
		enemy.setTimesMovedInDirection(0);
		//check to see if the person is in the center
		if (((place.x >= 18) && (place.x <= 22)) && ((place.y >= 18) && (place.y <= 22))){

			return enemyMoveOptions;
		}
		//if not, check to see if they're in an up/down corridor
		else if ((place.x >= 18) && (place.x <= 22)){
			int options = 0;
			if ((enemyMoveOptions&NORTH) > 0){
				options |= NORTH;
			}
			if ((enemyMoveOptions&SOUTH) > 0){
				options |= SOUTH;
			}
			
			//make sure they can move forward when not at a junction
			options |= enemy.getDirection();
			return options;
		}
		else if ((place.y >= 18) && (place.y <= 22)){
			int options = 0;
			if ((enemyMoveOptions&EAST) > 0){
				options |= EAST;
			}
			if ((enemyMoveOptions&WEST) > 0){
				options |= WEST;
			}
			
			//make sure they can move forward when not at a junction
			options |= enemy.getDirection();
			return options;
		}
		return 0;
	}
	
	public int getMoveOptions(Player player){
		Point place = player.getTilePlace();
		
		
		//check to see if the person is in the center
		if (((place.x >= WALL_SIZE+(CORRIDOR_SIZE/2)-(WIGGLE_ROOM/2)) && (place.x <= WALL_SIZE+(CORRIDOR_SIZE/2)+(WIGGLE_ROOM/2))) && 
				((place.y >= WALL_SIZE+(CORRIDOR_SIZE/2)-(WIGGLE_ROOM/2)) && (place.y <= WALL_SIZE+(CORRIDOR_SIZE/2)+(WIGGLE_ROOM/2)))){
			//this also means that the player eats the center blip/item
			if(!(item instanceof NoItem)){
				player.addPoints(item.getPoints());
				if (item instanceof PowerBlip){
					PowerBlip pb = (PowerBlip) item;
					if (pb.isExists()){
						if (Globals.state == ENEMY_HUNTED_STATE){
							Globals.timeUntilLeaveingHuntedState += TIME_IN_HUNTED_STATE/3;
						}
						else{
							Globals.state = ENEMY_HUNTED_STATE;
						}
						Globals.game.powerBlipEaten();
						pb.setExists(false);
					}
				}
				else{
					item = new NoItem();
				}
			}
			else if (cluster.getValueAt(1, 1)){
				cluster.removeBlip(1, 1);
				player.addPoints(POINTS_FOR_BLIP);
				Globals.blipsLeft--;
			}
			return playerMoveOptions;
		}
		//if not, check to see if they're in an up/down corridor
		else if ((place.y < 18) || (place.y > 22)){
			int options = 0;
			if ((playerMoveOptions&NORTH) > 0){
				options |= NORTH;
			}
			if ((playerMoveOptions&SOUTH) > 0){
				options |= SOUTH;
			}
			//however, this also means that we might be able to eat either the north or south blips
			if (cluster.getValueAt(0, 1)){
				if ((place.y >= NORTH_BLIP_CENTER.y - BLIP_RANGE) &&
						(place.y <= NORTH_BLIP_CENTER.y + BLIP_RANGE)){
					cluster.removeBlip(0, 1);
					player.addPoints(POINTS_FOR_BLIP);
					Globals.blipsLeft--;
				}
			}
			if (cluster.getValueAt(2, 1)){
				if ((place.y >= SOUTH_BLIP_CENTER.y - BLIP_RANGE) &&
						(place.y <= SOUTH_BLIP_CENTER.y + BLIP_RANGE)){
					cluster.removeBlip(2, 1);
					player.addPoints(POINTS_FOR_BLIP);
					Globals.blipsLeft--;
				}
			}
			
			//make sure they can move forward when not at a junction
			options |= player.getDirection();
			return options;
		}
		else if ((place.x < 18) || (place.x > 22)){
			int options = 0;
			if ((playerMoveOptions&EAST) > 0){
				options |= EAST;
			}
			if ((playerMoveOptions&WEST) > 0){
				options |= WEST;
			}
			//however, this also means that we might be able to eat either the north or south blips
			if (cluster.getValueAt(1, 2)){
				if ((place.x >= EAST_BLIP_CENTER.x - BLIP_RANGE) &&
						(place.x <= EAST_BLIP_CENTER.x + BLIP_RANGE)){
					cluster.removeBlip(1, 2);
					player.addPoints(POINTS_FOR_BLIP);
					Globals.blipsLeft--;
				}
			}
			if (cluster.getValueAt(1, 0)){
				if ((place.x >= WEST_BLIP_CENTER.x - BLIP_RANGE) &&
						(place.x <= WEST_BLIP_CENTER.x + BLIP_RANGE)){
					cluster.removeBlip(1, 0);
					player.addPoints(POINTS_FOR_BLIP);
					Globals.blipsLeft--;
				}
			}
			
			//make sure they can move forward when not at a junction
			options |= player.getDirection();
			return options;
		}
		return 0;
	}

	/**
	 * @return the regenDoor
	 */
	public int getRegenDoor() {
		return regenDoor;
	}

	/**
	 * @param regenDoor the regenDoor to set
	 */
	public void setRegenDoor(int regenDoor) {
		this.regenDoor = regenDoor;
	}

	/**
	 * @return the initialItem
	 */
	public Item getInitialItem() {
		return initialItem;
	}
	
	/**
	 * @return the regen
	 */
	public boolean isRegen() {
		return regen;
	}

	/**
	 * @param regen the regen to set
	 */
	public void setRegen(boolean regen) {
		this.regen = regen;
		if (regen){
			this.cluster = new BlipCluster();
		}
	}

	public void setEnemyMoveOptions(int enemyMoveOptions) {
		this.enemyMoveOptions = enemyMoveOptions;
	}

	public void setPlayerMoveOptions(int playerMoveOptions) {
		this.playerMoveOptions = playerMoveOptions;
	}

	/**
	 * @param initialItem the initialItem to set
	 */
	public void setInitialItem(Item initialItem) {
		this.initialItem = initialItem;
	}

	public BlipCluster getInitialCluster() {
		return initialCluster;
	}

	public void setInitialCluster(BlipCluster initialCluster) {
		this.initialCluster = initialCluster;
	}

	public void setCluster(BlipCluster cluster) {
		this.cluster = cluster;
	}
	
}
