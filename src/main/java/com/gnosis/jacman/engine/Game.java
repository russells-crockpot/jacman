package com.gnosis.jacman.engine;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.gnosis.jacman.ann.engine.*;

/**
 * This contains all of the data for a particular game.
 * Specifically:
 * - The board
 * - The enemies
 * - The artificial neural network for this game
 *
 * @author Brendan McGloin
 */
public class Game implements Serializable, Constants {

    static final long serialVersionUID = 0x534268af;

    private Board board;
    private transient Player player;
    private Enemy[] enemies;
    private ANN net;
    private transient List<TestCase> cases = new ArrayList<TestCase>();
    private transient SetSizeQueue<TestCase> previousConditions = new SetSizeQueue<TestCase>(2);
    private transient int steps = 0;

    protected Game() {
        this.player = new Player();
        player.setCenter(board.getPlayerStartPoint());
        divyUpNet();
    }

    public Game(Board board, Enemy[] enemies, ANN net) {
        super();
        this.board = board;
        this.enemies = enemies;
        this.net = net;
        this.player = new Player();
        player.setCenter(board.getPlayerStartPoint());
        divyUpNet();
    }

    public Test makeTest(int epochs) {
        return new Test(cases, net, epochs);
    }

    public void setSetSizeQueue() {
        previousConditions = new SetSizeQueue<TestCase>(Globals.lookAhead);
    }

    public void playerKilled() {
        //increment times the player was eaten
        Globals.timesPlayerWasEaten++;
        //if it's not in training mode...
        if (Globals.trainingMode == TRAINING_MODE_OFF) {
            //...then there's nothing more to do
            return;
        }
        //Check to see if previousConditions has been initialized
        if (previousConditions == null) {
            //it isn't, so we initialize it
            previousConditions = new SetSizeQueue<TestCase>(Globals.lookAhead);
        }
        //do the same with cases
        if (cases == null) {
            cases = new ArrayList<TestCase>();
        }
        //get the previous conditions
        TestCase tc = previousConditions.getFirst();
        //check to see if the queue wasn't full
        if (tc == null) {
            //it wasn't, so we ignore the event and leave
            return;
        }
        tc.setLr(getLearningRate(PLAYER_KILLED_EVENT));
        if (Globals.trainingMode == TRAINING_MODE_AS_YOU_GO) {
            tc.apply(net);
        } else if (Globals.trainingMode == TRAINING_MODE_AFTER) {
            cases.add(tc);
        } else {
            //only other possibility is that training mode is off, so we just return
            return;
        }
    }

    public void powerBlipEaten() {
        Globals.timesPowerBlipWasEaten++;
        if (Globals.trainingMode == TRAINING_MODE_OFF) {
            return;
        }
        if (previousConditions == null) {
            previousConditions = new SetSizeQueue<TestCase>(2);
        }
        if (cases == null) {
            cases = new ArrayList<TestCase>();
        }
        //get the previous conditions
        TestCase tc = previousConditions.getFirst();
        //check to see if the queue wasn't full
        if (tc == null) {
            //it wasn't, so we ignore the event and leave
            return;
        }
        tc.setLr(getLearningRate(POWER_BLIP_EATEN_EVENT));
        reverseAll(tc.getOutputs());
        if (Globals.trainingMode == TRAINING_MODE_AS_YOU_GO) {
            tc.apply(net);
        } else if (Globals.trainingMode == TRAINING_MODE_AFTER) {
            cases.add(tc);
        } else {
            //only other possibility is that training mode is off, so we just return
            return;
        }
    }


    public void enemyRegenerated(Enemy enemy) {
        Globals.timesEnemyRegenerated++;
        if (Globals.trainingMode == TRAINING_MODE_OFF) {
            return;
        }
        if (previousConditions == null) {
            previousConditions = new SetSizeQueue<TestCase>(2);
        }
        if (cases == null) {
            cases = new ArrayList<TestCase>();
        }
        //get the previous conditions
        TestCase tc = previousConditions.getFirst();
        //check to see if the queue wasn't full
        if (tc == null) {
            //it wasn't, so we ignore the event and leave
            return;
        }

        int enemyIndex = -1;
        //find the enemy in enemies
        for (int i = 0; i < enemies.length; i++) {
            if (enemy == enemies[i]) {
                enemyIndex = i;
                break;
            }
        }
        //check to make sure that the enemy was found
        if (enemyIndex == -1) {
            //it wasn't, so we should log it and the return
            System.err.println("Enemy not found in method enemyRegenerated(Enemy enemy)");
            return;
        }
        //reorder the outputs appropriately, and put it in the test case
        double[] outs = getSubArray(tc.getOutputs(), enemyIndex*4, (enemyIndex*4)+4);

        NerveClusterTestCase nctc = new NerveClusterTestCase(tc.getInputs(), outs,
                tc.getPreviousContextLayerActs(), getLearningRate(ENEMY_REGENERATED_EVENT),
                enemyIndex);
        if (Globals.trainingMode == TRAINING_MODE_AS_YOU_GO) {
            nctc.apply(net);
        } else if (Globals.trainingMode == TRAINING_MODE_AFTER) {
            cases.add(nctc);
        } else {
            //only other possibility is that training mode is off, so we just return
            return;
        }
    }

