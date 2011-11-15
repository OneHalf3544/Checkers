package ru.javatalks.checkers.model;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

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

    public void setInitialState() {
        for(int x = 0; x < CELL_SIDE_NUM; x++) {
            for(int y = 0; y <= 2; y++) {
                if ((x + y) % 2 == 0) {
                    contents[x][y].setChecker(new Checker(Player.USER));
                }
            }

            for(int y = 5; y <= 7; y++) {
                if ((x + y) % 2 == 0) {
                    contents[x][y].setChecker(new Checker(Player.OPPONENT));
                }
            }
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

    public void move(Cell from, Cell to) {
        assert !from.isEmpty();
        assert to.isEmpty();

        to.setChecker(from.getChecker());
        from.setChecker(null);

        fireMoved(from, to, Player.USER);
        fireBoardChange();
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

    public void doStep(Cell from, Cell to) {
        assert from != null;
        assert to == null;

        move(from, to);
    }

    public void addListener(ChessBoardListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChessBoardListener listener) {
        listeners.remove(listener);
    }

    private class CellIterator implements Iterator<Cell> {
        
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < CELL_SIDE_NUM * CELL_SIDE_NUM - 2;
        }

        @Override
        public Cell next() {
            index += 2;
            return contents[index / CELL_SIDE_NUM][index % CELL_SIDE_NUM];
        }

        @Override
        public void remove() {}
    }
}
