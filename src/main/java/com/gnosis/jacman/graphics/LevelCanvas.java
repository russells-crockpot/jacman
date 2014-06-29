/**
 * 
 */
package com.gnosis.jacman.graphics;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;

import com.gnosis.jacman.engine.*;

/**
 * This class is the guts and glory that is JacMan. It contains the main
 * game loop. It, however, does not handle the drawing directly,it merely 
 * calls the drawing methods located in graphics.GamePainters.
 * 
 * @author Brendan McGloin
 */
@SuppressWarnings("serial")
public class LevelCanvas extends Canvas implements KeyListener, Constants{
	
	private int playerChoice = 0;
	private boolean paused = false;
	
	private boolean stopped, waiting;
	
	private BufferStrategy strategy;
	private Enemy[] enemies;
	private Player player;
	private Board board;
	
	/**
	 * @param enemies An array of the different enemies
	 * @param board The current board (level)
	 */
	public LevelCanvas(Enemy[] enemies, Board board) {
		super();
		this.enemies = enemies;
		this.player = Globals.game.getPlayer();
		this.board = board;
		this.stopped = false;
		this.waiting = true;
		this.addKeyListener(this);
		this.setIgnoreRepaint(true);
		this.setBounds(0, 0, board.getColumns()*TILE_WIDTH, board.getRows()*TILE_HEIGHT+20);
		this.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				update();
			}
			public void focusGained(FocusEvent e) {
				update();
			}
		});
	}
	
	/**
	 * Creates the buffering strategy used by this canvas. Double-buffering
	 * is used, because it creates smoother movements.
	 */
	public void createStrategy(){
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		start();
	}
	
	public void loadGame(){
		this.stopped = true;
		this.board = Globals.game.getBoard();
		this.enemies = Globals.game.getEnemies();
		this.setBounds(0, 0, board.getColumns()*TILE_WIDTH, board.getRows()*TILE_HEIGHT+20);
		
		newGame();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		GamePainters.drawBoard(board, g, this);
		GamePainters.drawEnemies(enemies, g, this);
		GamePainters.drawPlayer(player, g, this);
		GamePainters.paintBottomBar(player, board, g, this);
	}



	/* (non-Javadoc)
	 * @see java.awt.Canvas#update(java.awt.Graphics)
	 */
	@Override
	public void update(Graphics g) {
		GamePainters.updateBoardForEnemies(enemies, board, g, this);
		GamePainters.updateBoardForPlayer(player, board, g, this);
		GamePainters.drawEnemies(enemies, g, this);
		GamePainters.drawPlayer(player, g, this);
		GamePainters.paintBottomBar(player, board, g, this);
	}



	/**
	 * Causes the ghosts and player to go back to their original starting positions. 
	 * Used when a player dies, for example.
	 */
	public void resetCanvas(){
		Graphics g = strategy.getDrawGraphics();
		waiting = true;
		
		Globals.state = ENEMY_HUNTER_STATE;
		
		player.setCenter(board.getPlayerStartPoint());
		player.setDirection(Player.DEFAULT_STARTING_DIRECTION);
		
		for (Enemy enemy: enemies){
			enemy.reset();
		}
		
		GamePainters.drawBoard(board, g, this);
		GamePainters.drawEnemies(enemies, g, this);
		GamePainters.drawPlayer(player, g, this);
		GamePainters.paintBottomBar(player, board, g, this);
		strategy.show();
	}
	
	/**
	 * Updates the canvas.
	 */
	public void update(){
		Graphics g = strategy.getDrawGraphics();
		
		GamePainters.drawBoard(board, g, this);
		GamePainters.drawEnemies(enemies, g, this);
		GamePainters.drawPlayer(player, g, this);
		GamePainters.paintBottomBar(player, board, g, this);
		
		strategy.show();
	}
	
	/**
	 * Paints "Game Over" on the canvas.
	 */
	public void paintGameOver(){
		Graphics g = strategy.getDrawGraphics();
		
		GamePainters.paintGameOver(g, this, board);
		
		strategy.show();
	}
	
	/**
	 * Paints "Pause" on the canvas.
	 */
	public void paintPauseScreen(){
		Graphics g = strategy.getDrawGraphics();
		
		GamePainters.paintPauseScreen(g, this, board);
		
		strategy.show();
	}

	
	/**
	 * Replaces the current player icon with one showing that the
	 * player has died.
	 */
	public void paintDeadPlayer(){
		Graphics g = strategy.getDrawGraphics();
		
		GamePainters.drawBoard(board, g, this);
		GamePainters.drawDeadPlayer(player, g, this);
		
		strategy.show();
	}
	
	/**
	 * Creates a new "game" from the current engine.Globals.game variable. While
	 * the ANN stays the same, the speed, actor positions, score, et cetera, are 
	 * all reset.
	 */
	public void newGame(){
		stopped = true;
		player.setLives(STARTING_LIVES);
		player.setScore(0);
		Graphics g = strategy.getDrawGraphics();
		waiting = true;
		
		Globals.state = ENEMY_HUNTER_STATE;
		
		player.setCenter(board.getPlayerStartPoint());
		player.setDirection(Player.DEFAULT_STARTING_DIRECTION);
		
		board.reset();
		Globals.blipsLeft = Globals.game.getBoard().getBlipCount();
		
		for (Enemy enemy: enemies){
			enemy.reset();
		}
		
		GamePainters.drawBoard(board, g, this);
		GamePainters.drawEnemies(enemies, g, this);
		GamePainters.drawPlayer(player, g, this);
		GamePainters.paintBottomBar(player, board, g, this);
		strategy.show();
	}
	
	/**
	 * This starts the main game loop. This is the guts of the game. When
	 * the loop starts it checks to see if the player has any lives left,
	 * if not, it prints "Game Over" and resets the game. If there are 
	 * lives left, it decides what the ghosts next move will be, detects 
	 * any collisions, and acts accordingly, and also gets input from the
	 * player
	 */
	public void start(){
		Globals.state = ENEMY_HUNTER_STATE;
		Globals.game.setPlayer(new Player());
		player = Globals.game.getPlayer();
		player.setCenter(board.getPlayerStartPoint());
		newGame();
		paused = false;
		waiting = true;
		Point regenDoorPoint = board.getRegenDoor();
		Point regenPoint = board.getRegenPoint();
		Point playerPoint = player.getCenter();
		while ((this.isVisible()) && (player.getLives() >= 0)){
			if (paused){
				paintPauseScreen();
			}
			while (paused || stopped || waiting){
				Thread.yield();
			}
			
			if (Globals.blipsLeft <= 0){
				Globals.speed *= SPEED_MOD;
				resetCanvas();
				board.reset();
				Globals.blipsLeft = board.getBlipCount();
			}
			//now we need to see if the current state is enemy hunted
			if (Globals.state == ENEMY_HUNTED_STATE){
				//now we need to see if we should go out of it
				if (Globals.timeUntilLeaveingHuntedState <= 0){
					Globals.state = ENEMY_HUNTER_STATE;
					Globals.timeUntilLeaveingHuntedState = TIME_IN_HUNTED_STATE;
				}
				else {
					//we shouldn't, so we decrement the counter
					Globals.timeUntilLeaveingHuntedState -= Globals.speed;
				}
			}
			//move the player
			player.move(board.getMoveOptions(player), playerChoice);
			if (Globals.aiMode == FSA_AI_MODE){
				//now updated the enemy movements
				for (int i = 0; i < enemies.length; i++){
					//these are put here to reduce program jumping and cache misses as well
					Enemy enemy = enemies[i];
					int moveOptions = board.getMoveOptions(enemy);
					//first, we need to take apporpriate actions if we're on a regen tile
					if (board.getCurrentTile(enemy).isRegen()){
						//first check to see if the enemy is alive
						if (!enemy.isAlive()){
							//it's not, so we make it alive
							enemy.setAlive(true);
						}
						//now we tell the enemy to move towards the the door
						enemy.move(moveOptions, regenDoorPoint, true);
					}
					else {
						//We're not in the regen pen, so now we check if the enemy is alive
						if (!enemy.isAlive()){
							//it's not, so we want to go towards the regen point
							enemy.move(moveOptions, regenPoint, false);
						}
						else {
							//the enemy is alive, so we tell it towards/away from the player
							enemy.move(moveOptions, playerPoint, false);
						}
					}
				}
			}
			else if (Globals.aiMode == ANN_AI_MODE){
				//Globals.game.getNet().process(ANNTools.getBoardCondition(board, enemies, player));
				player.setInputValues(board);
				for (Enemy enemy: enemies){
					enemy.setInputValues(board);
				}
				Globals.game.getNet().generateActivations();
				for (int i = 0; i < enemies.length; i++){
					//Last 2 values don't matter here
					enemies[i].move(board.getMoveOptions(enemies[i]), board.getRegenDoor(), board.getCurrentTile(enemies[i]).isRegen());
				}
			}
			
			//no we check for collisions and act appropriately
			checkCollision();
			//check to see if we should notify the game that we have stepped
			if (Globals.trainingMode != TRAINING_MODE_OFF){
				//we should, so we do.
				Globals.game.step();
			}
			//And now we sleep. The sleep time is the current speed
			try{
				Thread.sleep(Globals.speed);
			} catch(InterruptedException e){
				break;
			}
			//no update the screen
			update();
		}
		if (player.getLives() <= 0){
			paintGameOver();
			try{
				Thread.sleep(2500);
			} catch(InterruptedException e){
				//DO nothing
			}
			newGame();
			start();
		}
	}
	
	/**
	 * Checks if the player collides with a blip, power blip, or a ghost.
	 */
	public void checkCollision(){
		Rectangle playerRect = new Rectangle(player.getCenter().x-8, player.getCenter().y-8, 13, 13);
		for (Enemy enemy: enemies){
			Rectangle enemyRect = new Rectangle(enemy.getCenter().x-8, enemy.getCenter().y-8, 13, 13);
			if (playerRect.intersects(enemyRect)){
				if (!enemy.isAlive()){
					continue;
				}
				if (Globals.state == ENEMY_HUNTER_STATE){
					Globals.game.playerKilled();
					player.loseALife();
					//player is dead, so we paint the dead player screen
					paintDeadPlayer();
					try{
						//sleep to make it stay on screen
						Thread.sleep(1500);
					} catch(InterruptedException e){
						//DO nothing
					}
					resetCanvas();
					//we return because only one enemy should be able to make a player lose a life
					return;
				}
				else if (Globals.state == ENEMY_HUNTED_STATE){
					if (enemy.isAlive()){
						enemy.setAlive(false);
						player.addPoints(POINTS_FOR_KILLING_ENEMY);
					}
				}
			}
			else { continue;}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP: playerChoice = NORTH; waiting = false;break;
		case KeyEvent.VK_DOWN: playerChoice = SOUTH; waiting = false; break;
		case KeyEvent.VK_RIGHT: playerChoice = EAST; waiting = false; break;
		case KeyEvent.VK_LEFT: playerChoice = WEST; waiting = false; break;
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_P:
		case KeyEvent.VK_PAUSE: paused = (paused)? false: true; stopped = waiting= false; break;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		//do nothing
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
		
		playerChoice = 0;
	}

	/**
	 * @return the paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * @param paused the paused to set
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/**
	 * @return whether or not the game loop is temporarily stopped
	 */
	public boolean isStopped() {
		return stopped;
	}



	/**
	 * @param stopped sets whether or not the game loop is temporarily stopped
	 */
	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}



	/**
	 * @return An array containing all current enemies
	 */
	public Enemy[] getEnemies() {
		return enemies;
	}



	/**
	 * @param enemies the enemies to set
	 */
	public void setEnemies(Enemy[] enemies) {
		this.enemies = enemies;
	}



	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}



	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}



	/**
	 * @return the board
	 */
	public Board getBoard() {
		return this.board;
	}



	/**
	 * @param board the board to set
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * @return the strategy
	 */
	public BufferStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @return the waiting
	 */
	public boolean isWaiting() {
		return waiting;
	}

	/**
	 * @param waiting the waiting to set
	 */
	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}
	
	
}
