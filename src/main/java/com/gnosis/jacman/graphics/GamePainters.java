/**
 *
 */
package com.gnosis.jacman.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.List;

import com.gnosis.jacman.engine.*;
import com.gnosis.jacman.engine.items.PowerBlip;


/**
 * This class contains all of the methods needed to paint and draw
 *  the different aspects of the game (tiles, player, et cetera).
 *  All of the methods are static because the methods only act on
 *  other objects, and doesn't need any fields, thus there is no
 *  need to create an object.
 *
 * @author Brendan McGloin
 */
public final class GamePainters implements Constants {

    public static final int BLIP_DIAMETER = 3;
    public static final int POWER_BLIP_DIAMETER = 8;

    private static final int PLAYER_RADIUS = 1 + Globals.PLAYER_SIZE.height/2;

    public static void paintEnemyImage(Enemy enemy, Graphics g, ImageObserver observer) {
        int x = enemy.getCenter().x - (Globals.PLAYER_SIZE.width/2);
        int y = enemy.getCenter().y - (Globals.PLAYER_SIZE.height/2);
        if (enemy.isAlive()) {
            g.drawImage(enemy.getImage(), x, y, Globals.PLAYER_SIZE.width, Globals.PLAYER_SIZE.height, observer);
        }
        if (EnemyImageCache.EYES != null) {
            g.drawImage(EnemyImageCache.EYES, x, y, Globals.PLAYER_SIZE.width, Globals.PLAYER_SIZE.height, observer);
        }

    }

    public static void updateBoardForEnemies(Enemy[] enemies, Board board, Graphics g, ImageObserver observer) {
        for (Enemy enemy: enemies) {
            int row = enemy.getCenter().y/TILE_HEIGHT;
            int col = enemy.getCenter().x/TILE_WIDTH;
            int x = enemy.getTilePlace().x;
            int y = enemy.getTilePlace().y;
            drawTile(board.getTile(row, col), col, row, g, observer);
            //first check to see if any other drawing needs to occur
            if ((x > PLAYER_RADIUS) && (x < TILE_WIDTH - PLAYER_RADIUS) &&
                    (y > PLAYER_RADIUS) && (y < TILE_HEIGHT - PLAYER_RADIUS)) {
                //we don't, so leave
                return;
            }
            //top one needs to be redrawn
            else if ((y < PLAYER_RADIUS) && (row > 0)) {
                drawTile(board.getTile(row-1, col), col, row-1, g, observer);
            }
            //bottom one needs to be redrawn
            else if ((y > TILE_HEIGHT - (PLAYER_RADIUS)) && (row < board.getRows()-1)) {
                drawTile(board.getTile(row+1, col), col, row+1, g, observer);
            }
            //right one needs to be redrawn
            else if ((x > TILE_WIDTH - (PLAYER_RADIUS)) && (col < board.getColumns()-1)) {
                drawTile(board.getTile(row, col+1), col+1, row, g, observer);
            }
            //left one needs to be redrawn
            else if ((x < PLAYER_RADIUS) && (col > 0)) {
                drawTile(board.getTile(row, col-1), col-1, row, g, observer);
            }
        }
    }

