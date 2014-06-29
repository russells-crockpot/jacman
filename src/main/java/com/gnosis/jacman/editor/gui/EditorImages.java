/**
 *
 */
package com.gnosis.jacman.editor.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.gnosis.jacman.engine.Constants;

/**
 * @author Brendan McGloin
 *
 */
public final class EditorImages implements Constants {

    public static final BufferedImage R = loadR();

    public static final BufferedImage S = loadS();

    private static BufferedImage loadR() {
        try {
            return ImageIO.read(new File("."+SEPARATOR+"Images"+SEPARATOR+"R.gif"));
        } catch (IOException e) {
            System.out.println("Called1");
            System.err.println(e.getMessage());
            return null;
        }
    }
    private static BufferedImage loadS() {
        try {
            return ImageIO.read(new File("."+SEPARATOR+"Images"+SEPARATOR+"S.gif"));
        } catch (IOException e) {
            System.out.println("Called2");
            System.err.println(e.getMessage());
            return null;
        }
    }
}
