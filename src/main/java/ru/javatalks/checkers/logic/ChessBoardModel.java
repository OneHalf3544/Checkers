package ru.javatalks.checkers.logic;

import com.sun.istack.internal.Nullable;
import ru.javatalks.checkers.model.Cell;
import ru.javatalks.checkers.model.Player;
import ru.javatalks.checkers.model.StepDescription;
import ru.javatalks.checkers.model.StepDirection;

/**
 * Model of chess board. Doesn't contain any gui-dependent data
 *
 * Date: 13.11.11
 * Time: 2:49
 *
 * @author OneHalf
 */
public interface ChessBoardModel extends Iterable<Cell> {

    public static final int CELL_SIDE_NUM = 8;

    /**
     * Get a cell by indexes
     * @param x horizontal index
     * @param y vertical index
     * @return Cell data at specified coordinates
     */
    public Cell getCellAt(int x, int y);

    public StepDescription move(Cell activeCell, Cell targetCell);

    public void addListener(ChessBoardListener listener);

    public void removeListener(ChessBoardListener listener);

    /**
     * Fight a victim by moving checker from active cell to target
     * @param activeCell cell for start fight
     * @param targetCell cell for end fight
     * @return step description
     */
    public StepDescription fight(Cell activeCell, Cell targetCell);

    /**
     * Direction to move from one cell to another
     * @param from first cell
     * @param to end cell
     * @return
     */
    public StepDirection getDirection(Cell from, Cell to);

    /**
     * Get relative cell by the distance and the direction
     * @param cell base cell
     * @param direction
     * @param distant
     * @return relative cell
     */
    public Cell getRelativeCell(Cell cell, StepDirection direction, int distant);

    public Cell searchNextNonEmpty(Cell activeCell, StepDirection direction);

    @Nullable
    public Cell getRelativeCell(Cell cell, StepDirection direction);

    public int getCompCheckerNumber();

    public int getUserCheckerNumber();

    /**
     * Check, that user still has the checkers
     * @param player
     * @return true, if player has checkers
     */
    public boolean hasCheckerOf(Player player);

    /**
     * @param player
     * @return true, if specified player can do a step
     */
    public boolean canStep(Player player);

    /**
     * Set chessboard content to initial state (12+12 checkers at both side of board)
     */
    public void setInitialState();
}
