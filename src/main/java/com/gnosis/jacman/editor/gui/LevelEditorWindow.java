/**
 *
 */
package com.gnosis.jacman.editor.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;

import java.io.*;

import com.gnosis.jacman.engine.*;

/**
 * @author Brendan McGloin
 *
 */
@SuppressWarnings("serial")
public class LevelEditorWindow extends JFrame implements Constants {

    private LevelEditorCanvas canvas;
    private JFileChooser chooser;
    private String gamePath;

    public LevelEditorWindow() {
        super("JacMan Level Editor");
        PropertiesLoader.load();
        PLAYER_IMAGE_CACHE.loadImages();
        TILE_IMAGE_CACHE.loadImages();
        ENEMY_IMAGE_CACHE.loadImages();
        OTHER_IMAGE_CACHE.loadImages();
        this.setLayout(new BorderLayout());
        this.setJMenuBar(new MainMenuBar());
        chooser = new JFileChooser(new File("."+SEPARATOR+"Games"));
        chooser.setFileFilter(new FileFilters.GameFileFilter());
        chooser.setMultiSelectionEnabled(false);
        canvas = null;
        this.addWindowListener(new WEL(this));
        new StartDialog(this);
        if (canvas == null) {
            System.exit(0);
        }
        this.add(canvas);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        canvas.repaint();
    }

    private void newGame(int rows, int cols) {
        if (canvas == null) {
            canvas = new LevelEditorCanvas(rows, cols, this);
        } else {
            canvas.newGame(rows, cols);
        }

        gamePath = null;
        this.add(canvas, BorderLayout.CENTER);
        this.pack();
    }

