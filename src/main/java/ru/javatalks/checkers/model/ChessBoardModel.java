package ru.javatalks.checkers.model;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Date: 13.11.11
 * Time: 2:49
 *
 * @author OneHalf
 */
@Service
public class ChessBoardModel implements Iterable<Cell> {

    public static final int CELL_SIDE_NUM = 8;

    private int compCheckerNumber;
    private int userCheckerNumber;

    private final Cell[][] contents;

    private final List<ChessBoardListener> listeners = new ArrayList<ChessBoardListener>();

    public ChessBoardModel() {
        contents = new Cell[8][8];
        for (int i = 0; i < contents.length; i++) {
            Cell[] tempArray = new Cell[8];
            for (int j = 0; j < tempArray.length; j++) {
                tempArray[j] = new Cell(i, j);
            }
            contents[i] = tempArray;
        }

        setInitialState();
    }

    public final void setInitialState() {
        for (int x = 0; x < CELL_SIDE_NUM; x++) {
            for (int y = 0; y <= 2; y++) {
                if ((x + y) % 2 == 0) {
                    contents[x][y].setChecker(new Checker(Player.USER));
                }
            }

            for (int y = 5; y <= 7; y++) {
                if ((x + y) % 2 == 0) {
                    contents[x][y].setChecker(new Checker(Player.OPPONENT));
                }
            }
        }

        compCheckerNumber = 12;
        userCheckerNumber = 12;
    }

    public Cell getCellAt(Point point) {
        return getCellAt(point.x, point.y);
    }

    public Cell getCellAt(int x, int y) {
        if (x < 0 || y < 0 || x >= CELL_SIDE_NUM || y >= CELL_SIDE_NUM) {
            return null;
        }
        return contents[x][y];
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction) {
        return getRelativeCell(cell, direction, 1);
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction, int distant) {
        return getCellAt(direction.getNewCoordinates(cell.getPosition(), distant));
    }

    public StepDescription move(Cell from, StepDirection direction) {
        assert !from.isEmpty();
        assert direction != null;

        Cell to = getRelativeCell(from, direction);
        to.setChecker(from.getChecker());
        from.setChecker(null);

        // todo checkNewQueens();
        fireMoved(from, to, Player.USER);
        fireBoardChange();

        return new StepDescription(from, to);
    }

    private void fireMoved(Cell activeCell, Cell targetCell, Player user) {
        for (ChessBoardListener listener : listeners) {
            listener.moved(activeCell, targetCell, null, user);
        }
    }

    private void fireBoardChange() {
        compCheckerNumber = calculateCheckersNumberFor(Player.OPPONENT);
        userCheckerNumber = calculateCheckersNumberFor(Player.USER);

        for (ChessBoardListener listener : listeners) {
            listener.boardChanged();
        }
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
        return true;
    }

    public void addListener(ChessBoardListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChessBoardListener listener) {
        listeners.remove(listener);
    }

    public StepDescription fight(Cell activeCell, StepDirection direction) {
        throw new UnsupportedOperationException();
    }

    public StepDescription fight(Cell activeCell, Cell targetCell) {
        throw new UnsupportedOperationException();
    }

    private void checkNewQueens(Cell cell) {
        if (!cell.hasSimpleChecker()) {
            return;
        }

        if (cell.getChecker().getOwner() == Player.USER && cell.getY() == 7
                || cell.getChecker().getOwner() == Player.OPPONENT && cell.getY() == 0) {
            cell.getChecker().makeQueen();
        }
    }

    public StepDescription move(Cell activeCell, Cell targetCell) {
        return new StepDescription(activeCell, targetCell);
    }

    private class CellIterator implements Iterator<Cell> {

        private int indexX = -2;
        private int indexY = 0;

        @Override
        public boolean hasNext() {
            return indexX < 7 || indexY < 7;
        }

        @Override
        public Cell next() {
            indexX += 2;

            if (indexX >= CELL_SIDE_NUM) {
                indexX = 1 - indexX % CELL_SIDE_NUM;
                indexY++;
            }
            return contents[indexX][indexY];
        }

        @Override
        public void remove() {
        }
    }
}