    public void enemyKilled(Enemy enemy) {
        Globals.timesEnemyWasEaten++;
        if (Globals.trainingMode == TRAINING_MODE_OFF) {
            return;
        }
        if (previousConditions == null) {
            previousConditions = new SetSizeQueue<TestCase>(2);
        }
        if (cases == null) {
            cases = new ArrayList<TestCase>();
        }
        //get the previous conditions
        TestCase tc = previousConditions.getFirst();
        //check to see if the queue wasn't full
        if (tc == null) {
            //it wasn't, so we ignore the event and leave
            return;
        }
        int enemyIndex = -1;
        //find the enemy in enemies
        for (int i = 0; i < enemies.length; i++) {
            if (enemy == enemies[i]) {
                enemyIndex = i;
                break;
            }
        }
        //check to make sure that the enemy was found
        if (enemyIndex == -1) {
            //it wasn't, so we should log it and the return
            System.err.println("Enemy not found in method enemyKilled(Enemy enemy)");
            return;
        }
        //reorder the outputs appropriately, and put it in the test case
        //adjust the lr accordingly
        tc.setLr(getLearningRate(ENEMY_KILLED_EVENT));
        if (Globals.trainingMode == TRAINING_MODE_AS_YOU_GO) {
            tc.apply(net);
        } else if (Globals.trainingMode == TRAINING_MODE_AFTER) {
            cases.add(tc);
        } else {
            //only other possibility is that training mode is off, so we just return
            return;
        }
    }

    private static double getLearningRate(int event) {
        double lr = -1;

        if (event == PLAYER_KILLED_EVENT) {
            return BIG_LR;
        } else if (event == ENEMY_KILLED_EVENT) {
            return BIG_LR * ENEMY_EATEN_EVENT_MOD;
        } else if (event == ENEMY_REGENERATED_EVENT) {
            return LITTLE_LR;
        } else if (event == POWER_BLIP_EATEN_EVENT) {
            return LITTLE_LR * POWER_BLIP_EATEN_EVENT_MOD;
        }

        if ((event & BIG_LR_EVENT) > 0) {
            lr = BIG_LR;
        } else if ((event & LITTLE_LR_EVENT) > 0) {
            lr = LITTLE_LR;
        } else {
            //there has been an error, and we should notify the logger, and return
            System.err.println("Unkown type (Big v. Little) of ANN event in getLearningRate(int event)");
            return -1;
        }

        if ((event & GOOD_EVENT) > 0) {
            lr *= GOOD_EVENT_LR_MODIFIER;
        } else if ((event & BAD_EVENT) > 0) {
            lr *= BAD_EVENT_LR_MODIFIER;
        } else {
            //there has been an error, and we should notify the logger, and return
            System.err.println("Unkown type (Good v. Bad) of ANN event in getLearningRate(int event)");
            return -1;
        }

        return lr;
    }



    private static double[] getSubArray(double[] a, int start, int end) {
        double[] t = new double[end-start];

        for (int i = 0, c = start; c < end; i++, c++) {
            t[i] = a[c];
        }

        return t;
    }

    private void reverseAll(double[] a) {
        for (int i = 0; i < enemies.length; i++) {
            reverse(a, i);
        }
    }

    private static void reverse(double[] a, int enemyIndex) {
        int index = enemyIndex*4;
        //get the outputs for the enemy
        double[] enemyOuts = getSubArray(a, index, index+4);

        for (int i = 0, c = index+3; i < enemyOuts.length; i++, c--) {
            a[c] = enemyOuts[i];
        }
    }

    /**
     * @return the board
     */
    public Board getBoard() {
        return board;
    }
    /**
     * @param board the board to set
     */
    public void setBoard(Board board) {
        this.board = board;
    }
    /**
     * @return the enemies
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
     * @return the net
     */
    public ANN getNet() {
        return net;
    }
    /**
     * @param net the net to set
     */
    public void setNet(ANN net) {
        this.net = net;
        divyUpNet();
    }

    public void divyUpNet() {
        if (player == null) {
            this.player = new Player();
            player.setCenter(board.getPlayerStartPoint());
        }
        NerveCluster[] in = net.getInputLayer().getClusters();
        NerveCluster[] out = net.getOutputLayer().getClusters();
        int c = 0;
        player.setNerveCluster(in[c]);
        c++;
        for (int i = 0; i < enemies.length; i++, c++) {
            enemies[i].setInCluster(in[c]);
            enemies[i].setOutCluster(out[i]);
        }
        Neuron[] powerBlipNeurons = in[c].getCluster();
        for (int i = 0; i < powerBlipNeurons.length; i++) {
            board.getPowerBlips().get(i).setNeuron(powerBlipNeurons[i]);
        }
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

    public void step() {
        steps++;
        if (steps >= MOVES_UNTIL_NEW_TILE) {
            if (previousConditions == null) {
                previousConditions = new SetSizeQueue<TestCase>(2);
            }
            steps = 0;
            double[] ins = net.getInputLayer().getActivations();
            double[] outs = net.getOutputLayer().getActivations();
            double[] context = net.getMiddleLayer().getContextLayerPreviousActs();
            previousConditions.enque(new TestCase(ins, outs, context));
        }
    }
}
