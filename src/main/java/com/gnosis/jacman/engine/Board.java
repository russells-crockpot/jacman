/**
 *
 */
package com.gnosis.jacman.engine;

import java.awt.Point;
import java.io.Serializable;
import java.util.*;

import com.gnosis.jacman.engine.items.PowerBlip;

/**
 * @author Brendan McGloin
 *
 */
public class Board implements Serializable, Constants {

    private static final long serialVersionUID = 0xf5863210;

    private Tile[][] board;
    private int blipCount;
    private int startingRow, startingCol;
    private Point regenDoor, regenPoint;
    private List<PowerBlip> powerBlips;

    public Board(int rows, int columns, int startingRow, int startingCol) {
        this.board = new Tile[rows][columns];
        blipCount = 0;
        this.startingRow = startingRow;
        this.startingCol = startingCol;
        powerBlips = new ArrayList<PowerBlip>();
    }

    public Board(int rows, int columns) {
        this.board = new Tile[rows][columns];
        blipCount = 0;
        powerBlips = new ArrayList<PowerBlip>();
    }

    public void addRow(Tile[] row, int index) {
        this.board[index] = row;
    }

    public void reset() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col].reset();
            }
        }
    }

    public void reBlip() {
        for(int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                Tile tile = board[row][col];
                blipCount += tile.getCluster().getTotalBlips();
                if (tile.isRegen()) {
                    this.regenPoint = new Point(col*TILE_WIDTH, row*TILE_HEIGHT);
                }
                if (tile.getItem() instanceof PowerBlip) {
                    powerBlips.add((PowerBlip) tile.getItem());
                    blipCount++;
                }
            }
        }
    }

    public void removeBlips(Tile tile) {
        BlipCluster cluster = tile.getCluster();
        int counter = 0;
        for (int i = 0; i < cluster.getCluster().length; i++) {
            for (int j = 0; j < cluster.getCluster()[i].length; j++) {
                if(cluster.getCluster()[i][j]) {
                    cluster.getCluster()[i][j] = false;
                    counter++;
                }
            }
        }
        this.blipCount -= counter;
    }

    public Tile getCurrentTile(Player player) {
        return getTile(player.getCenter().y/TILE_HEIGHT, player.getCenter().x/TILE_WIDTH);
    }

    public Tile getCurrentTile(Enemy enemy) {
        return getTile(enemy.getCenter().y/TILE_HEIGHT, enemy.getCenter().x/TILE_WIDTH);
    }

    public void setRegenDoor(int rowIndex, int columnIndex, int direction) {
        int x = columnIndex * TILE_WIDTH;
        int y = rowIndex * TILE_HEIGHT;
        regenDoor = new Point(x+20, y+20);
    }

    public void addTile(Tile tile, int rowIndex, int columnIndex, boolean regenPoint) {
        board[rowIndex][columnIndex] = tile;
        blipCount += tile.getCluster().getTotalBlips();
        if (regenPoint) {
            this.regenPoint = new Point(columnIndex*TILE_WIDTH, rowIndex*TILE_HEIGHT);
        }
        if (tile.getItem() instanceof PowerBlip) {
            powerBlips.add((PowerBlip) tile.getItem());
            blipCount++;
        }
    }

    public Point getRegenPoint() {
        return regenPoint;
    }

    public void addTile(Tile tile, int rowIndex, int columnIndex) {
        board[rowIndex][columnIndex] = tile;
        blipCount += tile.getCluster().getTotalBlips();
        if (tile.getItem() instanceof PowerBlip) {
            powerBlips.add((PowerBlip) tile.getItem());
            blipCount++;
        }
    }

    public Tile getTile(int rowIndex, int columnIndex) {
        return board[rowIndex][columnIndex];
    }

    public int getBlipCount() {
        return blipCount;
    }

    /**
     * @return the board
     */
    public Tile[][] getBoard() {
        return board;
    }

    /**
     * @return the playerStartPoint
     */
    public Point getPlayerStartPoint() {
        int x = startingCol * TILE_WIDTH +(TILE_WIDTH/2);
        int y = startingRow * TILE_HEIGHT +(TILE_HEIGHT/2);
        return new Point(x, y);
    }

    public int getRows() {
        return board.length;
    }

    public int getColumns() {
        return board[0].length;
    }

    public int getMoveOptions(Enemy enemy) {
        int row = enemy.getCenter().y/TILE_HEIGHT;
        int col = enemy.getCenter().x/TILE_WIDTH;

        return board[row][col].getEnemyOptions(enemy);
    }

    public int getMoveOptions(Player player) {
        int row = player.getCenter().y/TILE_HEIGHT;
        int col = player.getCenter().x/TILE_WIDTH;
        return board[row][col].getMoveOptions(player);
    }

    /**
     * @return the regenDoor
     */
    public Point getRegenDoor() {
        return regenDoor;
    }

    /**
     * @return the powerBlips
     */
    public List<PowerBlip> getPowerBlips() {
        return powerBlips;
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

    public void setPlayerStartPoint(int row, int col) {
        this.startingCol = col;
        this.startingRow = row;
    }

    public void setRegenDoor(Point regenDoor) {
        this.regenDoor = regenDoor;
    }

    public void setRegenPoint(Point regenPoint) {
        this.regenPoint = regenPoint;
    }
}
