/**
 *
 */
package com.gnosis.jacman.editor.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.gnosis.jacman.ann.ANNTools;
import com.gnosis.jacman.ann.engine.ANN;

import com.gnosis.jacman.editor.BoardFactory;
import com.gnosis.jacman.engine.*;
import com.gnosis.jacman.engine.items.NoItem;
import com.gnosis.jacman.engine.items.PowerBlip;
import com.gnosis.jacman.graphics.GamePainters;

/**
 * @author Brendan McGloin
 *
 */
@SuppressWarnings("serial")
public class LevelEditorCanvas extends Canvas implements Constants {

    public static final String PATH_PREFIX = "."+SEPARATOR+"Images"+SEPARATOR;
    public static final String IMAGE_1_PATH_SUFFIX = " ghost 1.gif";
    public static final String IMAGE_2_PATH_SUFFIX = " ghost 2.gif";

    public static final String[] ENEMY_COLORS = {PURPLE, CYAN, ORANGE, PINK, RED, WHITE, YELLOW, GREEN};


    private Board board;
    private LevelEditorWindow parent;
    private ArrayList<Enemy> enemies;
    private JPopupMenu tileMenu, enemyMenu;
    private boolean startPointSet, regenDoorSet;
    private int popUpX, popUpY;
    private BoardUndoHandler undoHandler;

    /**
     * @param board
     */
    public LevelEditorCanvas(Game game, LevelEditorWindow parent) {
        this.board = game.getBoard();
        this.parent = parent;
        enemies = new ArrayList<Enemy>();
        for (Enemy enemy: game.getEnemies()) {
            enemies.add(enemy);
            enemy.reset();
        }
        this.addMouseListener(new MA(this));
        setupPopupMenus();
        startPointSet = true;
        regenDoorSet = true;
        popUpX = 0;
        popUpY = 0;
        undoHandler = new BoardUndoHandler();
        board.reset();
        this.setPreferredSize(new Dimension(board.getColumns()*TILE_WIDTH, board.getRows()*TILE_HEIGHT));
    }

    /**
     * @param board
     * @param parent
     */
    public LevelEditorCanvas(int rows, int cols, LevelEditorWindow parent) {
        this.board = BoardFactory.makeBlankBoard(rows, cols);
        this.parent = parent;
        enemies = new ArrayList<Enemy>();
        this.addMouseListener(new MA(this));
        setupPopupMenus();
        startPointSet = false;
        regenDoorSet = false;
        popUpX = 0;
        popUpY = 0;
        undoHandler = new BoardUndoHandler();
        this.setPreferredSize(new Dimension(board.getColumns()*TILE_WIDTH, board.getRows()*TILE_HEIGHT));
    }

    public void newGame(int rows, int cols) {
        this.board = BoardFactory.makeBlankBoard(rows, cols);
        enemies = new ArrayList<Enemy>();
        startPointSet = false;
        regenDoorSet = false;
        this.setPreferredSize(new Dimension(board.getColumns()*TILE_WIDTH, board.getRows()*TILE_HEIGHT));
    }

    public void setGame(Game game) {
        this.board = game.getBoard();
        enemies = new ArrayList<Enemy>();
        for (Enemy enemy: game.getEnemies()) {
            enemies.add(enemy);
            enemy.reset();
        }
        startPointSet = true;
        regenDoorSet = true;
        board.reset();
        this.setPreferredSize(new Dimension(board.getColumns()*TILE_WIDTH, board.getRows()*TILE_HEIGHT));
        repaint();
    }

