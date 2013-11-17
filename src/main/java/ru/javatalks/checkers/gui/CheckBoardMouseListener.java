package ru.javatalks.checkers.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

    @Autowired
    private UserLogic userLogic;

    @Autowired
    private ChessBoardPanel chessBoard;

    @Override
    public void mousePressed(MouseEvent evt) {
        // Don't process event, if listener has inactive state
        if (!userLogic.isActive()) {
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
            userLogic.addClickedCell(getActiveCell());
            userLogic.addClickedCell(clickedCell);
            setActiveCell(clickedCell);
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
}
