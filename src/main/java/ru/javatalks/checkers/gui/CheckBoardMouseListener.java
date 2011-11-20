package ru.javatalks.checkers.gui;

import org.apache.log4j.Logger;
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

    private static final Logger logger = Logger.getLogger(CheckBoardMouseListener.class);

    @Autowired
    private ChessBoardModel chessBoardModel;

    @Autowired
    private Logic logic;

    @Autowired
    private ChessBoard chessBoard;

    @Autowired
    private StepLogger stepLogger;

    @Override
    public void mousePressed(MouseEvent evt) {
        Cell clickedCell = getCellByCoordinates(evt.getX(), evt.getY());

        if (clickedCell == null || clickedCell.hasOpponentChecker()) {
            return;
        }

        if (evt.getButton() == MouseEvent.BUTTON1) {
            // set active cell (if cell is not active yet)
            if (!hasActiveChecker() || clickedCell.hasUserChecker()) {
                setActiveCell(clickedCell);
                return;
            }

            // If was click on active checker - it becomes deactivated
            if (getActiveCell() == clickedCell) {
                resetActiveCell();
                return;
            }

            // We activated checker, so second click selects target cell
            try {
                Cell activeCell = getActiveCell();
                logic.doStep(activeCell, clickedCell);
                logic.doCompStep();
            } catch (CheckerStepException e) {
                stepLogger.logErrorCause(e);
                logger.info(e.getCauseOfError());
            }
        }

        // Right-button mouse click - deactivates any active checker
        if (evt.getButton() == MouseEvent.BUTTON3) {
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
