/**
 *
 */
package com.gnosis.jacman.ann.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brendan McGloin
 *
 */
public class Test implements Runnable {

    private List<TestCase> cases;
    private ANN net;
    private int epochs;

    public Test(ANN net, int epochs) {
        this(new ArrayList<TestCase>(), net, epochs);
    }

    /**
     * @param cases
     */
    public Test(List<TestCase> cases, ANN net, int epochs) {
        this.cases = cases;
        this.net = net;
        this.epochs = epochs;
    }



    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        int counter = 0;
        while ((!Thread.interrupted()) && (counter <= epochs)) {
            for (TestCase tc: cases) {
                tc.apply(net);
            }
            net.resetContextLayers();
            counter++;
        }
    }

    public void addTestCase(TestCase tc) {
        cases.add(tc);
    }

    public List<TestCase> getCases() {
        return cases;
    }

    public void setCases(List<TestCase> cases) {
        this.cases = cases;
    }

    public ANN getNet() {
        return net;
    }

    public void setNet(ANN net) {
        this.net = net;
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }
}
