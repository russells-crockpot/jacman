/**
 *
 */
package com.gnosis.jacman.editor;

import com.gnosis.jacman.engine.*;

/**
 * @author Brendan McGloin
 *
 */
public final class BoardFactory implements Constants {

    private BoardFactory() {}

    public static Board makeBlankBoard(int rows, int columns) {
        Board board = new Board(rows, columns);

        for (int i = 0; i < rows; i++) {
            board.addRow(makeMiddleRow(columns), i);
        }

        board.addRow(makeTopRow(columns), 0);
        board.addRow(makeBottomRow(columns), rows - 1);

        return board;
    }

    private static Tile[] makeTopRow(int columns) {
        Tile[] row = new Tile[columns];

        for (int i = 0; i < row.length; i++) {
            row[i] = TileFactory.makeSEWtile();
        }
        row[0] = TileFactory.makeSEtile();
        row[row.length - 1] = TileFactory.makeSWtile();

        return row;
    }


    private static Tile[] makeMiddleRow(int columns) {
        Tile[] row = new Tile[columns];

        //first, set all items to be blank
        for (int i = 0; i < row.length; i++) {
            row[i] = new Tile(EMPTY);
        }
        row[0] = TileFactory.makeNSEtile();
        row[row.length - 1] = TileFactory.makeNSWtile();

        return row;
    }

    private static Tile[] makeBottomRow(int columns) {
        Tile[] row = new Tile[columns];

        for (int i = 0; i < row.length; i++) {
            row[i] = TileFactory.makeNEWtile();
        }
        row[0] = TileFactory.makeNEtile();
        row[row.length - 1] = TileFactory.makeNWtile();

        return row;
    }
}
