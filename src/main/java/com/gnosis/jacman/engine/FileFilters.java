/**
 *
 */
package com.gnosis.jacman.engine;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * @author Brendan McGloin
 *
 */
public final class FileFilters {

    public static class GameFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.getPath().endsWith(".jmg") || f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "(.jmg) JacMan Game File";
        }

    }

    public static class BoardFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.getPath().endsWith(".jmb")||f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "JacMan Board (.jmb)";
        }

    }
}