    public static void updateBoardForEnemies(List<Enemy> enemies, Board board, Graphics g, ImageObserver observer) {
        for (Enemy enemy: enemies) {
            int row = enemy.getCenter().y/TILE_HEIGHT;
            int col = enemy.getCenter().x/TILE_WIDTH;
            int x = enemy.getTilePlace().x;
            int y = enemy.getTilePlace().y;
            drawTile(board.getTile(row, col), col, row, g, observer);
            //first check to see if any other drawing needs to occur
            if ((x > PLAYER_RADIUS) && (x < TILE_WIDTH - PLAYER_RADIUS) &&
                    (y > PLAYER_RADIUS) && (y < TILE_HEIGHT - PLAYER_RADIUS)) {
                //we don't, so leave
                return;
            }
            //top one needs to be redrawn
            else if ((y < PLAYER_RADIUS) && (row > 0)) {
                drawTile(board.getTile(row-1, col), col, row-1, g, observer);
            }
            //bottom one needs to be redrawn
            else if ((y > TILE_HEIGHT - PLAYER_RADIUS) && (row < board.getRows()-1)) {
                drawTile(board.getTile(row+1, col), col, row+1, g, observer);
            }
            //right one needs to be redrawn
            else if ((x > TILE_WIDTH - PLAYER_RADIUS) && (col < board.getColumns()-1)) {
                drawTile(board.getTile(row, col+1), col+1, row, g, observer);
            }
            //left one needs to be redrawn
            else if ((x < PLAYER_RADIUS) && (col > 0)) {
                drawTile(board.getTile(row, col-1), col-1, row, g, observer);
            }
        }

    }

    public static void updateBoardForPlayer(Player player, Board board, Graphics g, ImageObserver observer) {
        int row = player.getCenter().y/TILE_HEIGHT;
        int col = player.getCenter().x/TILE_WIDTH;
        int x = player.getTilePlace().x;
        int y = player.getTilePlace().y;
        drawTile(board.getTile(row, col), col, row, g, observer);
        //first check to see if any other drawing needs to occur
        if ((x > PLAYER_RADIUS) && (x < TILE_WIDTH - PLAYER_RADIUS) &&
                (y > PLAYER_RADIUS) && (y < TILE_HEIGHT - PLAYER_RADIUS)) {
            //we don't, so leave
            return;
        }
        //top one needs to be redrawn
        else if ((y < PLAYER_RADIUS) && (row > 0)) {
            drawTile(board.getTile(row-1, col), col, row-1, g, observer);
        }
        //bottom one needs to be redrawn
        else if ((y > TILE_HEIGHT - PLAYER_RADIUS) && (row < board.getRows()-1)) {
            drawTile(board.getTile(row+1, col), col, row+1, g, observer);
        }
        //right one needs to be redrawn
        else if ((x > TILE_WIDTH - PLAYER_RADIUS) && (col < board.getColumns()-1)) {
            drawTile(board.getTile(row, col+1), col+1, row, g, observer);
        }
        //left one needs to be redrawn
        else if ((x < PLAYER_RADIUS) && (col > 0)) {
            drawTile(board.getTile(row, col-1), col-1, row, g, observer);
        }
    }

    public static void paintGameOver(Graphics g, ImageObserver observer, Board board) {
        double scale = (double) (board.getColumns()*40)/OTHER_IMAGE_CACHE.getImage(OtherImageCache.GAME_OVER).getWidth();
        int height = (int) (OTHER_IMAGE_CACHE.getImage(OtherImageCache.PAUSED).getHeight()*scale);
        g.drawImage(OTHER_IMAGE_CACHE.getImage(OtherImageCache.GAME_OVER), 0, ((board.getRows()*40)/2 - (height/2)), (board.getColumns()*40), height, observer);
    }

    public static void paintPauseScreen(Graphics g, ImageObserver observer, Board board) {
        double scale = (double) (board.getColumns()*40)/OTHER_IMAGE_CACHE.getImage(OtherImageCache.PAUSED).getWidth();
        int height = (int) (OTHER_IMAGE_CACHE.getImage(OtherImageCache.PAUSED).getHeight()*scale);
        g.drawImage(OTHER_IMAGE_CACHE.getImage(OtherImageCache.PAUSED), 0, ((board.getRows()*40)/2 - (height/2)), (board.getColumns()*40), height, observer);
    }

    public static void drawEnemies(Enemy[] enemies, Graphics g, ImageObserver observer) {
        for (Enemy enemy: enemies) {
            paintEnemyImage(enemy, g, observer);
        }
    }

    public static void drawEnemies(List<Enemy> enemies, Graphics g, ImageObserver observer) {
        for (Enemy enemy: enemies) {
            paintEnemyImage(enemy, g, observer);
        }
    }

