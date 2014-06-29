/**
 * 
 */
package com.gnosis.jacman.ann;

import com.gnosis.jacman.engine.*;
import com.gnosis.jacman.ann.engine.*;

/**
 * This class contains a variety of static methods used for
 * the manipulation of ANN's, as well evaluating them.
 * 
 * @author Brendan McGloin
 */
public final class ANNTools {
	
	public static final int NEURONS_PER_ACTOR = 5;
	
	public static final int HIDDEN_LAYERS = 1;
	
	public static void applyANN(ANN net, Enemy[] enemies, Player player){
		for (int i = 0; i < enemies.length; i++){
			enemies[i].setOutCluster(net.getOutputLayer().getCluster(i));
		}
		player.setNerveCluster(net.getInputLayer().getCluster(0));
	}
	
	public static ANN makeInitialANN2(Board board, int enemies, double momentum){
		int size = NEURONS_PER_ACTOR + board.getRows() + board.getColumns();
		NerveCluster[] outClusters = new NerveCluster[enemies];
		for (int i = 0; i < enemies; i++){
			outClusters[i] = new NerveCluster(Enemy.NEURONS_IN_OUT_CLUSTER, momentum);
		}
		NerveCluster[] inClusters = new NerveCluster[enemies+2];
		int c = 0;
		inClusters[c] = new NerveCluster(size+4, momentum); c++;
		for (int i = 0; i < enemies; i++, c++){
			inClusters[c] = new NerveCluster(size, momentum);
		}
		inClusters[c] = new NerveCluster(board.getPowerBlips().size(), momentum);
		NerveCluster[] middleClusterArray = new NerveCluster[1];
		middleClusterArray[0]= new NerveCluster(((enemies * NEURONS_PER_ACTOR)+ ((1+enemies)*NEURONS_PER_ACTOR))+4,
				momentum);
		NeuronLayer inLayer = new NeuronLayer(inClusters, false);
		NeuronLayer outLayer = new NeuronLayer(outClusters, false);
		NeuronLayer middleLayer = new NeuronLayer(middleClusterArray, true);
		return new ANN(inLayer, outLayer, middleLayer);
	}
	
	public static void printInputsData(Board board, Enemy[] enemies, Player player, double[] inputs){
		int c = 0;
		System.out.printf("State:\t%.0f\n", inputs[c]); c++;
		System.out.println(">>>>>>>>>>>>Player<<<<<<<<<<<<");
		System.out.println("------Move Options------");
		System.out.println("N\t" + inputs[c]); c++;
		System.out.println("S\t" + inputs[c]); c++;
		System.out.println("E\t" + inputs[c]); c++;
		System.out.println("W\t" + inputs[c]); c++;
		System.out.println("------Direction------");
		System.out.println("N\t" + inputs[c]); c++;
		System.out.println("S\t" + inputs[c]); c++;
		System.out.println("E\t" + inputs[c]); c++;
		System.out.println("W\t" + inputs[c]); c++;
		System.out.println("------Row------");
		for (int i = 0; i < board.getColumns(); i++, c++){
			System.out.printf("%d\t%.0f\n", i, inputs[c]);
		}
		System.out.println("------Col------");
		for (int i = 0; i < board.getColumns(); i++, c++){
			System.out.printf("%d\t%.0f\n", i, inputs[c]);
		}
		for (int index = 0; index < enemies.length; index++){
			System.out.printf(">>>>>>>>>>>>Enemy%d<<<<<<<<<<<<\n", index);
			System.out.println("Alive\t"+inputs[c]); c++;
			System.out.println("------Move Options------");
			System.out.println("N\t" + inputs[c]); c++;
			System.out.println("S\t" + inputs[c]); c++;
			System.out.println("E\t" + inputs[c]); c++;
			System.out.println("W\t" + inputs[c]); c++;
			System.out.println("------Row------");
			for (int i = 0; i < board.getColumns(); i++, c++){
				System.out.printf("%d\t%.0f\n", i, inputs[c]);
			}
			System.out.println("------Col------");
			for (int i = 0; i < board.getColumns(); i++, c++){
				System.out.printf("%d\t%.0f\n", i, inputs[c]);
			}
		}
	}
	
