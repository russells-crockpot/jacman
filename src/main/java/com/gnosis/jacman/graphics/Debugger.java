/**
 * 
 */
package com.gnosis.jacman.graphics;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;

import javax.swing.JFrame;

import com.gnosis.jacman.engine.*;

/**
 * @author Brendan McGloin
 *
 */
@SuppressWarnings("serial")
public class Debugger extends Canvas implements KeyListener{
	
	private int playerChoice = 0;
	private boolean paused = true;
	
	public boolean done = false;
	
	private BufferStrategy strategy;
	private Enemy[] enemies;
	private Player player;
	private Board board;
	/**
	 * @param enemies
	 * @param player
	 * @param board
	 */
	public Debugger(Enemy[] enemies, Board board, JFrame parent) {
		super();
		this.enemies = enemies;
		this.player = new Player();
		this.board = board;
		this.addKeyListener(this);
		this.setIgnoreRepaint(true);
		this.setBounds(0, 0, board.getColumns()*Globals.TILE_SIZE.width, board.getRows()*Globals.TILE_SIZE.height+20);
		//TODO make this not rely on Border layout
		parent.add(this, BorderLayout.CENTER);
		parent.pack();
		this.setVisible(true);
		parent.setVisible(true);
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		start();
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
		//Tools.updateBoardForEnemies(enemies, board, g, this);
		//Tools.updateBoardForPlayer(player, board, g, this);
		GamePainters.drawBoard(board, g, this);
		GamePainters.drawEnemies(enemies, g, this);
		GamePainters.drawPlayer(player, g, this);
		GamePainters.paintBottomBar(player, board, g, this);
	}



	public void resetCanvas(){
		Graphics g = strategy.getDrawGraphics();
		paused = true;
		
		int x = (board.getPlayerStartPoint().x * Globals.TILE_SIZE.width) - 20;
		int y = (board.getPlayerStartPoint().y * Globals.TILE_SIZE.height) - 20;
		player.setCenter(new Point(x, y));
		
		GamePainters.drawBoard(board, g, this);
		GamePainters.drawEnemies(enemies, g, this);
		GamePainters.drawPlayer(player, g, this);
		GamePainters.paintBottomBar(player, board, g, this);
		
		strategy.show();
	}
	
	public void update(){
		Graphics g = strategy.getDrawGraphics();
		
		GamePainters.updateBoardForEnemies(enemies, board, g, this);
//		while (!done){
//			System.out.println("called");
//			//wait
//		}
		GamePainters.updateBoardForPlayer(player, board, g, this);
		GamePainters.drawEnemies(enemies, g, this);
		GamePainters.drawPlayer(player, g, this);
		GamePainters.paintBottomBar(player, board, g, this);
		
		strategy.show();
	}
	
	public void start(){
		//TODO reset board and tiles for winning
		Globals.state = Globals.ENEMY_HUNTER_STATE;
		int x = (board.getPlayerStartPoint().x * Globals.TILE_SIZE.width) - 20;
		int y = (board.getPlayerStartPoint().y * Globals.TILE_SIZE.height) - 20;
		player = new Player();
		player.setCenter(new Point(x, y));
		for (Enemy enemy: enemies){
			enemy.reset();
		}
		long currentTimeInState = 0;
		Globals.blipsLeft = board.getBlipCount();
		resetCanvas();
		paused = true;
		while ((this.isVisible()) && (player.getLives() >= 0)){
			while (paused){
				Thread.yield();
			}
			if (Globals.blipsLeft == 0){
				Globals.speed -= 10;
				start();
			}
			//now we need to see if the current state is enemy hunted
			if (Globals.state == Globals.ENEMY_HUNTED_STATE){
				//now we need to see if we should go out of it
				if (currentTimeInState >= Globals.TIME_IN_HUNTED_STATE){
					Globals.state = Globals.ENEMY_HUNTER_STATE;
					currentTimeInState = 0;
				}
				else {
					currentTimeInState += Globals.speed;
				}
			}
			player.move(board.getMoveOptions(player), playerChoice);
			Point regenDoorPoint = board.getRegenDoor();
			Point regenPoint = board.getRegenPoint();
			Point playerPoint = player.getCenter();
			//now updated the enemy movements
			//TODO add collision checks
			for (int i = 0; i < enemies.length; i++){
				//these are put here to reduce program jumping and cache misses
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
			checkCollision();
			update();
			//now we sleep the sleep time is the current speed
			try{
				Thread.sleep(Globals.speed);
			} catch(InterruptedException e){
				break;
			}
		}
	}
	
	public void checkCollision(){
		int state = Globals.state;
		int playerX = player.getCenter().x;
		int playerY = player.getCenter().y;
		
		for (Enemy enemy: enemies){
			int x = playerY - enemy.getCenter().x;
			int y = playerX - enemy.getCenter().y;
			if ((x >= -8) && (x <= 8) && (y >= -8) && (y <= 8)){
				if (state == Globals.ENEMY_HUNTER_STATE){
					System.out.println("collision");
					player.loseALife();
					resetCanvas();
					//we return because only one enemy should be able to make a player lose a life
					return;
				}
				else if (state == Globals.ENEMY_HUNTED_STATE){
					if (enemy.isAlive()){
						System.out.println("collision");
						enemy.setAlive(false);
						player.addPoints(3000);
					}
				}
			}
			else { continue;}
			/*
			//first, check to see if the enemy is within x distance to have a collision
			if ((x < -8) || (x > 8)){
				//System.out.println("x");
				//it's not so a collision is impossible
				continue;
			}
			//next, check y
			if ((y < -8) || (y > 8)){
				//System.out.println("y");
				//it's not so a collision is once again impossible
				continue;
			}
			//now we know that we have collided, therefore, we act accordingly
			if (state == Globals.ENEMY_HUNTER_STATE){
				System.out.println("collision");
				player.loseALife();
				resetCanvas();
				//we return because only one enemy should be able to make a player lose a life
				return;
			}
			else if (state == Globals.ENEMY_HUNTED_STATE){
				if (enemy.isAlive()){
					enemy.setAlive(false);
					player.addPoints(3000);
				}
			} */
		}
	}
	
	public void checkCollision2(){
		//TODO figure out a better way to do this
		int state = Globals.state;
		for (Enemy enemy: enemies){
			int x = player.getCenter().x - enemy.getCenter().x;
			int y = player.getCenter().y - enemy.getCenter().y;
			if (x < 0){
				x = Math.abs(x);
			}
			if (y < 0){
				y = Math.abs(y);
			}
			//there has been a collision
			if ((y < 8) && (y < 8)){
				if ((state == Globals.ENEMY_HUNTED_STATE) && (enemy.isAlive())){
					enemy.setAlive(false);
					player.addPoints(3000);
				}
				else if (state == Globals.ENEMY_HUNTER_STATE){
					player.loseALife();
					resetCanvas();
					return;
				}
			}
		}
		
	}
	
	public void keyPressed(KeyEvent e) {
		playerChoice = 0;
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP: playerChoice |= Globals.NORTH; break;
		case KeyEvent.VK_DOWN: playerChoice |= Globals.SOUTH; break;
		case KeyEvent.VK_RIGHT: playerChoice |= Globals.EAST; break;
		case KeyEvent.VK_LEFT: playerChoice |= Globals.WEST; break;
		case KeyEvent.VK_SPACE: paused = (paused)? false: true;
		}
		
	}
	
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
