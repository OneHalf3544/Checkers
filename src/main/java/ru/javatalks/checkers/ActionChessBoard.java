package ru.javatalks.checkers;

import java.awt.event.*;

/** 
 *  * This class provides user's actions on chess board
 * 
 * @author Kapellan
 */
class ActionChessBoard extends MouseAdapter {

    private ChessBoard cBoard;
    private Logic logic;
    private Thread thread;
    
    /* If true - we selected checker (checker is active - it is red) */
    private boolean selectingFlag = false;

    ActionChessBoard(ChessBoard cBoard, Logic logic) {
        this.cBoard = cBoard;
        this.logic = logic;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1 && !logic.nextStepCompFlag) {
            /* Select checker - paint it red - make it active */
            Cell clickedCell = logic.getCellByCoordinates(e.getX(), e.getY());
            if (!selectingFlag) {
                setActiveCell(clickedCell);
                return;
            }

            /* If was click on active checker - it becomes deactive */
            if (getActiveCell().equals(clickedCell)) {
                resetActiveCell();
                return;
            }

            /* If was click on deactive checker, but we hawe active checker, active becomes deactive, and deactive becomes active */
            if (selectingFlag
                    && (clickedCell.status == CellStatus.USER_CHECKER
                    || clickedCell.status == CellStatus.WHITE_QUEEN)) {
                resetActiveCell();
                setActiveCell(clickedCell);
                return;
            }

            /* We activated checker, so second click selects target cell */
            if (selectingFlag == true) {
                Cell activeCell = getActiveCell();
                Cell targetCell = clickedCell;
                logic.userStep(activeCell, targetCell);
                if (logic.nextStepCompFlag == true) {
                    thread = new Thread(logic);
                    thread.start();
                    logic.shuffleCells();
                }
                return;
            }
        }

        /* Right-button mouse click - deactivates any active checker */
        if (e.getButton() == 3 && !logic.inActionFlag) {
            resetActiveCell();
            return;
        }
    }
    
    void setActiveCell(Cell cell) {
        if (cell.status == CellStatus.USER_CHECKER) {
            cell.status = CellStatus.ACTIVE;
            selectingFlag = true;
            return;
        }
        if (cell.status == CellStatus.WHITE_QUEEN) {
            cell.status = CellStatus.ACTIVE_QUEEN;
            selectingFlag = true;
            return;
        }
    }

    void resetActiveCell() {
        Cell cell;
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            cell = cBoard.cells[cCounter];
            if (cell.status == CellStatus.ACTIVE) {
                cell.status = CellStatus.USER_CHECKER;
                selectingFlag = false;
                return;
            }
            if (cell.status == CellStatus.ACTIVE_QUEEN) {
                cell.status = CellStatus.WHITE_QUEEN;
                selectingFlag = false;
                return;
            }
        }
    }

    private Cell getActiveCell() {
        Cell cell;
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            cell = cBoard.cells[cCounter];
            if (cell.status == CellStatus.ACTIVE || cell.status == CellStatus.ACTIVE_QUEEN) {
                return cell;
            }
        }
        return new Cell(CellStatus.NONE);
    }
}