	public static void printOutputsData(Board board, Enemy[] enemies, double[] outputs){
		for (int i = 0; i < enemies.length; i++){
			System.out.println("======================================");
			System.out.printf(">>>>>Enemy%d<<<<<\n", i);
			System.out.printf("N\t%.0f\n", outputs[(i*4)]);
			System.out.printf("S\t%.0f\n", outputs[(i*4)+1]);
			System.out.printf("E\t%.0f\n", outputs[(i*4)+2]);
			System.out.printf("W\t%.0f\n", outputs[(i*4)+3]);
		}
	}
	
	public static double[] getBoardCondition(Board board, Enemy[] enemies, Player player){
		int size = NEURONS_PER_ACTOR + board.getRows() + board.getColumns();
		double[] condition = new double[((enemies.length+1)*size)+4];
		if (Globals.state == Globals.ENEMY_HUNTER_STATE){
			condition[0] = 1;
		}
		else {
			condition[0] = 0;
		}
		
		//get the player's move options
		int moveOptions = board.getCurrentTile(player).getPlayerMoveOptions();
		if ((moveOptions&Globals.NORTH) > 0){
			condition[1] = 1;
		}
		else{
			condition[1] = 0;
		}
		if ((moveOptions&Globals.SOUTH) > 0){
			condition[2] = 1;
		}
		else{
			condition[2] = 0;
		}
		if ((moveOptions&Globals.EAST) > 0){
			condition[3] = 1;
		}
		else{
			condition[3] = 0;
		}
		if ((moveOptions&Globals.WEST) > 0){
			condition[4] = 1;
		}
		else{
			condition[4] = 0;
		}
		
		//get direction
		int direction = player.getDirection();
		if ((direction&Globals.NORTH) > 0){
			condition[5] = 1;
		}
		else{
			condition[5] = 0;
		}
		if ((direction&Globals.SOUTH) > 0){
			condition[6] = 1;
		}
		else{
			condition[6] = 0;
		}
		if ((direction&Globals.EAST) > 0){
			condition[7] = 1;
		}
		else{
			condition[7] = 0;
		}
		if ((direction&Globals.WEST) > 0){
			condition[8] = 1;
		}
		else{
			condition[8] = 0;
		}
		int c = 9;
		int playerRow = player.getCenter().y/Globals.TILE_SIZE.height;
		int playerCol = player.getCenter().x/Globals.TILE_SIZE.width;
		for (int i = 0; i < board.getRows(); i++, c++){
			condition[c] = (i == playerRow) ? 1:0;
		}
		for (int i = 0; i < board.getRows(); i++, c++){
			condition[c] = (i == playerCol) ? 1:0;
		}
		for (Enemy enemy: enemies){
			if(enemy.isAlive()){
				condition[c] = 0;
			}
			else {
				condition[c] = 1;
			}
			c++;
			moveOptions = board.getCurrentTile(enemy).getEnemyMoveOptions();
			if ((moveOptions&Globals.NORTH) > 0){
				condition[c] = 1;
			}
			else{
				condition[c] = 0;
			}
			c++;
			if ((moveOptions&Globals.SOUTH) > 0){
				condition[c] = 1;
			}
			else{
				condition[c] = 0;
			}
			c++;
			if ((moveOptions&Globals.EAST) > 0){
				condition[c] = 1;
			}
			else{
				condition[c] = 0;
			}
			c++;
			if ((moveOptions&Globals.WEST) > 0){
				condition[c] = 1;
			}
			else{
				condition[c] = 0;
			}
			c++;
			
			int enemyRow = enemy.getCenter().y/Globals.TILE_SIZE.height;
			int enemyCol = enemy.getCenter().x/Globals.TILE_SIZE.width;
			for (int i = 0; i < board.getRows(); i++, c++){
				condition[c] = (i == enemyRow) ? 1:0;
			}
			for (int i = 0; i < board.getRows(); i++, c++){
				condition[c] = (i == enemyCol) ? 1:0;
			}
		}
		return condition;
	}
}
