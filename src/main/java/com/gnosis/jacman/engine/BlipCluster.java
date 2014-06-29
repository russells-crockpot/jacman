/**
 *
 */
package com.gnosis.jacman.engine;

import java.io.Serializable;

/**
 * This represents all of the blips on a given tile.
 *
 * @author Brendan McGloin
 */
public class BlipCluster implements Serializable, Constants {

    private static final long serialVersionUID = 0xff32810;


    private boolean[][] cluster;
    private byte count;

    public BlipCluster() {
        cluster = new boolean[MAX_BLIPS_IN_COLUMN][MAX_BLIPS_IN_ROW];
        count = 0;
    }

    public BlipCluster clone() {
        boolean[][] newCluster = new boolean[MAX_BLIPS_IN_COLUMN][];
        for (int i = 0; i < cluster.length; i++) {
            newCluster[i] = cluster[i].clone();
        }
        BlipCluster bc = new BlipCluster();
        bc.setCluster(newCluster);
        bc.count = this.count;
        return bc;
    }

    public void setValueAt(int row, int col, boolean value) {
        cluster[row][col] = value;
        count += (value)? 1 : 0;
    }

    public boolean getValueAt(int rowIndex, int columnIndex) {
        return cluster[rowIndex][columnIndex];
    }

    public void addBlip(int rowIndex, int columnIndex) {
        cluster[rowIndex][columnIndex] = true;
        count++;
    }

    public void removeBlip(int rowIndex, int columnIndex) {
        cluster[rowIndex][columnIndex] = false;
        count--;
    }

    public byte getTotalBlips() {
        return count;
    }

    /**
     * @return the cluster
     */
    public boolean[][] getCluster() {
        return cluster;
    }

    /**
     * @param cluster the cluster to set
     */
    public void setCluster(boolean[][] cluster) {
        this.cluster = cluster;
    }
}
