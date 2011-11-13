package ru.javatalks.checkers.model;

import java.awt.*;
import java.util.Iterator;

/**
 * Date: 13.11.11
 * Time: 2:49
 *
 * @author OneHalf
 */
public class ChessBoardModel implements Iterable<Cell> {

    private final Cell[][] contents;

    public ChessBoardModel() {
        contents = new Cell[8][8];
        for (int i = 0; i < contents.length; i++) {
            Cell[] tempArray = new Cell[8];
            for (int j = 0; j < tempArray.length; j++) {
                tempArray[j] = new Cell(i, j, null);
            }
            contents[i] = tempArray;
        }
    }

    public Cell getCellAt(Point point) {
        return contents[point.x][point.y];
    }

    public Cell getCellAt(int x, int y) {
        return contents[x][y];
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction) {
        return getRelativeCell(cell, direction, 1);
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction, int distant) {
        return getCellAt(direction.getNewCoordinates(cell.getPosition(), distant));
    }

    @Override
    public Iterator<Cell> iterator() {
        return new CellIterator();
}

    private class CellIterator implements Iterator<Cell> {
        
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < 63;
        }

        @Override
        public Cell next() {
            index += 2;
            return contents[index / 8][index % 8];
        }

        @Override
        public void remove() {}
    }
}