    private void setupPopupMenus() {
        //---------------------------Tile Menu-----------------------------//
        tileMenu = new TilePopupMenu(this);

        //---------------------------Enemy Menu----------------------------//
        enemyMenu = new JPopupMenu();

        JMenuItem editEnemy = new JMenuItem("Edit Enemy");
        editEnemy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = popUpY/TILE_HEIGHT;
                int col = popUpX/TILE_WIDTH;
                Iterator<Enemy> iter = enemies.iterator();
                while (iter.hasNext()) {
                    Enemy enemy = iter.next();
                    if ((enemy.getStartingRow() == row)&&
                            (enemy.getStartingCol() == col)) {
                        new EnemyEditorDialog(enemy, board.getRows(), board.getColumns(), parent);
                        break;
                    }
                }
            }
        });

        JMenuItem removeEnemy = new JMenuItem("Remove Enemy");
        removeEnemy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = popUpY/TILE_HEIGHT;
                int col = popUpX/TILE_WIDTH;
                Iterator<Enemy> iter = enemies.iterator();
                while (iter.hasNext()) {
                    Enemy enemy = iter.next();
                    if ((enemy.getStartingRow() == row)&&
                            (enemy.getStartingCol() == col)) {
                        iter.remove();
                        break;
                    }
                }
            }
        });

        enemyMenu.add(editEnemy);
        enemyMenu.add(removeEnemy);
        enemyMenu.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    public Game makeGame() {
        if (!startPointSet) {
            JOptionPane.showMessageDialog(parent, "Player start point has not been set!",
                                          "Unable to proceed", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!regenDoorSet) {
            JOptionPane.showMessageDialog(parent, "Regen door has not been set!",
                                          "Unable to proceed", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        for(int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                if (board.getTile(row, col).getPlayerMoveOptions() == EMPTY) {
                    JOptionPane.showMessageDialog(parent, "Tile at row "+(row+1)+" and column "+(col+1)+" has no walls!",
                                                  "Unable To proceed", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
            }
        }
        board.reBlip();
        ANN net = ANNTools.makeInitialANN2(board, enemies.size(), 0.8);
        Enemy[] enemies = new Enemy[this.enemies.size()];
        this.enemies.toArray(enemies);
        return new Game(board, enemies, net);
    }

    private class TilePopupMenu extends JPopupMenu {
        JMenuItem ns, ew, ne, nw, se, sw;
        JMenuItem nse, nsw, nEw, sew;
        JMenuItem nsew;
        int X, Y;
        LevelEditorCanvas canvas;

        public TilePopupMenu(LevelEditorCanvas theCanvas) {
            this.canvas = theCanvas;
            JMenuItem editTile = new JMenuItem("Edit Tile");
            editTile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    new TileEditorDialog(board.getTile(row, col), row, col, canvas);
                }
            });

            JMenu setRegenDoor = new JMenu("Set Regen Door");

            JMenuItem srdNorth = new JMenuItem("North");
            srdNorth.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    board.setRegenDoor(row, col, NORTH);
                    regenDoorSet = true;
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    undoHandler.addEdit(new RegenDoorSetEvent(row, col, NORTH));
                }
            });
            JMenuItem srdSouth = new JMenuItem("South");
            srdSouth.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    board.setRegenDoor(row, col, SOUTH);
                    regenDoorSet = true;
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    undoHandler.addEdit(new RegenDoorSetEvent(row, col, SOUTH));
                }
            });
            JMenuItem srdEast = new JMenuItem("East");
            srdEast.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    board.setRegenDoor(row, col, EAST);
                    regenDoorSet = true;
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    undoHandler.addEdit(new RegenDoorSetEvent(row, col, EAST));
                }
            });
            JMenuItem srdWest = new JMenuItem("West");
            srdWest.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    board.setRegenDoor(row, col, WEST);
                    regenDoorSet = true;
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    undoHandler.addEdit(new RegenDoorSetEvent(row, col, WEST));
                }
            });
            setRegenDoor.add(srdNorth);
            setRegenDoor.add(srdSouth);
            setRegenDoor.add(srdEast);
            setRegenDoor.add(srdWest);

            JMenu moveOptions = new JMenu("Move Options");
            moveOptions.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });

            JMenu twoOptions = new JMenu("Two");
            ns = new JMenuItem("NS");
            ns.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(NORTH|SOUTH);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            ew = new JMenuItem("EW");
            ew.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(EAST|WEST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            ne = new JMenuItem("NE");
            ne.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(NORTH|EAST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            nw = new JMenuItem("NW");
            nw.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(NORTH|WEST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            se = new JMenuItem("SE");
            se.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(SOUTH|EAST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            sw = new JMenuItem("SW");
            sw.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(SOUTH|WEST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            twoOptions.add(ns);
            twoOptions.add(ew);
            twoOptions.add(ne);
            twoOptions.add(nw);
            twoOptions.add(se);
            twoOptions.add(sw);

            JMenu threeOptions = new JMenu("Three");
            nse = new JMenuItem("NSE");
            nse.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(NORTH|SOUTH|EAST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            nsw = new JMenuItem("NSW");
            nsw.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(NORTH|SOUTH|WEST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            nEw = new JMenuItem("NEW");
            nEw.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(NORTH|EAST|WEST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            sew = new JMenuItem("SEW");
            sew.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(SOUTH|EAST|WEST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });
            threeOptions.add(nse);
            threeOptions.add(nsw);
            threeOptions.add(nEw);
            threeOptions.add(sew);

            nsew = new JMenuItem("NSEW");
            nsew.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    undoHandler.addEdit(new TileDirectionsChangedEvent(board.getTile(row, col).getPlayerMoveOptions(),
                                        board.getTile(row, col).getEnemyMoveOptions(), row, col));
                    board.getTile(row, col).setMoveOptions(NORTH|SOUTH|EAST|WEST);
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                    repaint();
                }
            });

            moveOptions.add(twoOptions);
            moveOptions.add(threeOptions);
            moveOptions.add(nsew);

            JMenuItem addEnemy = new JMenuItem("Add Enemy");
            addEnemy.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    if (board.getTile(row, col).getPlayerMoveOptions() != EMPTY) {
                        new EnemyEditorDialog(board.getRows(), board.getColumns(), row+1, col+1, parent);
                    } else {
                        new EnemyEditorDialog(board.getRows(), board.getColumns(), 1, 1, parent);
                    }
                }
            });

            JMenuItem startingPoint = new JMenuItem("Set player start point");
            startingPoint.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    board.setPlayerStartPoint(row, col);
                    undoHandler.addEdit(new StartPointSetEvent(row, col));
                    startPointSet = true;
                }
            });

            this.add(editTile);
            this.add(moveOptions);
            this.addSeparator();
            this.add(addEnemy);
            this.addSeparator();
            this.add(setRegenDoor);
            this.add(startingPoint);

            this.addPopupMenuListener(new PopupMenuListener() {

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    if (row == 0) {
                        //disable all that have north as an option
                        ns.setEnabled(false);
                        ne.setEnabled(false);
                        nw.setEnabled(false);
                        nse.setEnabled(false);
                        nsw.setEnabled(false);
                        nEw.setEnabled(false);
                        nsew.setEnabled(false);
                    }
                    if (row == board.getRows()-1) {
                        //disable all that have south as an option
                        ns.setEnabled(false);
                        se.setEnabled(false);
                        sw.setEnabled(false);
                        nse.setEnabled(false);
                        nsw.setEnabled(false);
                        sew.setEnabled(false);
                        nsew.setEnabled(false);
                    }
                    if (col == 0) {
                        //disable all west
                        ew.setEnabled(false);
                        nw.setEnabled(false);
                        sw.setEnabled(false);
                        nsw.setEnabled(false);
                        nEw.setEnabled(false);
                        sew.setEnabled(false);
                        nsew.setEnabled(false);
                    }
                    if (col == board.getColumns()-1) {
                        //disable all east
                        ew.setEnabled(false);
                        ne.setEnabled(false);
                        se.setEnabled(false);
                        nse.setEnabled(false);
                        nEw.setEnabled(false);
                        sew.setEnabled(false);
                        nsew.setEnabled(false);
                    }
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    ns.setEnabled(true);
                    ew.setEnabled(true);
                    ne.setEnabled(true);
                    nw.setEnabled(true);
                    se.setEnabled(true);
                    sw.setEnabled(true);

                    nse.setEnabled(true);
                    nsw.setEnabled(true);
                    nEw.setEnabled(true);
                    sew.setEnabled(true);

                    nsew.setEnabled(true);

                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();

                    canvas.repaint(200);
                }

                public void popupMenuCanceled(PopupMenuEvent e) {
                    int row = Y/TILE_HEIGHT;
                    int col = X/TILE_WIDTH;
                    board.getTile(row, col).createInitialBlips();
                    board.getTile(row, col).makeBlips();
                }
            });
        }



        @Override
        public void setVisible(boolean b) {
            super.setVisible(b);
        }



        @Override
        public void show(Component invoker, int x, int y) {
            X = x;
            Y = y;
            super.show(invoker, x, y);
        }


    }

    @Override
    public void paint(Graphics g) {
        GamePainters.drawGridBoard(board, g);
        GamePainters.drawEnemies(enemies, g, this);
        if(startPointSet) {
            int x = board.getPlayerStartPoint().x-8;
            int y = board.getPlayerStartPoint().y-8;
            g.drawImage(EditorImages.S, x, y, this);
        }
        if(regenDoorSet) {
            int x = board.getRegenDoor().x-8;
            int y = board.getRegenDoor().y-8;
            g.drawImage(EditorImages.R, x, y, this);
        }
    }

    @Override
    public void update(Graphics g) {
        GamePainters.drawGridBoard(board, g);
        GamePainters.drawEnemies(enemies, g, this);
        if(startPointSet) {
            int x = board.getPlayerStartPoint().x-8;
            int y = board.getPlayerStartPoint().y-8;
            g.drawImage(EditorImages.S, x, y, this);
        }
        if(regenDoorSet) {
            int x = board.getRegenDoor().x-8;
            int y = board.getRegenDoor().y-8;
            g.drawImage(EditorImages.R, x, y, this);
        }
    }

    private class EnemyEditorDialog extends JDialog {
        private JComboBox color;
        private JSlider pursuitChance;
        int maxRows, maxCols, row, col;
        SpinnerNumberModel startingRowSNM, startingColSNM;

        public EnemyEditorDialog(Enemy enemy, int rows, int cols, JFrame owner) {
            super(owner, "Edit Enemy");
            this.maxRows = rows;
            this.maxCols = cols;
            this.row = enemy.getStartingRow()+1;
            this.col = enemy.getStartingCol()+1;

            this.setLayout(new GridBagLayout());

            GridBagConstraints centerPanelConstraints = new GridBagConstraints();
            centerPanelConstraints.gridx = 0;
            centerPanelConstraints.gridy = 0;

            GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
            buttonPanelConstraints.gridx = 0;
            buttonPanelConstraints.gridy = 1;
            buttonPanelConstraints.anchor = GridBagConstraints.SOUTHEAST;

            this.add(makeCenterPanel(), centerPanelConstraints);
            this.add(makeButtonPanel(), buttonPanelConstraints);
            for (int i = 0; i < ENEMY_COLORS.length; i++) {
                if (enemy.getColor().equals(color.getItemAt(i))) {
                    color.setSelectedIndex(i);
                    break;
                }
            }
            this.pack();
            this.setVisible(true);
        }

        public EnemyEditorDialog(int rows, int cols, int row, int col, JFrame owner) {
            super(owner, "New Enemy");
            this.maxRows = rows;
            this.maxCols = cols;
            this.row = row;
            this.col = col;

            this.setLayout(new GridBagLayout());

            GridBagConstraints centerPanelConstraints = new GridBagConstraints();
            centerPanelConstraints.gridx = 0;
            centerPanelConstraints.gridy = 0;

            GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
            buttonPanelConstraints.gridx = 0;
            buttonPanelConstraints.gridy = 1;
            buttonPanelConstraints.anchor = GridBagConstraints.SOUTHEAST;

            this.add(makeCenterPanel(), centerPanelConstraints);
            this.add(makeButtonPanel(), buttonPanelConstraints);

            this.pack();
            this.setVisible(true);
        }

        private JPanel makeCenterPanel() {
            JPanel panel = new JPanel(new GridBagLayout());

            color = new JComboBox(ENEMY_COLORS);

            pursuitChance = new JSlider(JSlider.HORIZONTAL, 0, ENEMY_CHASE_CHANCE_MAX, 0);
            pursuitChance.setToolTipText("The chance that an enemy will pursue.");

            startingRowSNM = new SpinnerNumberModel(row, 1, maxRows, 1);
            JSpinner rowSpinner = new JSpinner(startingRowSNM);

            startingColSNM = new SpinnerNumberModel(col, 1, maxCols, 1);
            JSpinner colSpinner = new JSpinner(startingColSNM);

            GridBagConstraints clConstraints = new GridBagConstraints();
            clConstraints.gridx = 0;
            clConstraints.gridy = 0;

            GridBagConstraints ccbConstraints = new GridBagConstraints();
            ccbConstraints.gridx = 1;
            ccbConstraints.gridy = 0;

            GridBagConstraints pclConstraints = new GridBagConstraints();
            pclConstraints.gridx = 0;
            pclConstraints.gridy = 1;

            GridBagConstraints pcsConstraints = new GridBagConstraints();
            pcsConstraints.gridx = 1;
            pcsConstraints.gridy = 1;

            GridBagConstraints srlConstraints = new GridBagConstraints();
            srlConstraints.gridx = 0;
            srlConstraints.gridy = 2;

            GridBagConstraints srsConstraints = new GridBagConstraints();
            srsConstraints.gridx = 1;
            srsConstraints.gridy = 2;

            GridBagConstraints sclConstraints = new GridBagConstraints();
            sclConstraints.gridx = 0;
            sclConstraints.gridy = 3;

            GridBagConstraints scsConstraints = new GridBagConstraints();
            scsConstraints.gridx = 1;
            scsConstraints.gridy = 3;

            panel.add(new JLabel("Color"), clConstraints);
            panel.add(color, ccbConstraints);
            panel.add(new JLabel("Pursuit Chance"), pclConstraints);
            panel.add(pursuitChance, pcsConstraints);
            panel.add(new JLabel("Starting Row"), srlConstraints);
            panel.add(rowSpinner, srsConstraints);
            panel.add(new JLabel("Starting Col"), sclConstraints);
            panel.add(colSpinner, scsConstraints);

            return panel;
        }

        Enemy makeEnemy() {
            return new Enemy(pursuitChance.getValue(), (String)color.getSelectedItem(),
                             startingRowSNM.getNumber().intValue()-1, startingColSNM.getNumber().intValue()-1);

        }

        JPanel makeButtonPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enemies.add(makeEnemy());
                    dispose();
                }
            });

            JButton applyButton = new JButton("Apply");
            applyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enemies.add(makeEnemy());
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            panel.add(okButton);
            panel.add(applyButton);
            panel.add(cancelButton);

            return panel;
        }
    }

    private class MA extends MouseAdapter {

        LevelEditorCanvas canvas;

        public MA(LevelEditorCanvas canvas) {
            this.canvas = canvas;
        }

        public void mousePressed(MouseEvent e) {
            if ((e.getY() > board.getRows()*TILE_HEIGHT)||(e.getX() > board.getColumns()*TILE_WIDTH)) {
                return;
            }
            if (e.getButton() == MouseEvent.BUTTON1) {
                if(tileMenu.isVisible()||enemyMenu.isVisible()) {
                    System.out.println("Called");
                    return;
                }
                for (Enemy enemy: enemies) {
                    if (enemy.getRectangle().contains(e.getPoint())) {
                        return;
                    }
                }
                Point point = e.getPoint();
                int row = point.y/TILE_WIDTH;
                int col = point.x/TILE_WIDTH;
                new TileEditorDialog(board.getTile(row, col), row, col, canvas);
                repaint();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                boolean selectionFound = false;
                popUpX = e.getX();
                popUpY = e.getY();
                for (Enemy enemy: enemies) {
                    if (enemy.getRectangle().contains(e.getPoint())) {
                        selectionFound = true;
                        enemyMenu.show(parent, e.getX(), e.getY());
                    }
                }
                if (!selectionFound) {
                    tileMenu.show(parent, e.getX(), e.getY());
                }
            }
        }

    }

    private class TileEditorDialog extends JDialog {

        DirectionsPanel emo, pmo;
        Tile tile;
        BlipsPanel blipsPanel;
        JCheckBox powerBlip, regen;
        LevelEditorCanvas canvas;

        TileEditorDialog(Tile tile, int rowIndex, int columnIndex, LevelEditorCanvas theCanvas) {
            super(parent, String.format("Row %d | Column %d", rowIndex+1, columnIndex+1));
            this.canvas = theCanvas;
            this.tile = tile;
            this.setLayout(new GridBagLayout());
            int walls = tile.getEnemyMoveOptions()|tile.getPlayerMoveOptions();
            blipsPanel = new BlipsPanel(walls, !(tile.getInitialItem() instanceof NoItem));

            GridBagConstraints mpConstraints = new GridBagConstraints();
            mpConstraints.gridx = 0;
            mpConstraints.gridy = 0;

            GridBagConstraints bpConstraints = new GridBagConstraints();
            bpConstraints.gridx = 0;
            bpConstraints.gridy = 1;
            bpConstraints.anchor = GridBagConstraints.SOUTHEAST;

            this.add(makeMainPanel(), mpConstraints);
            this.add(makeButtonPanel(), bpConstraints);

            if (rowIndex == (board.getRows()-1)) {
                pmo.disableDirection(WEST);
                emo.disableDirection(WEST);
                blipsPanel.westBlip.setEnabled(false);
                blipsPanel.westBlip.setSelected(false);
            }
            if (rowIndex == 0) {
                pmo.disableDirection(EAST);
                emo.disableDirection(EAST);
                blipsPanel.eastBlip.setEnabled(false);
                blipsPanel.eastBlip.setSelected(false);
            }
            if (columnIndex == (board.getColumns()-1)) {
                pmo.disableDirection(NORTH);
                emo.disableDirection(NORTH);
                blipsPanel.northBlip.setEnabled(false);
                blipsPanel.northBlip.setSelected(false);
            }
            if (columnIndex == 0) {
                pmo.disableDirection(SOUTH);
                emo.disableDirection(SOUTH);
                blipsPanel.southBlip.setEnabled(false);
                blipsPanel.southBlip.setSelected(false);
            }
            pmo.north.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    blipsPanel.northBlip.setEnabled(pmo.north.isSelected()&&emo.north.isSelected());
                    blipsPanel.northBlip.setSelected(pmo.north.isSelected()&&emo.north.isSelected());
                }
            });
            pmo.south.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    blipsPanel.southBlip.setEnabled(pmo.south.isSelected()&&emo.south.isSelected());
                    blipsPanel.southBlip.setSelected(pmo.south.isSelected()&&emo.south.isSelected());
                }
            });
            pmo.east.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    blipsPanel.eastBlip.setEnabled(pmo.east.isSelected()&&emo.east.isSelected());
                    blipsPanel.eastBlip.setSelected(pmo.east.isSelected()&&emo.east.isSelected());
                }
            });
            pmo.west.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    blipsPanel.westBlip.setEnabled(pmo.west.isSelected()&&emo.west.isSelected());
                    blipsPanel.westBlip.setSelected(pmo.west.isSelected()&&emo.west.isSelected());
                }
            });
            powerBlip.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    blipsPanel.centerBlip.setEnabled(!powerBlip.isSelected());
                    blipsPanel.centerBlip.setSelected(blipsPanel.centerBlip.isEnabled());
                }
            });

            this.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    canvas.repaint();
                }
            });

            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            this.pack();
            this.setVisible(true);
        }



        void makeTile() {
            tile.setPlayerMoveOptions(pmo.getValue());
            tile.setEnemyMoveOptions(emo.getValue());
            if (powerBlip.isSelected()) {
                PowerBlip pb = new PowerBlip();
                pb.setExists(true);
                tile.setInitialItem(pb);
                tile.setItem(pb);
            } else {
                NoItem item = new NoItem();
                tile.setInitialItem(item);
                tile.setItem(item);
            }
            tile.setRegen(regen.isSelected());
            tile.setInitialCluster(blipsPanel.makeCluster());
            tile.makeBlips();
        }

        JPanel makeButtonPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    makeTile();
                    dispose();
                }
            });

            JButton applyButton = new JButton("Apply");
            applyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    makeTile();
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            panel.add(okButton);
            panel.add(applyButton);
            panel.add(cancelButton);

            return panel;
        }

        JPanel makeMainPanel() {
            JPanel panel = new JPanel(new GridBagLayout());

            GridBagConstraints opConstraints = new GridBagConstraints();
            opConstraints.gridx = 1;
            opConstraints.gridy = 1;

            GridBagConstraints blipConstraints = new GridBagConstraints();
            blipConstraints.gridx = 0;
            blipConstraints.gridy = 1;

            GridBagConstraints directionsPanelConstraints = new GridBagConstraints();
            directionsPanelConstraints.gridx = 0;
            directionsPanelConstraints.gridy = 0;
            directionsPanelConstraints.gridwidth = 2;

            panel.add(makeOtherPanel(), opConstraints);
            panel.add(makeDirectionsPanel(), directionsPanelConstraints);
            panel.add(blipsPanel, blipConstraints);

            return panel;
        }

        JPanel makeOtherPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            powerBlip = new JCheckBox("Power Blip");
            powerBlip.setSelected(tile.getInitialItem() instanceof PowerBlip);

            regen = new JCheckBox("Regen");

            panel.add(powerBlip);
            panel.add(regen);

            return panel;
        }

        JPanel makeDirectionsPanel() {
            JPanel panel = new JPanel(new GridBagLayout());

            pmo = new DirectionsPanel("Player", tile.getPlayerMoveOptions());
            emo = new DirectionsPanel("Enemy", tile.getEnemyMoveOptions());

            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.gridy = 0;
            labelConstraints.gridwidth = 2;

            GridBagConstraints pmoConstraints = new GridBagConstraints();
            pmoConstraints.gridx = 0;
            pmoConstraints.gridy = 1;

            GridBagConstraints emoConstraints = new GridBagConstraints();
            emoConstraints.gridx = 1;
            emoConstraints.gridy = 1;

            panel.add(new JLabel("Move Options"), labelConstraints);
            panel.add(pmo, pmoConstraints);
            panel.add(emo, emoConstraints);
            panel.setBorder(new EtchedBorder());

            return panel;
        }
    }

    private class BlipsPanel extends JPanel {
        JCheckBox northBlip, southBlip, eastBlip,
                  westBlip, centerBlip, nwBlip, neBlip,
                  swBlip, seBlip;
        BlipsPanel(int walls, boolean hasItem) {
            this.setLayout(new GridLayout(2, 1));

            this.setBorder(new EtchedBorder());

            this.add(new JLabel("Blips"));
            this.add(makeBlipBoxes(walls, hasItem));
        }

        JPanel makeBlipBoxes(int walls, boolean hasItem) {
            JPanel panel = new JPanel(new GridLayout(3, 3));

            nwBlip = new JCheckBox();
            northBlip = new JCheckBox();
            neBlip = new JCheckBox();

            westBlip = new JCheckBox();
            centerBlip = new JCheckBox();
            eastBlip = new JCheckBox();

            swBlip = new JCheckBox();
            southBlip = new JCheckBox();
            seBlip = new JCheckBox();

            nwBlip.setEnabled(false);
            neBlip.setEnabled(false);
            swBlip.setEnabled(false);
            seBlip.setEnabled(false);

            northBlip.setEnabled((walls&NORTH)>0);
            southBlip.setEnabled((walls&SOUTH)>0);
            eastBlip.setEnabled((walls&EAST)>0);
            westBlip.setEnabled((walls&WEST)>0);
            centerBlip.setEnabled(!hasItem);

            northBlip.setSelected(northBlip.isEnabled());
            southBlip.setSelected(southBlip.isEnabled());
            eastBlip.setSelected(eastBlip.isEnabled());
            westBlip.setSelected(westBlip.isEnabled());
            centerBlip.setSelected(centerBlip.isEnabled());

            panel.add(nwBlip);
            panel.add(northBlip);
            panel.add(neBlip);

            panel.add(westBlip);
            panel.add(centerBlip);
            panel.add(eastBlip);

            panel.add(swBlip);
            panel.add(southBlip);
            panel.add(seBlip);

            return panel;
        }

        BlipCluster makeCluster() {
            BlipCluster bc = new BlipCluster();

            bc.setValueAt(0, 1, northBlip.isSelected());
            bc.setValueAt(2, 1, southBlip.isSelected());
            bc.setValueAt(1, 2, eastBlip.isSelected());
            bc.setValueAt(1, 0, westBlip.isSelected());
            bc.setValueAt(1, 1, centerBlip.isSelected());

            return bc;
        }
    }

    private class DirectionsPanel extends JPanel {

        JCheckBox north, south, east, west;

        DirectionsPanel(String name, int directions) {
            super();
            this.setLayout(new GridBagLayout());

            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.gridy = 0;

            GridBagConstraints panelConstraints = new GridBagConstraints();
            panelConstraints.gridx = 0;
            panelConstraints.gridy = 1;

            this.add(new JLabel(name));
            this.add(makePanel(directions), panelConstraints);
            this.setBorder(new EtchedBorder());
        }

        void disableDirection(int direction) {
            if (direction == NORTH) {
                north.setSelected(false);
                north.setEnabled(false);
            } else if (direction == SOUTH) {
                south.setSelected(false);
                south.setEnabled(false);
            } else if (direction == EAST) {
                east.setSelected(false);
                east.setEnabled(false);
            } else if (direction == WEST) {
                west.setSelected(false);
                west.setEnabled(false);
            }
        }

        public int getValue() {
            int value = 0;

            if (north.isSelected()) {
                value |= NORTH;
            }
            if (south.isSelected()) {
                value |= SOUTH;
            }
            if (east.isSelected()) {
                value |= EAST;
            }
            if (west.isSelected()) {
                value |= WEST;
            }

            return value;
        }


        JPanel makePanel(int directions) {
            JPanel panel = new JPanel(new GridLayout(3, 3));

            north = new JCheckBox("N");
            south = new JCheckBox("S");
            east = new JCheckBox("E");
            west = new JCheckBox("W");

            north.setSelected((directions & NORTH) > 0);
            south.setSelected((directions & SOUTH) > 0);
            east.setSelected((directions & EAST) > 0);
            west.setSelected((directions & WEST) > 0);

            panel.add(new JPanel());
            panel.add(north);
            panel.add(new JPanel());
            panel.add(west);
            panel.add(new JPanel());
            panel.add(east);
            panel.add(new JPanel());
            panel.add(south);
            panel.add(new JPanel());

            return panel;
        }
    }

    private interface BoardChangedEvent {}

    private class TileDirectionsChangedEvent implements BoardChangedEvent {
        int previousPlayerDirections, previousEnemyDirections, row, col;

        /**
         * @param previousPlayerDirections
         * @param previousEnemyDirections
         * @param row
         * @param col
         */
        public TileDirectionsChangedEvent(int previousDirections, int row, int col) {
            this.previousPlayerDirections = previousDirections;
            this.previousEnemyDirections = previousDirections;
            this.row = row;
            this.col = col;
        }

        /**
         * @param previousPlayerDirections
         * @param previousEnemyDirections
         * @param row
         * @param col
         */
        public TileDirectionsChangedEvent(int previousPlayerDirections,
                                          int previousEnemyDirections, int row, int col) {
            this.previousPlayerDirections = previousPlayerDirections;
            this.previousEnemyDirections = previousEnemyDirections;
            this.row = row;
            this.col = col;
        }


    }

    private class ComplexChangeEvent implements BoardChangedEvent {
        TileDirectionsChangedEvent tdce;
        RegenTileEvent rte;
        PowerBlipEvent pbe;
        /**
         * @param tdce
         * @param rte
         * @param pbe
         */
        public ComplexChangeEvent(TileDirectionsChangedEvent tdce,
                                  RegenTileEvent rte, PowerBlipEvent pbe) {
            this.tdce = tdce;
            this.rte = rte;
            this.pbe = pbe;
        }

    }

    private class RegenDoorSetEvent implements BoardChangedEvent {
        int row, col, direction;

        /**
         * @param row
         * @param col
         * @param direction
         */
        public RegenDoorSetEvent(int row, int col, int direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }

    }

    private class StartPointSetEvent implements BoardChangedEvent {
        int row, col;

        /**
         * @param row
         * @param col
         */
        public StartPointSetEvent(int row, int col) {
            this.row = row;
            this.col = col;
        }


    }

    private class RegenTileEvent implements BoardChangedEvent {
        int row, col;

        /**
         * @param row
         * @param col
         */
        public RegenTileEvent(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private class PowerBlipEvent implements BoardChangedEvent {
        int row, col;
        boolean added;
        /**
         * @param row
         * @param col
         * @param added
         */
        public PowerBlipEvent(int row, int col, boolean added) {
            this.row = row;
            this.col = col;
            this.added = added;
        }

    }

    public class BoardUndoHandler {

        private Stack<BoardChangedEvent> past;
        private Stack<BoardChangedEvent> future;

        public BoardUndoHandler() {
            past = new Stack<BoardChangedEvent>();
            future = new Stack<BoardChangedEvent>();
        }

        public void addEdit(BoardChangedEvent bce) {
            past.push(bce);
        }

        public boolean canRedo() {
            return future.size() > 0;
        }

        public boolean canUndo() {
            return past.size() > 0;
        }

        public void redo() {
            if (future.size() > 0) {
                BoardChangedEvent bce = future.pop();
                processBoardChangedEvent(bce);
                past.push(bce);
            }
        }

        private void processBoardChangedEvent(BoardChangedEvent bce) {
            if (bce instanceof TileDirectionsChangedEvent) {
                TileDirectionsChangedEvent tdce = (TileDirectionsChangedEvent) bce;
                board.getTile(tdce.row, tdce.col).setPlayerMoveOptions(tdce.previousPlayerDirections);
                board.getTile(tdce.row, tdce.col).setEnemyMoveOptions(tdce.previousEnemyDirections);
            } else if (bce instanceof PowerBlipEvent) {
                PowerBlipEvent pbe = (PowerBlipEvent) bce;
                Tile tile = board.getTile(pbe.row, pbe.col);
                if (tile.getItem() instanceof PowerBlip) {
                    Item item = new NoItem();
                    tile.setInitialItem(item);
                    tile.setItem(item);
                } else {
                    Item item = new PowerBlip();
                    tile.setInitialItem(item);
                    tile.setItem(item);
                }
            } else if (bce instanceof RegenTileEvent) {
                RegenTileEvent rte = (RegenTileEvent) bce;
                board.getTile(rte.row, rte.col).setRegen(!board.getTile(rte.row, rte.col).isRegen());
            } else if (bce instanceof StartPointSetEvent) {
                if (startPointSet) {
                    startPointSet = false;
                    board.setPlayerStartPoint(0, 0);
                } else {
                    StartPointSetEvent spse = (StartPointSetEvent) bce;
                    board.setPlayerStartPoint(spse.row, spse.col);
                }
            } else if (bce instanceof RegenDoorSetEvent) {
                RegenDoorSetEvent rdse = (RegenDoorSetEvent) bce;
                if (regenDoorSet) {
                    regenDoorSet = false;
                    board.setRegenDoor(0, 0, 0);
                } else {
                    board.setRegenDoor(rdse.row, rdse.col, rdse.direction);
                }
            } else if (bce instanceof ComplexChangeEvent) {
                ComplexChangeEvent cce = (ComplexChangeEvent) bce;
                if (cce.pbe != null) {
                    processBoardChangedEvent(cce.pbe);
                }
                if (cce.rte != null) {
                    processBoardChangedEvent(cce.rte);
                }
                if (cce.tdce != null) {
                    processBoardChangedEvent(cce.tdce);
                }
            }
        }

        public void undo() {
            if (past.size() > 0) {
                BoardChangedEvent bce = past.pop();
                processBoardChangedEvent(bce);
                future.push(bce);
            }
        }

    }


    public BoardUndoHandler getUndoHandler() {
        return undoHandler;
    }

    public Board getBoard() {
        if (!regenDoorSet) {
            board.setRegenDoor(null);
        }
        if(!startPointSet) {
            board.setStartingCol(-1);
            board.setStartingRow(-1);
        }
        return board;
    }

    private JPanel makeMessagePanel(String message) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Scanner scanner = new Scanner(message);

        while (scanner.hasNext()) {
            panel.add(new JLabel(scanner.next()));
        }

        return panel;
    }

    public boolean checkBoardForSave() {
        if (!regenDoorSet) {
            if(JOptionPane.showConfirmDialog(this, makeMessagePanel("Regen Door not set.\nContinue anyway?")
                                             , "Continue?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (!startPointSet) {
                    return (JOptionPane.showConfirmDialog(this, makeMessagePanel("Player start point not set.\nContinue anyway?")
                                                          , "Continue?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else if (!startPointSet) {
            return (JOptionPane.showConfirmDialog(this, makeMessagePanel("Player start point not set.\nContinue anyway?")
                                                  , "Continue?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
        } else {
            return true;
        }
    }

    public void setBoard(Board board) {
        this.board = board;
        enemies = new ArrayList<Enemy>();
        if((board.getStartingRow() == -1)||(board.getStartingCol() == -1)) {
            startPointSet = false;
        } else {
            startPointSet = true;
        }
        regenDoorSet = (board.getRegenDoor() != null);
        board.reset();
        this.setPreferredSize(new Dimension(board.getColumns()*TILE_WIDTH, board.getRows()*TILE_HEIGHT));
        repaint();
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }
}
