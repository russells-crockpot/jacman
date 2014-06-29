/**
 *
 */
package com.gnosis.jacman.engine;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.gnosis.jacman.ann.engine.NerveCluster;
import com.gnosis.jacman.ann.engine.Neuron;

/**
 * @author Brendan McGloin
 *
 */
public class Player implements Constants {

    protected Point center;
    protected int direction;
    protected boolean useImage1;
    private int changeImageBuffer;

    private byte lives;

    private long score, pointsCounter;

    private NerveCluster nerveCluster;

    public static final int DEFAULT_STARTING_DIRECTION = EAST;


    public Player() {
        super();
        lives = STARTING_LIVES;
        score = 0;
        pointsCounter = 0;
        direction = DEFAULT_STARTING_DIRECTION;
    }


    /**
     * @return the score
     */
    public long getScore() {
        return score;
    }

    public void addPoints(int points) {
        score += points;
        pointsCounter += points;
        if (pointsCounter > POINTS_TO_GET_AN_EXTRA_LIFE) {
            pointsCounter = 0;
            lives++;
        }
    }

    public void loseALife() {
        lives--;
    }

    public void gainALife() {
        if (lives == MAX_LIVES) {
            lives++;
        }
    }

    public void move(int moveOptions, int goal) {
        //check to see if you can move the way you want the player to
        if ((goal&moveOptions)> 0) {
            direction = goal;
        } else if ((direction&moveOptions) > 0) {
            moveForward();
            return;
        }

        //if not, we check to see if moving forward is ok
        else if ((direction&moveOptions) <= 0) {
            //check to see if were at a corner
            if (moveOptions == (NORTH|EAST)) {
                if (direction == NORTH) {
                    direction = EAST;
                } else if (direction == EAST) {
                    direction = NORTH;
                }
            }
            //NW
            else if (moveOptions == (NORTH|WEST)) {
                if (direction == NORTH) {
                    direction = WEST;
                } else if (direction == WEST) {
                    direction = NORTH;
                }
            }
            //SW
            else if (moveOptions == (SOUTH|WEST)) {
                if (direction == SOUTH) {
                    direction = WEST;
                } else if (direction == WEST) {
                    direction = SOUTH;
                }
            }
            //SE
            else if (moveOptions == (SOUTH|EAST)) {
                if (direction == SOUTH) {
                    direction = EAST;
                } else if (direction == EAST) {
                    direction = SOUTH;
                }
            }

            int options = moveOptions;
            //make sure you don't go backwards
            switch (direction) {
            case NORTH:
                options &=(~SOUTH);
                break;
            case SOUTH:
                options &=(~NORTH);
                break;
            case EAST:
                options &=(~WEST);
                break;
            case WEST:
                options &=(~EAST);
                break;
            }
            direction = chooseRandomDirection(options);
        }
        moveForward();
    }

    /**
     * @return the lives
     */
    public byte getLives() {
        return lives;
    }

    /**
     * @param lives the lives to set
     */
    public void setLives(byte lives) {
        this.lives = lives;
    }

    public BufferedImage getImage() {
        return PLAYER_IMAGE_CACHE.getImage(direction, useImage1);
    }
    public Rectangle getRectangle() {
        return new Rectangle(center.x-8, center.y-8, 14, 14);
    }

    protected void moveForward() {
        switch (direction) {
        case NORTH:
            center.y = center.y - MOVEMENT_RATE;
            break;
        case SOUTH:
            center.y = center.y + MOVEMENT_RATE;
            break;
        case EAST:
            center.x = center.x + MOVEMENT_RATE;
            break;
        case WEST:
            center.x = center.x - MOVEMENT_RATE;
            break;
        }
        changeImageBuffer++;
        if (changeImageBuffer > MOVES_TO_SWITCH_IMAGE) {
            changeImageBuffer = 0;
            useImage1 = (useImage1)? false:true;
        }
    }

    protected static int chooseRandomDirection(int options) {
        List<Integer> choices = new ArrayList<Integer>(4);
        if ((options&NORTH) > 0) {
            choices.add(NORTH);
        }
        if ((options&SOUTH) > 0) {
            choices.add(SOUTH);
        }
        if ((options&EAST) > 0) {
            choices.add(EAST);
        }
        if ((options&WEST) > 0) {
            choices.add(WEST);
        }
        if (choices.isEmpty()) {
            //this should never happen, but just in case
            System.out.println("This should've never happen");
            return 0;
        }
        return choices.get(RNG.nextInt(choices.size()));
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

    public void setInputValues(Board board) {
        if (nerveCluster == null) {
            //TODO add abstraction
            nerveCluster = Globals.game.getNet().getInputLayer().getCluster(0);
        }
        Neuron[] cluster = nerveCluster.getCluster();
        int c = 0;
        cluster[0].setActivation((Globals.state == ENEMY_HUNTER_STATE)? 1:0);
        c++;
        int moveOptions = board.getMoveOptions(this);
        cluster[c].setActivation(((moveOptions&NORTH) > 0)? 1:0);
        c++;
        cluster[c].setActivation(((moveOptions&SOUTH) > 0)? 1:0);
        c++;
        cluster[c].setActivation(((moveOptions&EAST) > 0)? 1:0);
        c++;
        cluster[c].setActivation(((moveOptions&WEST) > 0)? 1:0);
        c++;
        cluster[c].setActivation((direction == NORTH)? 1:0);
        c++;
        cluster[c].setActivation((direction == SOUTH)? 1:0);
        c++;
        cluster[c].setActivation((direction == EAST)? 1:0);
        c++;
        cluster[c].setActivation((direction == WEST)? 1:0);
        c++;
        int row = center.y/TILE_HEIGHT;
        int col = center.x/TILE_WIDTH;
        for (int i = 0; i < board.getRows(); i++, c++) {
            cluster[c].setActivation((i == row)? 1:0);
        }
        for (int i = 0; i < board.getColumns(); i++, c++) {
            cluster[c].setActivation((i == col)? 1:0);
        }
    }

    public Point getTilePlace() {
        int x = center.x % TILE_WIDTH;
        int y = center.y % TILE_HEIGHT;
        return new Point(x, y);
    }


    public NerveCluster getNerveCluster() {
        return nerveCluster;
    }


    public void setNerveCluster(NerveCluster nerveCluster) {
        this.nerveCluster = nerveCluster;
    }


    public void setScore(long score) {
        this.score = score;
    }
}
