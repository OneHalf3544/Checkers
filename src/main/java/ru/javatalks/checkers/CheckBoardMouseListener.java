package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.model.Cell;
import ru.javatalks.checkers.model.ChessBoardModel;
import ru.javatalks.checkers.model.Logic;

import java.awt.event.*;

/** 
 * This class provides user's actions on chess board
 * 
 * @author Kapellan
 */
@Component
class CheckBoardMouseListener extends MouseAdapter {

    @Autowired
    private ChessBoardModel chessBoardModel;

    @Autowired
    private Logic logic;

    @Autowired
    private StepLogger painter;

    @Autowired
    private ChessBoard chessBoard;

    @Override
    public void mousePressed(MouseEvent e) {
        Cell clickedCell = getCellByCoordinates(e.getX(), e.getY());

        if (clickedCell == null || clickedCell.hasOpponentChecker()) {
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (!hasActiveChecker()) {
                setActiveCell(clickedCell);
                return;
            }

            // If was click on active checker - it becomes deactivated
            if (getActiveCell() == clickedCell) {
                resetActiveCell();
                return;
            }

            // If was click on non active checker, but we have active checker,
            // active becomes non active, and deactivated becomes active
            if (clickedCell.hasUserChecker()) {
                resetActiveCell();
                setActiveCell(clickedCell);
                return;
            }

            // We activated checker, so second click selects target cell
            Cell activeCell = getActiveCell();
            logic.doStep(activeCell, clickedCell);
            return;
        }

        // Right-button mouse click - deactivates any active checker
        if (e.getButton() == MouseEvent.BUTTON3) {
            resetActiveCell();
        }
    }

    public boolean hasActiveChecker() {
        return chessBoard.hasActiveChecker();
    }

    void setActiveCell(Cell cell) {
        chessBoard.setActiveCell(cell);
    }

    void resetActiveCell() {
        chessBoard.setActiveCell(null);
    }

    private Cell getActiveCell() {
        return chessBoard.getActiveCell();
    }

    Cell getCellByCoordinates(int clickedX, int clickedY) {
        int x = (clickedX - ChessBoard.OFFSET_LEFT_BOUND) / CellType.CELL_SIZE;
        int y = ChessBoardModel.CELL_SIDE_NUM - (clickedY - ChessBoard.OFFSET_TOP_BOUND) / CellType.CELL_SIZE -1;

        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            return chessBoardModel.getCellAt(x, y);
        }
        return null;
    }
}