    public static void paintBottomBar(Player player, Board board, Graphics g, ImageObserver observer) {
        int width = board.getColumns() * TILE_WIDTH;
        //the y of the corner were to start drawing
        int startingY = board.getRows() * TILE_HEIGHT;
        g.setColor(Globals.backgroundColor);
        g.fillRect(0, startingY, width, BOTTOM_BAR_HEIGHT);
        g.setColor(Globals.wallColor);
        //TODO figure out a good way to get rid of these magic numbers
        g.drawString("" + player.getScore(), 5, startingY + 14);
        // now paint the lives
        g.drawImage(OTHER_IMAGE_CACHE.getImage(OtherImageCache.LIFE), width - TILE_WIDTH, startingY+5, LIVES_ICON_WIDTH, LIVES_ICON_HEIGHT, observer);
        g.drawString("x" + player.getLives(), width - TILE_WIDTH + (LIVES_ICON_WIDTH +2), startingY+14);
    }

    public static void drawDeadPlayer(Player player, Graphics g, ImageObserver observer) {
        int x = player.getCenter().x - (Globals.PLAYER_SIZE.width/2);
        int y = player.getCenter().y - (Globals.PLAYER_SIZE.height/2);
        BufferedImage image = PlayerImageCache.DEAD_JACMAN;
        if (image != null) {
            g.drawImage(image, x, y, Globals.PLAYER_SIZE.width, Globals.PLAYER_SIZE.height, observer);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, Globals.PLAYER_SIZE.width, Globals.PLAYER_SIZE.height);
        }
    }

    public static void drawPlayer(Player player, Graphics g, ImageObserver observer) {
        int x = player.getCenter().x - (Globals.PLAYER_SIZE.width/2);
        int y = player.getCenter().y - (Globals.PLAYER_SIZE.height/2);
        BufferedImage image = player.getImage();
        if (image != null) {
            g.drawImage(image, x, y, Globals.PLAYER_SIZE.width, Globals.PLAYER_SIZE.height, observer);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, Globals.PLAYER_SIZE.width, Globals.PLAYER_SIZE.height);
        }
    }

    public static void drawGridBoard(Board board, Graphics g) {
        Tile[][] boardArray = board.getBoard();
        int xSpace = board.getColumns() * TILE_HEIGHT;
        int ySpace = board.getRows() * TILE_WIDTH;

        for (int currentRow = 0; currentRow < boardArray.length; currentRow++) {
            for (int currentCol = 0; currentCol < boardArray[currentRow].length; currentCol++) {
                drawTile(boardArray[currentRow][currentCol], currentCol, currentRow, g);
            }
        }
        g.setColor(Color.RED);
        for (int currentX = 0; currentX < xSpace; currentX += TILE_WIDTH) {
            g.drawLine(currentX, 0, currentX, ySpace);
        }
        g.setColor(Color.RED);
        for (int currentY = 0; currentY < ySpace; currentY += TILE_WIDTH) {
            g.drawLine(0, currentY, xSpace, currentY);
        }
    }

    public static void drawBoard(Board board, Graphics g, ImageObserver observer) {
        Tile[][] boardArray = board.getBoard();
        for (int currentRow = 0; currentRow < boardArray.length; currentRow++) {
            for (int currentCol = 0; currentCol < boardArray[currentRow].length; currentCol++) {
                drawTile(boardArray[currentRow][currentCol], currentCol, currentRow, g, observer);
            }
        }
    }

    public static void drawTile(Tile tile, int colIndex, int rowIndex, Graphics g) {
        //start by calculateing where to start filling
        int xPos = colIndex * TILE_WIDTH;
        int yPos = rowIndex * TILE_HEIGHT;

        if (tile.getEnemyMoveOptions() == EMPTY) {
            g.setColor(Globals.backgroundColor);
            g.fillRect(xPos, yPos, TILE_WIDTH, TILE_HEIGHT);
            return;
        }

        if (tile.isRegen()) {
            g.setColor(Globals.penColor);
            g.fillRect(xPos, yPos, TILE_WIDTH, TILE_HEIGHT);
            return;
        }

        //then fill the background square
        g.setColor(Globals.backgroundColor);
        g.fillRect(xPos, yPos, TILE_WIDTH, TILE_HEIGHT);

        //get all of the walls
        int walls = tile.getEnemyMoveOptions()|tile.getPlayerMoveOptions();

        if(walls == Globals.OPEN) {
            return;
        }

        //now fill the walls
        g.setColor(Globals.wallColor);

        //check to see if this is a solid block
        if (walls == Globals.FILLED) {
            g.fillRect(xPos, yPos, TILE_WIDTH, TILE_HEIGHT);
            return;
        }

        //go through and check which ones to draw
        if ((walls & NORTH) > 0) {
            g.fillRect(xPos, yPos, WALL_SIZE, WALL_SIZE);
        } else {
            g.fillRect(xPos, yPos, TILE_WIDTH, WALL_SIZE);
        }
        if ((walls & SOUTH) > 0) {
            g.fillRect((xPos + TILE_HEIGHT -WALL_SIZE), (yPos + TILE_WIDTH -WALL_SIZE), WALL_SIZE, WALL_SIZE);
        } else {
            g.fillRect(xPos, (yPos + TILE_HEIGHT -WALL_SIZE), TILE_WIDTH, WALL_SIZE);
        }
        if ((walls & EAST) > 0) {
            g.fillRect((xPos + TILE_HEIGHT -WALL_SIZE), yPos, WALL_SIZE, WALL_SIZE);
        } else {
            g.fillRect((xPos + TILE_HEIGHT -WALL_SIZE), yPos, WALL_SIZE, TILE_WIDTH);
        }
        if ((walls & WEST) > 0) {
            g.fillRect(xPos, (yPos + TILE_WIDTH -WALL_SIZE), WALL_SIZE, WALL_SIZE);
        } else {
            g.fillRect(xPos, yPos, WALL_SIZE, TILE_HEIGHT);
        }

        //now check to see if any doors need to be drawn
        if (tile.getEnemyMoveOptions() != tile.getPlayerMoveOptions()) {
            //get all the doors that will need to be drawn
            int allDoors = tile.getEnemyMoveOptions() ^ tile.getPlayerMoveOptions();
            int enemyDoors = tile.getEnemyMoveOptions() & allDoors;
            int playerDoors = tile.getPlayerMoveOptions() & allDoors;

            //draw enemy doors first
            g.setColor(Globals.enemyDoorColor);
            //north door
            if ((enemyDoors&NORTH) > 0) {
                int y = yPos + WALL_SIZE;
                g.drawLine(xPos + WALL_SIZE, y, xPos + WALL_SIZE + CORRIDOR_SIZE, y);
            }
            //south door
            if ((enemyDoors&SOUTH) > 0) {
                int y = yPos + (TILE_HEIGHT - WALL_SIZE);
                g.drawLine(xPos + WALL_SIZE, y, xPos + WALL_SIZE + CORRIDOR_SIZE, y);
            }
            //east door
            if ((enemyDoors&WEST) > 0) {
                int x = xPos + WALL_SIZE;
                g.drawLine(x, yPos + WALL_SIZE, x, yPos + WALL_SIZE + CORRIDOR_SIZE);
            }
            //west door
            if ((enemyDoors&EAST) > 0) {
                int x = xPos + (TILE_WIDTH - WALL_SIZE);
                g.drawLine(x, yPos + WALL_SIZE, x, yPos + WALL_SIZE + CORRIDOR_SIZE);
            }

            //now draw the player doors
            g.setColor(Globals.playerDoorColor);
            //north door
            if ((playerDoors&NORTH) > 0) {
                int y = yPos + WALL_SIZE;
                g.drawLine(xPos + WALL_SIZE, y, xPos + WALL_SIZE + CORRIDOR_SIZE, y);
            }
            //south door
            if ((playerDoors&SOUTH) > 0) {
                int y = yPos + (TILE_HEIGHT - WALL_SIZE);
                g.drawLine(xPos + WALL_SIZE, y, xPos + WALL_SIZE + CORRIDOR_SIZE, y);
            }
            //east door/south door
            if ((playerDoors&WEST) > 0) {
                int x = xPos + WALL_SIZE;
                g.drawLine(x-1, yPos + WALL_SIZE, x-1, yPos + WALL_SIZE + CORRIDOR_SIZE);
            }
            //west door/south door
            if ((playerDoors&EAST) > 0) {
                int x = xPos + (TILE_WIDTH - WALL_SIZE);
                g.drawLine(x, yPos + WALL_SIZE, x, yPos + WALL_SIZE + CORRIDOR_SIZE);
            }
        }

        //now draw the blips
        BlipCluster cluster = tile.getCluster();
        g.setColor(Globals.blipColor);
        //north
        if (cluster.getValueAt(0, 1)) {
            g.fillOval(xPos + NORTH_BLIP_CENTER.x - (BLIP_DIAMETER/2), yPos + NORTH_BLIP_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }
        //south
        if (cluster.getValueAt(2, 1)) {
            g.fillOval(xPos + SOUTH_BLIP_CENTER.x - (BLIP_DIAMETER/2), yPos + SOUTH_BLIP_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }
        //east
        if (cluster.getValueAt(1, 2)) {
            g.fillOval(xPos + EAST_BLIP_CENTER.x - (BLIP_DIAMETER/2), yPos + EAST_BLIP_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }
        //west
        if (cluster.getValueAt(1, 0)) {
            g.fillOval(xPos + WEST_BLIP_CENTER.x - (BLIP_DIAMETER/2), yPos + WEST_BLIP_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }
        //center
        if (cluster.getValueAt(1, 1)) {
            g.fillOval(xPos + ITEM_CENTER.x - (BLIP_DIAMETER/2), yPos + ITEM_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }

        //Now draw items. Right now only the Power Blip can be drawn
        if (tile.getItem() != null) {
            if (tile.getItem() instanceof PowerBlip) {
                PowerBlip pb = (PowerBlip) tile.getItem();
                if (pb.exists()) {
                    g.fillOval(xPos + ITEM_CENTER.x - (POWER_BLIP_DIAMETER/2), yPos + ITEM_CENTER.y - (POWER_BLIP_DIAMETER/2), POWER_BLIP_DIAMETER, POWER_BLIP_DIAMETER);
                }
            }
        }
    }

    public static void drawTile(Tile tile, int colIndex, int rowIndex, Graphics g, ImageObserver observer) {
        //start by calculateing where to start filling
        int xPos = colIndex * TILE_WIDTH;
        int yPos = rowIndex * TILE_HEIGHT;

        if (tile.isRegen()) {
            g.drawImage(TileImageCache.REGEN_TILE, xPos, yPos, observer);
            return;
        }

        g.drawImage(TILE_IMAGE_CACHE.getImage(tile.getEnemyMoveOptions()|tile.getPlayerMoveOptions()), xPos, yPos, observer);

        //now check to see if any doors need to be drawn
        if (tile.getEnemyMoveOptions() != tile.getPlayerMoveOptions()) {
            //get all the doors that will need to be drawn
            int allDoors = tile.getEnemyMoveOptions() ^ tile.getPlayerMoveOptions();
            int enemyDoors = tile.getEnemyMoveOptions() & allDoors;
            int playerDoors = tile.getPlayerMoveOptions() & allDoors;

            //draw enemy doors first
            g.setColor(Globals.enemyDoorColor);
            //north door
            if ((enemyDoors&NORTH) > 0) {
                int y = yPos + WALL_SIZE;
                g.drawLine(xPos + WALL_SIZE, y, xPos + WALL_SIZE + CORRIDOR_SIZE, y);
            }
            //south door
            if ((enemyDoors&SOUTH) > 0) {
                int y = yPos + (TILE_HEIGHT - WALL_SIZE);
                g.drawLine(xPos + WALL_SIZE, y, xPos + WALL_SIZE + CORRIDOR_SIZE, y);
            }
            //east door
            if ((enemyDoors&WEST) > 0) {
                int x = xPos + WALL_SIZE;
                g.drawLine(x, yPos + WALL_SIZE, x, yPos + WALL_SIZE + CORRIDOR_SIZE);
            }
            //west door
            if ((enemyDoors&EAST) > 0) {
                int x = xPos + (TILE_WIDTH - WALL_SIZE);
                g.drawLine(x, yPos + WALL_SIZE, x, yPos + WALL_SIZE + CORRIDOR_SIZE);
            }

            //now draw the player doors
            g.setColor(Globals.playerDoorColor);
            //north door
            if ((playerDoors&NORTH) > 0) {
                int y = yPos + WALL_SIZE;
                g.drawLine(xPos + WALL_SIZE, y, xPos + WALL_SIZE + CORRIDOR_SIZE, y);
            }
            //south door
            if ((playerDoors&SOUTH) > 0) {
                int y = yPos + (TILE_HEIGHT - WALL_SIZE);
                g.drawLine(xPos + WALL_SIZE, y, xPos + WALL_SIZE + CORRIDOR_SIZE, y);
            }
            //east door/south door
            if ((playerDoors&WEST) > 0) {
                int x = xPos + WALL_SIZE;
                g.drawLine(x-1, yPos + WALL_SIZE, x-1, yPos + WALL_SIZE + CORRIDOR_SIZE);
            }
            //west door/south door
            if ((playerDoors&EAST) > 0) {
                int x = xPos + (TILE_WIDTH - WALL_SIZE);
                g.drawLine(x, yPos + WALL_SIZE, x, yPos + WALL_SIZE + CORRIDOR_SIZE);
            }
        }

        //now draw the blips
        BlipCluster cluster = tile.getCluster();
        g.setColor(Globals.blipColor);
        //north
        if (cluster.getValueAt(0, 1)) {
            g.fillOval(xPos + NORTH_BLIP_CENTER.x - (BLIP_DIAMETER/2), yPos + NORTH_BLIP_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }
        //south
        if (cluster.getValueAt(2, 1)) {
            g.fillOval(xPos + SOUTH_BLIP_CENTER.x - (BLIP_DIAMETER/2), yPos + SOUTH_BLIP_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }
        //east
        if (cluster.getValueAt(1, 2)) {
            g.fillOval(xPos + EAST_BLIP_CENTER.x - (BLIP_DIAMETER/2), yPos + EAST_BLIP_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }
        //west
        if (cluster.getValueAt(1, 0)) {
            g.fillOval(xPos + WEST_BLIP_CENTER.x - (BLIP_DIAMETER/2), yPos + WEST_BLIP_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }
        //center
        if (cluster.getValueAt(1, 1)) {
            g.fillOval(xPos + ITEM_CENTER.x - (BLIP_DIAMETER/2), yPos + ITEM_CENTER.y - (BLIP_DIAMETER/2), BLIP_DIAMETER, BLIP_DIAMETER);
        }

        //Now draw items. Right now only the Power Blip can be drawn
        if (tile.getItem() != null) {
            if (tile.getItem() instanceof PowerBlip) {
                PowerBlip pb = (PowerBlip) tile.getItem();
                if (pb.isExists()) {
                    g.fillOval(xPos + ITEM_CENTER.x - (POWER_BLIP_DIAMETER/2), yPos + ITEM_CENTER.y - (POWER_BLIP_DIAMETER/2), POWER_BLIP_DIAMETER, POWER_BLIP_DIAMETER);
                }
            }
        }
    }
}