    private void loadGame(File file) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            try {
                Game game = (Game) in.readObject();
                gamePath = file.getPath();
                if (canvas == null) {
                    canvas = new LevelEditorCanvas(game, this);
                } else {
                    canvas.setGame(game);
                }

                this.pack();
                this.repaint();
            } finally {
                in.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean showOverwriteDialog(File file) {
        //we only need to show the overwrite dialog if the file already exists
        if (file.exists()) {
            //if the game path is null, then we definately need to show it
            if (gamePath == null) {
                return (JOptionPane.showConfirmDialog(this, "Overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION);
            }
            //else, we need to check if the game path is equal to the file path
            else if(!gamePath.equals(file.getPath())) {
                //it's not, so we show the dialog
                return (JOptionPane.showConfirmDialog(this, "Overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION);
            }
        }
        return true;
    }

    private void saveGame(File file) {
        Game game = canvas.makeGame();
        if (game == null) {
            return;
        }
        if (!file.getPath().endsWith(".jmg")) {
            file = new File(file.getPath() + ".jmg");
        }
        if (!showOverwriteDialog(file)) {
            return;
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            try {
                out.writeObject(game);
                gamePath = file.getPath();
            } finally {
                out.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBoard(File file) {
        if (canvas.checkBoardForSave()) {
            if (!file.getPath().endsWith(".jmb")) {
                file = new File(file.getPath()+".jmb");
            }
            if (!showOverwriteDialog(file)) {
                return;
            }
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                try {
                    out.writeObject(canvas.getBoard());
                } finally {
                    out.close();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadBoard(File file) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            try {
                canvas.setBoard((Board)in.readObject());
                gamePath = file.getPath();
                this.pack();
                this.repaint();
            } finally {
                in.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class WEL extends WindowAdapter {
        LevelEditorWindow parent;

        WEL(LevelEditorWindow owner) {
            this.parent = owner;
        }

        public void windowClosing(WindowEvent e) {
            if ((gamePath == null)&&
                    (JOptionPane.showConfirmDialog(parent, "Do you want to save?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    && (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)) {
                saveGame(chooser.getSelectedFile());
            }
        }
    }

    private class NewBoardDialog extends JDialog {

        NewBoardDialog thisDialog;
        SpinnerNumberModel rowSNM, colSNM;

        NewBoardDialog(Frame owner) {
            super(owner, "New Game", true);
            this.thisDialog = this;
            this.setLayout(new BorderLayout());

            this.add(makeMainPanel(), BorderLayout.CENTER);
            this.add(makeButtonPanel(), BorderLayout.SOUTH);

            this.pack();
            this.setVisible(true);
        }

        NewBoardDialog(Dialog owner) {
            super(owner, "New Game", true);
            this.thisDialog = this;
            this.setLayout(new BorderLayout());

            this.add(makeMainPanel(), BorderLayout.CENTER);
            this.add(makeButtonPanel(), BorderLayout.SOUTH);

            this.pack();
            this.setVisible(true);
        }

        JPanel makeMainPanel() {
            JPanel panel = new JPanel(new GridBagLayout());

            rowSNM = new SpinnerNumberModel(5, 3, 15, 1);

            colSNM = new SpinnerNumberModel(5, 3, 15, 1);

            GridBagConstraints rslConstraints = new GridBagConstraints();
            rslConstraints.gridx = 0;
            rslConstraints.gridy = 0;

            GridBagConstraints rsConstraints = new GridBagConstraints();
            rsConstraints.gridx = 1;
            rsConstraints.gridy = 0;

            GridBagConstraints cslConstraints = new GridBagConstraints();
            cslConstraints.gridx = 0;
            cslConstraints.gridy = 1;

            GridBagConstraints csConstraints = new GridBagConstraints();
            csConstraints.gridx = 1;
            csConstraints.gridy = 1;

            panel.add(new JLabel("Rows"), rslConstraints);
            panel.add(new JSpinner(rowSNM), rsConstraints);
            panel.add(new JLabel("Columns"), cslConstraints);
            panel.add(new JSpinner(colSNM), csConstraints);

            return panel;
        }

        JPanel makeButtonPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    newGame(rowSNM.getNumber().intValue(), colSNM.getNumber().intValue());
                    thisDialog.dispose();
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    thisDialog.dispose();
                }
            });

            panel.add(okButton);
            panel.add(cancelButton);

            return panel;
        }
    }

    private class StartDialog extends JDialog {

        Frame parent;
        StartDialog thisDialog;

        public StartDialog(Frame owner) {
            super(owner, true);
            this.parent = owner;
            this.thisDialog = this;
            this.setLayout(new GridLayout(3, 1, 5, 2));

            JButton newButton = new JButton("New Level");
            newButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new NewBoardDialog(thisDialog);
                    if (canvas != null) {
                        thisDialog.dispose();
                    }
                }
            });

            JButton loadButton = new JButton("Open Level");
            loadButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        loadGame(chooser.getSelectedFile());
                        dispose();
                    }
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            this.add(newButton);
            this.add(loadButton);
            this.add(cancelButton);
            this.pack();
            this.setVisible(true);
        }
    }

    private class MainMenuBar extends JMenuBar {

        LevelEditorWindow parent;
        JMenuItem undo, redo;
        JMenu editMenu;

        public MainMenuBar() {
            this.add(makeFileMenu());
            editMenu = makeEditMenu();
            this.add(editMenu);
            editMenu.addMenuListener(new MenuListener() {

                public void menuSelected(MenuEvent e) {
                    redo.setEnabled(canvas.getUndoHandler().canRedo());
                    undo.setEnabled(canvas.getUndoHandler().canUndo());
                }

                public void menuDeselected(MenuEvent e) {
                }

                public void menuCanceled(MenuEvent e) {
                }
            });
        }

        JMenu makeFileMenu() {
            JMenu menu = new JMenu("File");

            JMenuItem newGame = new JMenuItem("New");
            newGame.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new NewBoardDialog(parent);
                }
            });
            newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

            JMenuItem loadGame = new JMenuItem("Open Game");
            loadGame.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        loadGame(chooser.getSelectedFile());
                    }
                }
            });
            loadGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

            JMenuItem openBoard = new JMenuItem("Open Board");
            openBoard.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    FileFilter filter = chooser.getFileFilter();
                    chooser.setFileFilter(new FileFilters.BoardFileFilter());
                    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        loadBoard(chooser.getSelectedFile());
                    }
                    chooser.setFileFilter(filter);
                }
            });

            JMenuItem saveGame = new JMenuItem("Save Game");
            saveGame.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if ((gamePath == null)&&
                            (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)) {
                        saveGame(chooser.getSelectedFile());
                    } else if ((!gamePath.endsWith(".jmg"))&&
                               (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)) {
                        saveGame(chooser.getSelectedFile());
                    } else {
                        saveGame(new File(gamePath));
                    }
                }
            });
            saveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

            JMenuItem saveBoard = new JMenuItem("Save Board");
            saveBoard.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if ((gamePath != null)&&(gamePath.endsWith(".jmb"))) {
                        saveBoard(new File(gamePath));
                    } else {
                        FileFilter filter = chooser.getFileFilter();
                        chooser.setFileFilter(new FileFilters.BoardFileFilter());
                        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                            saveBoard(chooser.getSelectedFile());
                        }
                        chooser.setFileFilter(filter);
                    }
                }
            });
            saveBoard.setToolTipText("Saves the board. Boards cannot be played, but can be edited.");

            JMenuItem saveGameAs = new JMenuItem("Save Game as...");
            saveGameAs.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        saveGame(chooser.getSelectedFile());
                    }
                }
            });
            saveGameAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));

            JMenuItem saveBoardAs = new JMenuItem("Save Board as...");
            saveBoardAs.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                        saveBoard(chooser.getSelectedFile());
                    }
                }
            });
            saveBoard.setToolTipText("Saves the board. Boards cannot be played, but can be edited.");

            JMenuItem quit = new JMenuItem("Quit");
            quit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if ((gamePath == null)&&
                            (JOptionPane.showConfirmDialog(parent, "Do you want to save?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                            && (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)) {
                        saveGame(chooser.getSelectedFile());
                    }
                    parent.dispose();
                    System.exit(0);
                }
            });
            quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

            menu.add(newGame);
            menu.add(loadGame);
            menu.add(openBoard);
            menu.addSeparator();
            menu.add(saveGame);
            menu.add(saveGameAs);
            menu.addSeparator();
            menu.add(saveBoard);
            menu.add(saveBoardAs);
            menu.addSeparator();
            menu.add(quit);

            return menu;
        }

        JMenu makeEditMenu() {
            JMenu menu = new JMenu("Edit");

            undo = new JMenuItem("Undo");
            undo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.getUndoHandler().undo();
                    undo.setEnabled(canvas.getUndoHandler().canUndo());
                }
            });
            undo.setEnabled(false);
            undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));

            redo = new JMenuItem("Redo");
            redo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.getUndoHandler().redo();
                    redo.setEnabled(canvas.getUndoHandler().canRedo());
                }
            });
            redo.setEnabled(false);
            redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));

            menu.add(undo);
            menu.add(redo);

            return menu;
        }
    }

    public static void main(String[] args) {
        new LevelEditorWindow();
    }
}
