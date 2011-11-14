package ru.javatalks.checkers;

import ru.javatalks.checkers.model.Cell;

import java.awt.event.*;

/** 
 *  * This class provides user's actions on chess board
 * 
 * @author Kapellan
 */
class ActionChessBoard extends MouseAdapter {

    private final ChessBoard cBoard;
    private final Painter painter;

    /* If true - we selected checker (checker is active - it is red) */
    private boolean selectingFlag = false;

    ActionChessBoard(ChessBoard cBoard, Painter painter) {
        this.cBoard = cBoard;
        this.painter = painter;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            /* Select checker - paint it red - make it active */
            Cell clickedCell = getCellByCoordinates(e.getX(), e.getY());
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
                    && (clickedCell.getStatus() == CellStatus.USER_CHECKER
                    || clickedCell.getStatus() == CellStatus.WHITE_QUEEN)) {
                resetActiveCell();
                setActiveCell(clickedCell);
                return;
            }

            /* We activated checker, so second click selects target cell */
            if (selectingFlag) {
                Cell activeCell = getActiveCell();
                painter.userStep(activeCell, clickedCell);
                if (painter.nextStepCompFlag == true) {
                    Thread thread = new Thread(painter);
                    thread.start();
                }
                return;
            }
        }

        /* Right-button mouse click - deactivates any active checker */
        if (e.getButton() == 3 && !painter.inActionFlag) {
            resetActiveCell();
            return;
        }
    }
    
    void setActiveCell(Cell cell) {
        if (cell.getStatus() == CellStatus.USER_CHECKER) {
            cell.setChecker(CellStatus.ACTIVE);
            selectingFlag = true;
            return;
        }
        if (cell.getStatus() == CellStatus.WHITE_QUEEN) {
            cell.setChecker(CellStatus.ACTIVE_QUEEN);
            selectingFlag = true;
            return;
        }
    }

    void resetActiveCell() {
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            Cell cell = cBoard.cells[cCounter];
            if (cell.getStatus() == CellStatus.ACTIVE) {
                cell.setChecker(CellStatus.USER_CHECKER);
                selectingFlag = false;
                return;
            }
            if (cell.getStatus() == CellStatus.ACTIVE_QUEEN) {
                cell.setChecker(CellStatus.WHITE_QUEEN);
                selectingFlag = false;
                return;
            }
        }
    }

    private Cell getActiveCell() {
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            Cell cell = cBoard.cells[cCounter];
            if (cell.getStatus() == CellStatus.ACTIVE || cell.getStatus() == CellStatus.ACTIVE_QUEEN) {
                return cell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    Cell getCellByCoordinates(int clickedX, int clickedY) {
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            Cell tmpCell = cBoard.cells[cCounter];
            if (clickedX >= tmpCell.cX
                    & clickedX < tmpCell.cX + ChessBoard.CELL_SIZE
                    & clickedY >= tmpCell.cY
                    & clickedY < tmpCell.cY + ChessBoard.CELL_SIZE) {
                return tmpCell;
            }
        }
        return null;
    }
}
