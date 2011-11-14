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

    public static final int CELL_SIDE_NUM = 8;

    private int compCheckerNumber;
    private int userCheckerNumber;

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

    public void move(Cell activeCell, Cell targetCell) {
        // do move

        fireBoardChange();

        throw new UnsupportedOperationException("not implemented yet");
    }

    private void fireBoardChange() {
        compCheckerNumber = calculateCheckersNumberFor(Player.OPPONENT);
        userCheckerNumber = calculateCheckersNumberFor(Player.USER);
    }

    @Override
    public Iterator<Cell> iterator() {
        return new CellIterator();
}

    public int calculateCheckersNumberFor(Player owner) {
        int result = 0;
        for (Cell cell : this) {
            if (!cell.isEmpty() && cell.getChecker().getOwner() == owner) {
                result++;
            }
        }
        return result;
    }

    public int getCompCheckerNumber() {
        return compCheckerNumber;
    }

    public int getUserCheckerNumber() {
        return userCheckerNumber;
    }

    public boolean hasCheckerOf(Player player) {
        for (Cell cell : this) {
            if (cell.getChecker() != null && cell.getChecker().getOwner() == player) {
                return true;
            }
        }
        return false;
    }

    public boolean canStep(Player player) {
        return false;
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
