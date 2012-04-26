package ru.javatalks.checkers.gui;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.logic.CheckerStepException;
import ru.javatalks.checkers.model.Cell;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** 
 * This class provides user's actions on chess board
 * 
 * @author Kapellan
 */
@Component
class CheckBoardMouseListener extends MouseAdapter {

    private static final Logger logger = Logger.getLogger(CheckBoardMouseListener.class);

    @Autowired
    private UserLogic userLogic;

    @Autowired
    private ChessBoard chessBoard;

    @Autowired
    private StepLogger stepLogger;

    private boolean active = false;

    @Override
    public void mousePressed(MouseEvent evt) {
        // Don't process event, if listener has inactive state
        if (!active) {
            return;
        }

        Cell clickedCell = chessBoard.getCellByCoordinates(evt.getX(), evt.getY());

        // Right-button mouse click - deactivates any active checker
        if (evt.getButton() == MouseEvent.BUTTON3) {
            resetActiveCell();
            return;
        }

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
                userLogic.doStep(activeCell, clickedCell);
                active = false;
            } catch (CheckerStepException e) {
                stepLogger.logErrorCause(e);
                logger.info(e.getCauseOfError());
            }
        }
    }

    private boolean hasActiveChecker() {
        return chessBoard.hasActiveChecker();
    }

    private void setActiveCell(Cell cell) {
        chessBoard.setActiveCell(cell);
    }

    private void resetActiveCell() {
        chessBoard.setActiveCell(null);
    }

    private Cell getActiveCell() {
        return chessBoard.getActiveCell();
    }

    public void setActiveState() {
        active = true;
    }
}
