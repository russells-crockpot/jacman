package com.gnosis.jacman.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.gnosis.jacman.ann.ANNTools;
import com.gnosis.jacman.engine.items.PowerBlip;

/**
 * This class contains static methods to create 2 different games.
 * These preloads are hard-coded, but only used if no valid game
 * files can be found.
 *
 * @author Brendan McGloin
 */
public final class Preloads implements Constants {


    public static final String GAME_1_PATH = WORKING_PATH+"Games"+SEPARATOR+"game1.jmg";
    public static final String GAME_2_PATH = WORKING_PATH+"Games"+SEPARATOR+"game2.jmg";

    public static Game makeGame2() {
        return new Game(makeBoard2(), makeEnemiesForBoard2(), ANNTools.makeInitialANN2(makeBoard2(), makeEnemiesForBoard2().length, 0.8));
    }

    public static Enemy[] makeEnemiesForBoard2() {
        Enemy[] enemies = new Enemy[4];

        enemies[0] = new Enemy(6500,"Purple", 0, 2);
        enemies[1] = new Enemy(2500, "Red", 0, 2);
        enemies[2] = new Enemy(7500, "Orange", 0, 2);
        enemies[3] = new Enemy(3500, "Cyan", 0, 2);
        return enemies;
    }

    public static Board makeBoard2() {
        Board board = new Board(6, 5, 4, 2);

        //row 0
        board.addTile(TileFactory.makeSEPowerBlipTile(), 0, 0);
        board.addTile(TileFactory.makeSWtile(), 0, 1);
        board.addTile(TileFactory.makeNSEWRegenTile(SOUTH), 0, 2);
        board.addTile(TileFactory.makeSEtile(), 0, 3);
        board.addTile(TileFactory.makeSWPowerBlipTile(), 0, 4);

        //row 1
        board.addTile(new Tile(NORTH|SOUTH|EAST, NORTH|SOUTH), 1, 0);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, NORTH|SOUTH|EAST), 1, 1);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, EAST|WEST), 1, 2, true);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, NORTH|SOUTH|WEST), 1, 3);
        board.addTile(new Tile(NORTH|SOUTH|WEST, NORTH|SOUTH), 1, 4);

        //row 2
        board.addTile(new Tile(NORTH|SOUTH|EAST, NORTH|EAST), 2, 0);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, NORTH|WEST|EAST), 2, 1);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, SOUTH|EAST|WEST), 2, 2);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, NORTH|EAST|WEST), 2, 3);
        board.addTile(new Tile(NORTH|SOUTH|WEST, NORTH|WEST), 2, 4);

        //row 3
        board.addTile(new Tile(NORTH|SOUTH|EAST, SOUTH|EAST), 3, 0);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, SOUTH|WEST), 3, 1);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, SOUTH|NORTH), 3, 2);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, SOUTH|EAST), 3, 3);
        board.addTile(new Tile(NORTH|SOUTH|WEST, SOUTH|WEST), 3, 4);

        //row 4
        board.addTile(new Tile(NORTH|SOUTH|EAST, NORTH|SOUTH, new PowerBlip()), 4, 0);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, NORTH|SOUTH|EAST), 4, 1);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, NORTH|EAST|WEST), 4, 2);
        board.addTile(new Tile(NORTH|SOUTH|EAST|WEST, NORTH|SOUTH|WEST), 4, 3);
        board.addTile(new Tile(NORTH|SOUTH|WEST, NORTH|SOUTH, new PowerBlip()), 4, 4);

        //row 5
        board.addTile(new Tile(NORTH|EAST, NORTH|EAST), 5, 0);
        board.addTile(new Tile(NORTH|EAST|WEST, NORTH|WEST|EAST), 5, 1);
        board.addTile(new Tile(NORTH|EAST|WEST, EAST|WEST), 5, 2);
        board.addTile(new Tile(NORTH|EAST|WEST, NORTH|WEST|EAST), 5, 3);
        board.addTile(new Tile(NORTH|WEST, NORTH|WEST), 5, 4);

        board.setRegenDoor(1, 2, NORTH);

        return board;
    }

    public static Game loadGame1() throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(new File(GAME_1_PATH)));
        Game g = (Game) stream.readObject();
        for (PowerBlip pb: g.getBoard().getPowerBlips()) {
            pb.setExists(true);
        }
        return g;
    }

    public static Game makeGame1() {
        Board board = makeBoard1();
        Enemy[] enemies = makeEnemiesForBoard1();
        return new Game(board, enemies, ANNTools.makeInitialANN2(board, enemies.length, 0.8));
    }

    public static Enemy[] makeEnemiesForBoard1() {
        Enemy[] enemies = new Enemy[5];

        enemies[0] = new Enemy(6500,"Purple", 0, 5);
        enemies[1] = new Enemy(2500, "Red", 0, 5);
        enemies[2] = new Enemy(7500, "Orange", 0, 5);
        enemies[3] = new Enemy(3500, "Cyan", 0, 5);
        enemies[4] = new Enemy(8000, "Pink", 0, 5);

        return enemies;
    }

    public static Board makeBoard1() {
        Board board = new Board(11, 11, 7, 5);

        //row 0
        board.addTile(TileFactory.makeSEPowerBlipTile(), 0, 0);
        board.addTile(TileFactory.makeEWtile(), 0, 1);
        board.addTile(TileFactory.makeSEWtile(), 0, 2);
        board.addTile(TileFactory.makeSWtile(), 0, 3);
        board.addTile(TileFactory.makeSERegenTile(), 0, 4);
        board.addTile(TileFactory.makeSEWRegenTile(), 0, 5, true);
        board.addTile(TileFactory.makeSWRegenTile(), 0, 6);
        board.addTile(TileFactory.makeSEtile(), 0, 7);
        board.addTile(TileFactory.makeSEWtile(), 0, 8);
        board.addTile(TileFactory.makeEWtile(), 0, 9);
        board.addTile(TileFactory.makeSWPowerBlipTile(), 0, 10);

        //row 01
        board.addTile(TileFactory.makeNSEtile(), 1, 0);
        board.addTile(TileFactory.makeEWtile(), 1, 1);
        board.addTile(TileFactory.makeNWPowerBlipTile(), 1, 2);
        board.addTile(TileFactory.makeNStile(), 1, 3);
        board.addTile(TileFactory.makeNERegenTile(), 1, 4);
        board.addTile(TileFactory.makeNSEWRegenTile(NORTH), 1, 5);
        board.addTile(TileFactory.makeNWRegenTile(), 1, 6);
        board.addTile(TileFactory.makeNStile(), 1, 7);
        board.addTile(TileFactory.makeNEPowerBlipTile(), 1, 8);
        board.addTile(TileFactory.makeEWtile(), 1, 9);
        board.addTile(TileFactory.makeNSWtile(), 1, 10);

        //row 02
        board.addTile(TileFactory.makeNSEtile(), 2, 0);
        board.addTile(TileFactory.makeEWPowerBlipTile(), 2, 1);
        board.addTile(TileFactory.makeSWtile(), 2, 2);
        board.addTile(TileFactory.makeNSEtile(), 2, 3);
        board.addTile(TileFactory.makeEWtile(), 2, 4);
        Tile enteranceTile = new Tile(Globals.NORTH|Globals.SOUTH|Globals.EAST|Globals.WEST, Globals.SOUTH|Globals.EAST|Globals.WEST, new PowerBlip());
        board.addTile(enteranceTile, 2, 5);
        board.addTile(TileFactory.makeEWtile(), 2, 6);
        board.addTile(TileFactory.makeNSWtile(), 2, 7);
        board.addTile(TileFactory.makeSEtile(), 2, 8);
        board.addTile(TileFactory.makeEWPowerBlipTile(), 2, 9);
        board.addTile(TileFactory.makeNSWtile(), 2, 10);

        //row 03
        board.addTile(TileFactory.makeNSEtile(), 3, 0);
        board.addTile(TileFactory.makeEWtile(), 3, 1);
        board.addTile(TileFactory.makeNSEWtile(), 3, 2);
        board.addTile(TileFactory.makeNSWtile(), 3, 3);
        board.addTile(TileFactory.makeSEtile(), 3, 4);
        board.addTile(TileFactory.makeNSEWtile(), 3, 5);
        board.addTile(TileFactory.makeSWtile(), 3, 6);
        board.addTile(TileFactory.makeNSEtile(), 3, 7);
        board.addTile(TileFactory.makeNSEWtile(), 3, 8);
        board.addTile(TileFactory.makeEWtile(), 3, 9);
        board.addTile(TileFactory.makeNSWtile(), 3, 10);

        //row 04
        board.addTile(TileFactory.makeNStile(), 4, 0);
        board.addTile(TileFactory.makeSolidTile(), 4, 1);
        board.addTile(TileFactory.makeNStile(), 4, 2);
        Tile tile43  = new Tile(Globals.NORTH|Globals.SOUTH, Globals.NORTH|Globals.SOUTH|Globals.EAST);
        board.addTile(tile43, 4, 3);
        Tile tile44  = new Tile(Globals.NORTH|Globals.SOUTH, Globals.NORTH|Globals.SOUTH|Globals.WEST);
        board.addTile(tile44, 4, 4);
        board.addTile(TileFactory.makeNStile(), 4, 5);
        Tile tile46  = new Tile(Globals.NORTH|Globals.SOUTH, Globals.NORTH|Globals.SOUTH|Globals.EAST);
        board.addTile(tile46, 4, 6);
        Tile tile47  = new Tile(Globals.NORTH|Globals.SOUTH, Globals.NORTH|Globals.SOUTH|Globals.WEST);
        board.addTile(tile47, 4, 7);
        board.addTile(TileFactory.makeNStile(), 4, 8);
        board.addTile(TileFactory.makeSolidTile(), 4, 9);
        board.addTile(TileFactory.makeNStile(), 4, 10);

        //row 05
        board.addTile(TileFactory.makeNEPowerBlipTile(), 5, 0);
        board.addTile(TileFactory.makeSEWtile(), 5, 1);
        board.addTile(TileFactory.makeNWPowerBlipTile(), 5, 2);
        board.addTile(TileFactory.makeNStile(), 5, 3);
        board.addTile(TileFactory.makeNSPowerBlipTile(), 5, 4);
        board.addTile(TileFactory.makeNStile(), 5, 5);
        board.addTile(TileFactory.makeNSPowerBlipTile(), 5, 6);
        board.addTile(TileFactory.makeNStile(), 5, 7);
        board.addTile(TileFactory.makeNEPowerBlipTile(), 5, 8);
        board.addTile(TileFactory.makeSEWtile(), 5, 9);
        board.addTile(TileFactory.makeNWPowerBlipTile(), 5, 10);

        //row 06
        board.addTile(TileFactory.makeSEPowerBlipTile(), 6, 0);
        board.addTile(TileFactory.makeNEWtile(), 6, 1);
        board.addTile(TileFactory.makeSEWtile(), 6, 2);
        board.addTile(TileFactory.makeNSEWtile(), 6, 3);
        board.addTile(TileFactory.makeNSEWtile(), 6, 4);
        board.addTile(TileFactory.makeNEWtile(), 6, 5);
        board.addTile(TileFactory.makeNSEWtile(), 6, 6);
        board.addTile(TileFactory.makeNSEWtile(), 6, 7);
        board.addTile(TileFactory.makeSEWtile(), 6, 8);
        board.addTile(TileFactory.makeNEWtile(), 6, 9);
        board.addTile(TileFactory.makeSWPowerBlipTile(), 6, 10);

        //row 07
        board.addTile(TileFactory.makeNEtile(), 7, 0);
        board.addTile(TileFactory.makeEWtile(), 7, 1);
        board.addTile(TileFactory.makeNSWPowerBlipTile(), 7, 2);
        board.addTile(TileFactory.makeNSEtile(), 7, 3);
        board.addTile(TileFactory.makeNEWtile(), 7, 4);
        board.addTile(new Tile(SOUTH|EAST|WEST, EAST|WEST), 7, 5);
        board.addTile(TileFactory.makeNEWtile(), 7, 6);
        board.addTile(TileFactory.makeNSWtile(), 7, 7);
        board.addTile(TileFactory.makeNSEPowerBlipTile(), 7, 8);
        board.addTile(TileFactory.makeEWtile(), 7, 9);
        board.addTile(TileFactory.makeNWtile(), 7, 10);

        //row 08
        board.addTile(TileFactory.makeSEtile(), 8, 0);
        board.addTile(TileFactory.makeSWPowerBlipTile(), 8, 1);
        board.addTile(TileFactory.makeNStile(), 8, 2);
        board.addTile(TileFactory.makeNStile(), 8, 3);
        board.addTile(TileFactory.makeSEtile(), 8, 4);
        board.addTile(new Tile(NORTH|EAST|WEST, EAST|WEST), 8, 5);
        board.addTile(TileFactory.makeSWtile(), 8, 6);
        board.addTile(TileFactory.makeNStile(), 8, 7);
        board.addTile(TileFactory.makeNStile(), 8, 8);
        board.addTile(TileFactory.makeSEPowerBlipTile(), 8, 9);
        board.addTile(TileFactory.makeSWtile(), 8, 10);

        //row 09
        board.addTile(TileFactory.makeNStile(), 9, 0);
        board.addTile(TileFactory.makeNStile(), 9, 1);
        board.addTile(TileFactory.makeNStile(), 9, 2);
        board.addTile(TileFactory.makeNEPowerBlipTile(), 9, 3);
        board.addTile(TileFactory.makeNSEWtile(), 9, 4);
        board.addTile(TileFactory.makeEWtile(), 9, 5);
        board.addTile(TileFactory.makeNSEWtile(), 9, 6);
        board.addTile(TileFactory.makeNWPowerBlipTile(), 9, 7);
        board.addTile(TileFactory.makeNStile(), 9, 8);
        board.addTile(TileFactory.makeNStile(), 9, 9);
        board.addTile(TileFactory.makeNStile(), 9, 10);

        //row 08
        board.addTile(TileFactory.makeNEPowerBlipTile(), 10, 0);
        board.addTile(TileFactory.makeNEWtile(), 10, 1);
        board.addTile(TileFactory.makeNEWtile(), 10, 2);
        board.addTile(TileFactory.makeEWtile(), 10, 3);
        board.addTile(TileFactory.makeNEWtile(), 10, 4);
        board.addTile(TileFactory.makeEWtile(), 10, 5);
        board.addTile(TileFactory.makeNEWtile(), 10, 6);
        board.addTile(TileFactory.makeEWtile(), 10, 7);
        board.addTile(TileFactory.makeNEWtile(), 10, 8);
        board.addTile(TileFactory.makeNEWtile(), 10, 9);
        board.addTile(TileFactory.makeNWPowerBlipTile(), 10, 10);

        board.setRegenDoor(2, 5, Globals.NORTH);

        return board;
    }


}
