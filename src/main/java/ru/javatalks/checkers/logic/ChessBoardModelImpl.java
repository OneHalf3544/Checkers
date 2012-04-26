package ru.javatalks.checkers.logic;

import com.sun.istack.internal.Nullable;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.model.*;

import java.awt.*;
import java.util.*;

/**
 * Implementation of chessboard model interface
 * <p/>
 * <p/>
 * Created: 21.04.12 16:07
 * <p/>
 *
 * @author OneHalf
 */
@Service
public class ChessBoardModelImpl implements ChessBoardModel {

    /**
     * Number of opponent checkers on board at the moment
     */
    private int compCheckerNumber;

    /**
     * Number of user checkers on board at the moment
     */
    private int userCheckerNumber;

    /**
     * Chessboard content
     */
    private final Cell[][] contents;

    /**
     * Listeners of chessboard changes
     */
    private final Collection<ChessBoardListener> listeners = new ArrayList<ChessBoardListener>();

    public ChessBoardModelImpl() {
        contents = new Cell[8][8];

        setInitialState();
    }

    @Override
    public final void setInitialState() {
        for (int i = 0; i < contents.length; i++) {
            Cell[] tempArray = new Cell[8];
            for (int j = 0; j < tempArray.length; j++) {
                if ((i + j) % 2 == 0) {
                    tempArray[j] = new Cell(i, j);
                }
            }
            contents[i] = tempArray;
        }

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

        fireBoardChange();
    }

    public Cell getCellAt(Point point) {
        return getCellAt(point.x, point.y);
    }

    @Override
    public Cell getCellAt(int x, int y) {
        if (x < 0 || y < 0 || x >= CELL_SIDE_NUM || y >= CELL_SIDE_NUM) {
            return null;
        }
        return contents[x][y];
    }

    /**
     * Get cell relative to the specified cell for the specified direction
     * @param cell started cell
     * @param direction
     * @return
     */
    @Override
    @Nullable
    public Cell getRelativeCell(Cell cell, StepDirection direction) {
        return getRelativeCell(cell, direction, 1);
    }

    @Override
    public Cell getRelativeCell(Cell cell, StepDirection direction, int distant) {
        return getCellAt(direction.getNewCoordinates(cell.getPosition(), distant));
    }

    @Override
    public StepDescription move(Cell activeCell, Cell targetCell) {
        assert !activeCell.isEmpty();
        assert targetCell.isEmpty();

        Checker movedChecker = activeCell.getChecker();

        targetCell.setChecker(movedChecker);
        activeCell.setChecker(null);

        searchNewQueens();
        StepDescription stepDescription = new StepDescription(movedChecker, activeCell, targetCell);

        fireMoved(stepDescription);
        fireBoardChange();

        return stepDescription;
    }

    private void fireMoved(StepDescription stepDescription) {
        for (ChessBoardListener listener : listeners) {
            listener.moved(stepDescription);
        }
    }

    /**
     * Notify listeners about chessboard change
     */
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

    @Override
    public int getCompCheckerNumber() {
        return compCheckerNumber;
    }

    @Override
    public int getUserCheckerNumber() {
        return userCheckerNumber;
    }

    @Override
    public boolean hasCheckerOf(Player player) {
        for (Cell cell : this) {
            if (cell.getChecker() != null && cell.getChecker().getOwner() == player) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canStep(Player player) {
        return true;
    }

    @Override
    public void addListener(ChessBoardListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ChessBoardListener listener) {
        listeners.remove(listener);
    }

    @Override
    public StepDescription fight(Cell activeCell, Cell targetCell) {
        assert activeCell.hasQueen() || activeCell.hasSimpleChecker();

        StepDirection direction = getDirection(activeCell, targetCell);

        Checker checker = activeCell.getChecker();
        Cell victimCell = searchNextNonEmpty(activeCell, direction);

        activeCell.setChecker(null);
        victimCell.setChecker(null);
        targetCell.setChecker(checker);

        searchNewQueens();
        StepDescription stepDescription = new StepDescription(checker, activeCell, targetCell, victimCell);

        fireMoved(stepDescription);
        fireBoardChange();

        return stepDescription;
    }

    /**
     * Check new queens, if any simple checkers reached opposing chessboard side
     */
    private void searchNewQueens() {
        int y = 0;
        for (int x = 0; x < CELL_SIDE_NUM; x += 2) {
            Cell cell = contents[x][y];
            if (cell.hasCheckerOf(Player.OPPONENT) && cell.hasSimpleChecker()) {
                cell.getChecker().makeQueen();
            }

        }
        y = 7;
        for (int x = 1; x < CELL_SIDE_NUM; x += 2) {
            Cell cell = contents[x][y];
            if (cell.hasCheckerOf(Player.USER) && cell.hasSimpleChecker()) {
                cell.getChecker().makeQueen();
            }
        }
    }

    /**
     * Search next empty cell after cell in the specified direction
     * @param activeCell
     * @param direction direction to search empty cell
     * @return cell is found
     */
    @Override
    public Cell searchNextNonEmpty(Cell activeCell, StepDirection direction) {
        Cell tmpCell = activeCell;
        do {
            tmpCell = getRelativeCell(tmpCell, direction);
        } while (tmpCell != null && tmpCell.isEmpty());

        return tmpCell;
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

    @Override
    public StepDirection getDirection(Cell from, Cell to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        return StepDirection.getByDxDy(dx, dy);
    }

    /**
     * Iterator for getting black cells at board
     */
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
            throw new UnsupportedOperationException("remove method not supported");
        }
    }
}
