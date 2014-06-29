/**
 *
 */
package com.gnosis.jacman.engine;

/**
 * @author Brendan McGloin
 *
 */
public class KeyBindings {

    private int up, down, right, left, pause;

    /**
     * @param up
     * @param down
     * @param right
     * @param left
     * @param pause
     */
    public KeyBindings(int up, int down, int right, int left, int pause) {
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.pause = pause;
    }

    /**
     * @return the up
     */
    public int getUp() {
        return up;
    }

    /**
     * @param up the up to set
     */
    public void setUp(int up) {
        this.up = up;
    }

    /**
     * @return the down
     */
    public int getDown() {
        return down;
    }

    /**
     * @param down the down to set
     */
    public void setDown(int down) {
        this.down = down;
    }

    /**
     * @return the right
     */
    public int getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(int right) {
        this.right = right;
    }

    /**
     * @return the left
     */
    public int getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(int left) {
        this.left = left;
    }

    /**
     * @return the pause
     */
    public int getPause() {
        return pause;
    }

    /**
     * @param pause the pause to set
     */
    public void setPause(int pause) {
        this.pause = pause;
    }
}
