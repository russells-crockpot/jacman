/**
 *
 */
package com.gnosis.jacman.engine;

import com.gnosis.jacman.engine.items.PowerBlip;

/**
 * @author Brendan McGloin
 *
 */
public final class TileFactory implements Constants {

    public static Tile makeSERegenTile(int exit) {
        Tile t = new Tile(SOUTH|EAST,
                          FILLED, null, true);
        t.setRegenDoor(exit);
        return t;
    }
    public static Tile makeSWRegenTile(int exit) {
        Tile t = new Tile(SOUTH|WEST,
                          FILLED, null, true);
        t.setRegenDoor(exit);
        return t;
    }
    public static Tile makeSEWRegenTile(int exit) {
        Tile t = new Tile(SOUTH|EAST|WEST,
                          FILLED, null, true);
        t.setRegenDoor(exit);
        return t;
    }
    public static Tile makeNSERegenTile(int exit) {
        Tile t = new Tile(NORTH|SOUTH|EAST,
                          FILLED, null, true);
        t.setRegenDoor(exit);
        return t;
    }
    public static Tile makeNSWRegenTile(int exit) {
        Tile t = new Tile(NORTH|SOUTH|WEST,
                          FILLED, null, true);
        t.setRegenDoor(exit);
        return t;
    }

    public static Tile makeNWRegenTile(int exit) {
        Tile t = new Tile(NORTH|WEST,
                          FILLED, null, true);
        t.setRegenDoor(exit);
        return t;
    }

    public static Tile makeNERegenTile(int exit) {
        Tile t = new Tile(NORTH|EAST,
                          FILLED, null, true);
        t.setRegenDoor(exit);
        return t;
    }
    public static Tile makeNSEWRegenTile(int exit) {
        Tile t = new Tile(NORTH|SOUTH|EAST|WEST,
                          FILLED, null, true);
        t.setRegenDoor(exit);
        return t;
    }

    public static Tile makeSERegenTile() {
        Tile t = new Tile(SOUTH|EAST,
                          FILLED, null, true);
        return t;
    }
    public static Tile makeSWRegenTile() {
        Tile t = new Tile(SOUTH|WEST,
                          FILLED, null, true);
        return t;
    }
    public static Tile makeSEWRegenTile() {
        Tile t = new Tile(SOUTH|EAST|WEST,
                          FILLED, null, true);
        return t;
    }
    public static Tile makeNSERegenTile() {
        Tile t = new Tile(NORTH|SOUTH|EAST,
                          FILLED, null, true);
        return t;
    }
    public static Tile makeNSWRegenTile() {
        Tile t = new Tile(NORTH|SOUTH|WEST,
                          FILLED, null, true);
        return t;
    }

    public static Tile makeNWRegenTile() {
        Tile t = new Tile(NORTH|WEST,
                          FILLED, null, true);
        return t;
    }

    public static Tile makeNERegenTile() {
        Tile t = new Tile(NORTH|EAST,
                          FILLED, null, true);
        return t;
    }
    public static Tile makeNSEWRegenTile() {
        Tile t = new Tile(NORTH|SOUTH|EAST|WEST,
                          FILLED, null, true);
        return t;
    }

    public static Tile makeNSPowerBlipTile() {
        return new Tile(NORTH|SOUTH, new PowerBlip());
    }
    public static Tile makeNEPowerBlipTile() {
        return new Tile(NORTH|EAST, new PowerBlip());
    }
    public static Tile makeNWPowerBlipTile() {
        return new Tile(NORTH|WEST, new PowerBlip());
    }
    public static Tile makeNEWPowerBlipTile() {
        return new Tile(NORTH|EAST|WEST, new PowerBlip());
    }
    public static Tile makeNSEPowerBlipTile() {
        return new Tile(NORTH|EAST|SOUTH, new PowerBlip());
    }
    public static Tile makeNSWPowerBlipTile() {
        return new Tile(NORTH|SOUTH|WEST, new PowerBlip());
    }
    public static Tile makeEWPowerBlipTile() {
        return new Tile(EAST|WEST, new PowerBlip());
    }
    public static Tile makeSEPowerBlipTile() {
        return new Tile(SOUTH|EAST, new PowerBlip());
    }
    public static Tile makeSWPowerBlipTile() {
        return new Tile(SOUTH|WEST, new PowerBlip());
    }
    public static Tile makeSEWPowerBlipTile() {
        return new Tile(SOUTH|EAST|WEST, new PowerBlip());
    }
    public static Tile makeNSEWPowerBlipTile() {
        return new Tile(NORTH|SOUTH|EAST|WEST, new PowerBlip());
    }

    public static Tile makeNStile() {
        return new Tile(NORTH|SOUTH);
    }
    public static Tile makeNEtile() {
        return new Tile(NORTH|EAST);
    }
    public static Tile makeNWtile() {
        return new Tile(NORTH|WEST);
    }
    public static Tile makeNEWtile() {
        return new Tile(NORTH|EAST|WEST);
    }
    public static Tile makeNSEtile() {
        return new Tile(NORTH|EAST|SOUTH);
    }
    public static Tile makeNSWtile() {
        return new Tile(NORTH|SOUTH|WEST);
    }
    public static Tile makeEWtile() {
        return new Tile(EAST|WEST);
    }
    public static Tile makeSEtile() {
        return new Tile(SOUTH|EAST);
    }
    public static Tile makeSWtile() {
        return new Tile(SOUTH|WEST);
    }
    public static Tile makeSEWtile() {
        return new Tile(SOUTH|EAST|WEST);
    }
    public static Tile makeNSEWtile() {
        return new Tile(NORTH|SOUTH|EAST|WEST);
    }
    public static Tile makeSolidTile() {
        return new Tile(FILLED);
    }
    public static Tile makeOpenTile() {
        return new Tile(OPEN);
    }
}
