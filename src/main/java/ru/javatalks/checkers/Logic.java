package ru.javatalks.checkers;

import ru.javatalks.checkers.model.Cell;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** 
 * This class provides game logic of checkers - move and fight
 * 
 * @author Kapellan
 */
class Logic implements Runnable {
    /* if true - computer make's next step, else - player */
    boolean nextStepCompFlag = false;
    
    /* If true - user must continue fight  */
    boolean inActionFlag = false;
    
    private ChessBoard cBoard;
    private Menu menu;
    private String userResultCheckersNum;
    private Cell[] turkishArr = new Cell[12];

    Logic(ChessBoard cBoard, Menu menu) {
        this.cBoard = cBoard;
        this.menu = menu;
        initTurkishArr();
    }
    
    void userStep(Cell activeCell, Cell targetCell) {
        
        /*
         * There is a fighter checker,
         * but we selected other checker, which is not fighter 
         */
        if (getUserFighter().getStatus() != CellStatus.NONE && isFighter(activeCell) == false) {
            menu.resultBuf = menu.userHasFighterText + getUserFighter().getIndex() + "\n";
            menu.customResult();
            return;
        }
        
        /* First we must fight */
        if (isFighter(activeCell)) {
            Cell actCells[] = fight(activeCell, targetCell);
            Cell victimCell = actCells[0];
            if (!inActionFlag) {
                userResultCheckersNum = activeCell.getIndex();
            }
            activeCell = actCells[1];
            if (activeCell.getStatus() == CellStatus.NONE) {
                menu.resultBuf = menu.userMustFightText + "\n";                
                menu.customResult();
                return;
            }

            addToTurkishArr(victimCell);
            if (isFighter(activeCell)) {
                menu.act.setActiveCell(activeCell);
                inActionFlag = true;
                userResultCheckersNum += ":" + activeCell.getIndex();
                return;
            }
            if (!isFighter(activeCell)) {
                menu.act.resetActiveCell();
                resetTurkishArr();
                inActionFlag = false;
                userResultCheckersNum += ":" + activeCell.getIndex();
                menu.resultBuf = userResultCheckersNum + "\n";
                menu.customResult();
                nextStepCompFlag = true;                
                return;
                 
            }            
        }

        /* If there is no fighter checker we move */
        if (isMover(activeCell)) {
            Cell mCell = move(activeCell, targetCell);
            if (mCell.getStatus() == CellStatus.USER_CHECKER || mCell.getStatus() == CellStatus.WHITE_QUEEN) {
                menu.resultBuf = activeCell.getIndex() + ":" + mCell.getIndex() + "\n";
                nextStepCompFlag = true;                
            } else {
                menu.resultBuf = menu.wrongNextCellText + "\n";
            }
            menu.customResult();
            menu.tArea.setCaretPosition(menu.tArea.getDocument().getLength());
            return;
        }
    } // End of userStep

    public void run() {
        compStep();
        menu.tArea.append(menu.stepUserText + "\n");
        menu.tArea.setCaretPosition(menu.tArea.getDocument().getLength());
    }
    

    void compStep() {
        Cell actCells[];
        Cell activeCell;
        Cell victimCell;
        try {
            menu.resultBuf = menu.stepCompText + "\n";
            activeCell = getCompFighter();
            if (activeCell.getStatus() != CellStatus.NONE) {
                do {
                    menu.resultBuf += activeCell.getIndex();
                    Thread.sleep(500);
                    if (isFighter(activeCell)) {
                        menu.resultBuf += ":";
                    }
                    actCells = fight(activeCell);
                    victimCell = actCells[0];
                    activeCell = actCells[1];
                        if(isFighter(activeCell))
                        addToTurkishArr(victimCell);
                } while (activeCell.getStatus() != CellStatus.NONE);
                menu.resultBuf += "\n";
                resetTurkishArr();                
                menu.customResult();
                menu.tArea.setCaretPosition(menu.tArea.getDocument().getLength());
                nextStepCompFlag = false;               
                return;
            }
            activeCell = getCompStepper();
            if (activeCell.getStatus() != CellStatus.NONE) {
                Thread.sleep(700);
                menu.resultBuf += activeCell.getIndex();
                activeCell = move(activeCell);
                menu.resultBuf += ":" + activeCell.getIndex();
                menu.resultBuf += "\n";
                menu.customResult();
                menu.tArea.setCaretPosition(menu.tArea.getDocument().getLength());
                nextStepCompFlag = false;
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * To make right turkish blow rule
     */
    protected boolean addToTurkishArr(Cell cell) {
        cell.setChecker(CellStatus.CHECKER_IN_FIGHT);
        for (int i = 0; i < turkishArr.length; i++) {
            if (turkishArr[i].getStatus() == CellStatus.NONE) {
                turkishArr[i] = cell;
                return true;
            }
        }
        return false;
    }

    private void resetTurkishArr() {
        for (int i = 0; i < turkishArr.length; i++) {
            turkishArr[i].setChecker(CellStatus.GREY);
            turkishArr[i] = new Cell(CellStatus.NONE);
        }
    }

    private void initTurkishArr() {
        for (int i = 0; i < turkishArr.length; i++) {
            turkishArr[i] = new Cell(CellStatus.NONE);
        }
    }

    private int getRandomSign() {
        Random random = new Random();
        if (random.nextInt(2) == 0) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * We check is checker a fighter
     */
    private boolean isFighter(Cell cell) {
        Cell firstNext;
        Cell secondNext;
        /* for black checkers */
        if (cell.getStatus() == CellStatus.COMP_CHECKER) {
            /* left upper next cell */
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.USER_CHECKER || firstNext.getStatus() == CellStatus.WHITE_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.getcX() - cBoard.cellSize, firstNext.getcY() - cBoard.cellSize);
                if (secondNext.getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /* right upper cell */
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.USER_CHECKER || firstNext.getStatus() == CellStatus.WHITE_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.getcX() + cBoard.cellSize, firstNext.getcY() - cBoard.cellSize);
                if (secondNext.getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /* right bottom cell */
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.USER_CHECKER || firstNext.getStatus() == CellStatus.WHITE_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.getcX() + cBoard.cellSize, firstNext.getcY() + cBoard.cellSize);
                if (secondNext.getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /* left bottom cell */
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.USER_CHECKER || firstNext.getStatus() == CellStatus.WHITE_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.getcX() - cBoard.cellSize, firstNext.getcY() + cBoard.cellSize);
                if (secondNext.getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
        }/* for comp queen */
        if (cell.getStatus() == CellStatus.BLACK_QUEEN) {
            Cell tmpCell;
            /** Check top left diagonal from selected checker. When cells with status "2" ended, we check next cell - if it is enemy(status "3 (6)" ) 
             * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
             */
            int count = 1;
            while ((tmpCell = getCellByCoordinates(cell.getcX() - cBoard.cellSize * count, cell.getcY() - cBoard.cellSize * count)).getStatus() == CellStatus.GREY) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.USER_CHECKER || tmpCell.getStatus() == CellStatus.WHITE_QUEEN) {
                if (getCellByCoordinates(tmpCell.getcX() - cBoard.cellSize, tmpCell.getcY() - cBoard.cellSize).getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check top right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.getcX() + (cBoard.cellSize * count), cell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.USER_CHECKER || tmpCell.getStatus() == CellStatus.WHITE_QUEEN) {
                if (getCellByCoordinates(tmpCell.getcX() + cBoard.cellSize, tmpCell.getcY() - cBoard.cellSize).getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.getcX() - (cBoard.cellSize * count), cell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.USER_CHECKER || tmpCell.getStatus() == CellStatus.WHITE_QUEEN) {
                if (getCellByCoordinates(tmpCell.getcX() - cBoard.cellSize, tmpCell.getcY() + cBoard.cellSize).getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check bottom right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.getcX() + (cBoard.cellSize * count), cell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.USER_CHECKER || tmpCell.getStatus() == CellStatus.WHITE_QUEEN) {
                if (getCellByCoordinates(tmpCell.getcX() + cBoard.cellSize, tmpCell.getcY() + cBoard.cellSize).getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
        }

        /* for white checkers */
        if (cell.getStatus() == CellStatus.USER_CHECKER || cell.getStatus() == CellStatus.ACTIVE) {
            /** left up next cell */
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.COMP_CHECKER || firstNext.getStatus() == CellStatus.BLACK_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.getcX() - cBoard.cellSize, firstNext.getcY() - cBoard.cellSize);
                if (secondNext.getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** right up cell */
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.COMP_CHECKER || firstNext.getStatus() == CellStatus.BLACK_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.getcX() + cBoard.cellSize, firstNext.getcY() - cBoard.cellSize);
                if (secondNext.getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** right bottom cell */
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.COMP_CHECKER || firstNext.getStatus() == CellStatus.BLACK_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.getcX() + cBoard.cellSize, firstNext.getcY() + cBoard.cellSize);
                if (secondNext.getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** left bottom cell */
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.COMP_CHECKER || firstNext.getStatus() == CellStatus.BLACK_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.getcX() - cBoard.cellSize, firstNext.getcY() + cBoard.cellSize);
                if (secondNext.getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
        }
        /* for user queen and active queen */
        if (cell.getStatus() == CellStatus.WHITE_QUEEN || cell.getStatus() == CellStatus.ACTIVE_QUEEN) {
            Cell tmpCell;
            /** Check top left diagonal from selected checker. When cells with status "2" ended, we check next cell - if it is enemy(status "4 (7)" ) 
             * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
             */
            int count = 1;
            while (((tmpCell = getCellByCoordinates(cell.getcX() - (cBoard.cellSize * count), cell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.COMP_CHECKER || tmpCell.getStatus() == CellStatus.BLACK_QUEEN) {
                if (getCellByCoordinates(tmpCell.getcX() - cBoard.cellSize, tmpCell.getcY() - cBoard.cellSize).getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check top right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.getcX() + (cBoard.cellSize * count), cell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.COMP_CHECKER || tmpCell.getStatus() == CellStatus.BLACK_QUEEN) {
                if (getCellByCoordinates(tmpCell.getcX() + cBoard.cellSize, tmpCell.getcY() - cBoard.cellSize).getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.getcX() - (cBoard.cellSize * count), cell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.COMP_CHECKER || tmpCell.getStatus() == CellStatus.BLACK_QUEEN) {
                if (getCellByCoordinates(tmpCell.getcX() - cBoard.cellSize, tmpCell.getcY() + cBoard.cellSize).getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check bottom right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.getcX() + (cBoard.cellSize * count), cell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.COMP_CHECKER || tmpCell.getStatus() == CellStatus.BLACK_QUEEN) {
                if (getCellByCoordinates(tmpCell.getcX() + cBoard.cellSize, tmpCell.getcY() + cBoard.cellSize).getStatus() == CellStatus.GREY) {
                    return true;
                }
            }
        }
        return false;
    }//end of isFighter()

    /** find comp fighting checker */
    Cell getCompFighter() {
        Cell fighterCell = null;
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            fighterCell = cBoard.cells[cCounter];
            if ((fighterCell.getStatus() == CellStatus.COMP_CHECKER || fighterCell.getStatus() == CellStatus.BLACK_QUEEN) && isFighter(fighterCell)) {
                return fighterCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    /** find user fighting checker */
    Cell getUserFighter() {
        Cell fighterCell;
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            fighterCell = cBoard.cells[cCounter];
            if ((fighterCell.getStatus() == CellStatus.USER_CHECKER || fighterCell.getStatus() == CellStatus.ACTIVE || fighterCell.getStatus() == CellStatus.WHITE_QUEEN || fighterCell.getStatus() == CellStatus.ACTIVE_QUEEN) && isFighter(fighterCell)) {
                return fighterCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    Cell getUserStepper() {
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            Cell mCell = cBoard.cells[cCounter];
            if ((mCell.getStatus() == CellStatus.USER_CHECKER || mCell.getStatus() == CellStatus.ACTIVE || mCell.getStatus() == CellStatus.WHITE_QUEEN || mCell.getStatus() == CellStatus.ACTIVE_QUEEN) && isMover(mCell)) {
                return mCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    private boolean isMover(Cell cell) {
        Cell firstNext;
        /* for black checkers */
        if (cell.getStatus() == CellStatus.COMP_CHECKER) {
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }

        }
        if (cell.getStatus() == CellStatus.BLACK_QUEEN) {
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
        }
        /* for white (user checkers) */
        if (cell.getStatus() == CellStatus.USER_CHECKER || cell.getStatus() == CellStatus.ACTIVE) {
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }

        }
        if (cell.getStatus() == CellStatus.WHITE_QUEEN || cell.getStatus() == CellStatus.ACTIVE_QUEEN) {
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() - cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.getcX() - cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.getcX() + cBoard.cellSize, cell.getcY() + cBoard.cellSize);
            if (firstNext.getStatus() == CellStatus.GREY) {
                return true;
            }
        }
        return false;
    } //end isMover()

    Cell getCompStepper() {
        Cell stepperCell = null;
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            stepperCell = cBoard.cells[cCounter];
            if ((stepperCell.getStatus() == CellStatus.COMP_CHECKER || stepperCell.getStatus() == CellStatus.BLACK_QUEEN) && isMover(stepperCell)) {
                return stepperCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    private Cell[] fight(Cell fCell) {
        Cell retCell[] = {new Cell(CellStatus.NONE), new Cell(CellStatus.NONE)};
        Cell victimCell;
        Cell targetCell;

        if (fCell.getStatus() == CellStatus.COMP_CHECKER) {
            int randomSignX = getRandomSign();
            int clickedXrand = fCell.getcX() + cBoard.cellSize * randomSignX;
            int clickedY = fCell.getcY() + cBoard.cellSize;
            int clickedXrev = fCell.getcX() + cBoard.cellSize * randomSignX * (-1);
            int clickedYrev = fCell.getcY() - cBoard.cellSize;

            victimCell = getCellByCoordinates(clickedXrand, clickedY);
            if (victimCell.getStatus() == CellStatus.USER_CHECKER || victimCell.getStatus() == CellStatus.WHITE_QUEEN) {
                targetCell = getCellByCoordinates(victimCell.getcX() + cBoard.cellSize * randomSignX, victimCell.getcY() + cBoard.cellSize);
                if (targetCell.getStatus() == CellStatus.GREY) {
                    victimCell.setChecker(CellStatus.GREY);
                    targetCell.setChecker(CellStatus.COMP_CHECKER);
                    if (checkQeen(targetCell)) {
                        targetCell.setChecker(CellStatus.BLACK_QUEEN);
                    }
                    fCell.setChecker(CellStatus.GREY);
                    cBoard.userCheckers--;
                    retCell[0] = victimCell;
                    retCell[1] = targetCell;
                    return retCell;
                }
            }
            // if while did not breake we take opposite element by X
            victimCell = getCellByCoordinates(clickedXrev, clickedY);
            if (victimCell.getStatus() == CellStatus.USER_CHECKER || victimCell.getStatus() == CellStatus.WHITE_QUEEN) {
                targetCell = getCellByCoordinates(victimCell.getcX() + cBoard.cellSize * randomSignX * (-1), victimCell.getcY() + cBoard.cellSize);
                if (targetCell.getStatus() == CellStatus.GREY) {
                    victimCell.setChecker(CellStatus.GREY);
                    targetCell.setChecker(CellStatus.COMP_CHECKER);
                    if (checkQeen(targetCell)) {
                        targetCell.setChecker(CellStatus.BLACK_QUEEN);
                    }
                    fCell.setChecker(CellStatus.GREY);
                    cBoard.userCheckers--;
                    retCell[0] = victimCell;
                    retCell[1] = targetCell;
                    return retCell;
                }
            }
            ///////// The same for reverse fight (up by Y)
            victimCell = getCellByCoordinates(clickedXrand, clickedYrev);
            if (victimCell.getStatus() == CellStatus.USER_CHECKER || victimCell.getStatus() == CellStatus.WHITE_QUEEN) {
                targetCell = getCellByCoordinates(victimCell.getcX() + cBoard.cellSize * randomSignX, victimCell.getcY() - cBoard.cellSize);
                if (targetCell.getStatus() == CellStatus.GREY) {
                    victimCell.setChecker(CellStatus.GREY);
                    targetCell.setChecker(CellStatus.COMP_CHECKER);
                    if (checkQeen(targetCell)) {
                        targetCell.setChecker(CellStatus.BLACK_QUEEN);
                    }
                    fCell.setChecker(CellStatus.GREY);
                    cBoard.userCheckers--;
                    retCell[0] = victimCell;
                    retCell[1] = targetCell;
                    return retCell;
                }
            }
            // if while did not breake we take opposite element by X
            victimCell = getCellByCoordinates(clickedXrev, clickedYrev);
            if (victimCell.getStatus() == CellStatus.USER_CHECKER || victimCell.getStatus() == CellStatus.WHITE_QUEEN) {
                targetCell = getCellByCoordinates(victimCell.getcX() + cBoard.cellSize * randomSignX * (-1), victimCell.getcY() - cBoard.cellSize);
                if (targetCell.getStatus() == CellStatus.GREY) {
                    victimCell.setChecker(CellStatus.GREY);
                    targetCell.setChecker(CellStatus.COMP_CHECKER);
                    if (checkQeen(targetCell)) {
                        targetCell.setChecker(CellStatus.BLACK_QUEEN);
                    }
                    fCell.setChecker(CellStatus.GREY);
                    cBoard.userCheckers--;
                    retCell[0] = victimCell;
                    retCell[1] = targetCell;
                    return retCell;
                }
            }
        }
        if (fCell.getStatus() == CellStatus.BLACK_QUEEN) {
            Cell tmpCell;
            int count = 1;
            while (((tmpCell = getCellByCoordinates(fCell.getcX() - (cBoard.cellSize * count), fCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.USER_CHECKER || tmpCell.getStatus() == CellStatus.WHITE_QUEEN) {
                count++;
                victimCell = tmpCell;
                tmpCell = (getCellByCoordinates(fCell.getcX() - (cBoard.cellSize * count), fCell.getcY() - (cBoard.cellSize * count)));
                while (((tmpCell = getCellByCoordinates(fCell.getcX() - (cBoard.cellSize * count), fCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                    targetCell = tmpCell;
                    if (targetCell.equals(tmpCell)) {
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.BLACK_QUEEN);
                        fCell.setChecker(CellStatus.GREY);
                        cBoard.userCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                    count++;
                }
            }
            /** Check top right diagonal from selected checker.  */
            count = 1;
            while (((tmpCell = getCellByCoordinates(fCell.getcX() + (cBoard.cellSize * count), fCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.USER_CHECKER || tmpCell.getStatus() == CellStatus.WHITE_QUEEN) {
                count++;
                victimCell = tmpCell;
                tmpCell = (getCellByCoordinates(fCell.getcX() + (cBoard.cellSize * count), fCell.getcY() - (cBoard.cellSize * count)));
                while (((tmpCell = getCellByCoordinates(fCell.getcX() + (cBoard.cellSize * count), fCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                    targetCell = tmpCell;
                    if (targetCell.equals(tmpCell)) {
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.BLACK_QUEEN);
                        fCell.setChecker(CellStatus.GREY);
                        cBoard.userCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                    count++;
                }
            }
            /** Check bootom left diagonal from selected checker.  */
            count = 1;
            while (((tmpCell = getCellByCoordinates(fCell.getcX() - (cBoard.cellSize * count), fCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.USER_CHECKER || tmpCell.getStatus() == CellStatus.WHITE_QUEEN) {
                count++;
                victimCell = tmpCell;
                tmpCell = (getCellByCoordinates(fCell.getcX() - (cBoard.cellSize * count), fCell.getcY() + (cBoard.cellSize * count)));
                targetCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(fCell.getcX() - (cBoard.cellSize * count), fCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                    targetCell = tmpCell;
                    if (targetCell.equals(tmpCell)) {
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.BLACK_QUEEN);
                        fCell.setChecker(CellStatus.GREY);
                        cBoard.userCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                    count++;
                }
            }
            /** Check bootom right diagonal from selected checker.  */
            count = 1;
            while (((tmpCell = getCellByCoordinates(fCell.getcX() + (cBoard.cellSize * count), fCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.USER_CHECKER || tmpCell.getStatus() == CellStatus.WHITE_QUEEN) {
                count++;
                victimCell = tmpCell;
                targetCell = (getCellByCoordinates(fCell.getcX() + (cBoard.cellSize * count), fCell.getcY() + (cBoard.cellSize * count)));
                while (((tmpCell = getCellByCoordinates(fCell.getcX() + (cBoard.cellSize * count), fCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.BLACK_QUEEN);
                        fCell.setChecker(CellStatus.GREY);
                        cBoard.userCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                    count++;
                }
            }

        }

        retCell[0] = new Cell(CellStatus.NONE);
        retCell[1] = new Cell(CellStatus.NONE);
        return retCell;
    }

    private Cell[] fight(Cell activeCell, Cell targetCell) {
        Cell retCell[] = {new Cell(CellStatus.NONE), new Cell(CellStatus.NONE)};
        Cell victimCell;

        if (activeCell.getStatus() == CellStatus.ACTIVE) {
            /* Verify that second upper left cell from our active checker is free   */
            if (targetCell.equals(getCellByCoordinates(activeCell.getcX() - 2 * cBoard.cellSize, activeCell.getcY() - 2 * cBoard.cellSize))) {
                if (targetCell.getStatus() == CellStatus.GREY) {
                    /* Verify that first upper left cell from our active checker is enemy (status 4 or 7) */
                    victimCell = getCellByCoordinates(activeCell.getcX() - cBoard.cellSize, activeCell.getcY() - cBoard.cellSize);
                    if (victimCell.getStatus() == CellStatus.COMP_CHECKER || victimCell.getStatus() == CellStatus.BLACK_QUEEN) {
                        activeCell.setChecker(CellStatus.GREY);
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.USER_CHECKER);
                        if (checkQeen(targetCell)) {
                            targetCell.setChecker(CellStatus.WHITE_QUEEN);
                        }
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;

                    }
                }

            }
            if (targetCell.equals(getCellByCoordinates(activeCell.getcX() + 2 * cBoard.cellSize, activeCell.getcY() - 2 * cBoard.cellSize))) {
                if (targetCell.getStatus() == CellStatus.GREY) {
                    victimCell = getCellByCoordinates(activeCell.getcX() + cBoard.cellSize, activeCell.getcY() - cBoard.cellSize);
                    if (victimCell.getStatus() == CellStatus.COMP_CHECKER || victimCell.getStatus() == CellStatus.BLACK_QUEEN) {
                        activeCell.setChecker(CellStatus.GREY);
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.USER_CHECKER);
                        if (checkQeen(targetCell)) {
                            targetCell.setChecker(CellStatus.WHITE_QUEEN);
                        }
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                }


                /* REVERSE FIGHT
                 ******************
                 * left bottom cell 
                 */
            }
            if (targetCell.equals(getCellByCoordinates(activeCell.getcX() - 2 * cBoard.cellSize, activeCell.getcY() + 2 * cBoard.cellSize))) {
                if (targetCell.getStatus() == CellStatus.GREY) {
                    victimCell = getCellByCoordinates(activeCell.getcX() - cBoard.cellSize, activeCell.getcY() + cBoard.cellSize);
                    if (victimCell.getStatus() == CellStatus.COMP_CHECKER || victimCell.getStatus() == CellStatus.BLACK_QUEEN) {
                        activeCell.setChecker(CellStatus.GREY);
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.USER_CHECKER);
                        if (checkQeen(targetCell)) {
                            targetCell.setChecker(CellStatus.WHITE_QUEEN);
                        }
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                }

                /* right bottom cell */            }
            if (targetCell.equals(getCellByCoordinates(activeCell.getcX() + 2 * cBoard.cellSize, activeCell.getcY() + 2 * cBoard.cellSize))) {
                if (targetCell.getStatus() == CellStatus.GREY) {
                    victimCell = getCellByCoordinates(activeCell.getcX() + cBoard.cellSize, activeCell.getcY() + cBoard.cellSize);
                    if (victimCell.getStatus() == CellStatus.COMP_CHECKER || victimCell.getStatus() == CellStatus.BLACK_QUEEN) {
                        activeCell.setChecker(CellStatus.GREY);
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.USER_CHECKER);
                        if (checkQeen(targetCell)) {
                            targetCell.setChecker(CellStatus.WHITE_QUEEN);
                        }
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                }
            }
        }
        if (activeCell.getStatus() == CellStatus.ACTIVE_QUEEN) {
            Cell tmpCell;
            /** Check top left diagonal from selected cell. When cells with status "2" ended, we check next cell - if it is enemy(status "4 (7)" ) 
             * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
             * 
             * top left diagonal 
             */            
            int count = 1;
            while (((tmpCell = getCellByCoordinates(activeCell.getcX() - (cBoard.cellSize * count), activeCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.COMP_CHECKER || tmpCell.getStatus() == CellStatus.BLACK_QUEEN) {
                count++;
                victimCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(activeCell.getcX() - (cBoard.cellSize * count), activeCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.WHITE_QUEEN);
                        activeCell.setChecker(CellStatus.GREY);
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                    count++;
                }
            }
            /** Check top right diagonal from selected checker.  */
            count = 1;
            while (((tmpCell = getCellByCoordinates(activeCell.getcX() + (cBoard.cellSize * count), activeCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.COMP_CHECKER || tmpCell.getStatus() == CellStatus.BLACK_QUEEN) {
                count++;
                victimCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(activeCell.getcX() + (cBoard.cellSize * count), activeCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.WHITE_QUEEN);
                        activeCell.setChecker(CellStatus.GREY);
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                    count++;
                }
            }
            /** Check bootom left diagonal from selected checker.  */
            count = 1;
            while (((tmpCell = getCellByCoordinates(activeCell.getcX() - (cBoard.cellSize * count), activeCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.COMP_CHECKER || tmpCell.getStatus() == CellStatus.BLACK_QUEEN) {
                count++;
                victimCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(activeCell.getcX() - (cBoard.cellSize * count), activeCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.WHITE_QUEEN);
                        activeCell.setChecker(CellStatus.GREY);
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                    count++;
                }
            }
            /** Check bootom right diagonal from selected checker.  */
            count = 1;
            while (((tmpCell = getCellByCoordinates(activeCell.getcX() + (cBoard.cellSize * count), activeCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.getStatus() == CellStatus.COMP_CHECKER || tmpCell.getStatus() == CellStatus.BLACK_QUEEN) {
                count++;
                victimCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(activeCell.getcX() + (cBoard.cellSize * count), activeCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.setChecker(CellStatus.GREY);
                        targetCell.setChecker(CellStatus.WHITE_QUEEN);
                        activeCell.setChecker(CellStatus.GREY);
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                    count++;
                }
            }
        }
        retCell[0] = new Cell(CellStatus.NONE);
        retCell[1] = new Cell(CellStatus.NONE);
        return retCell;
    }

    /** Computer move */
    private Cell move(Cell mCell) {
        Random random = new Random();
        if (mCell.getStatus() == CellStatus.COMP_CHECKER) {
            int randomSignX = getRandomSign();
            int clickedXrand = mCell.getcX() + cBoard.cellSize * randomSignX;
            int clickedY = mCell.getcY() + cBoard.cellSize;
            int clickedXrev = mCell.getcX() - cBoard.cellSize * randomSignX;
            Cell targetCell;

            targetCell = getCellByCoordinates(clickedXrand, clickedY);
            if (targetCell.getStatus() == CellStatus.GREY) {
                targetCell.setChecker(CellStatus.COMP_CHECKER);
                if (checkQeen(targetCell)) {
                    targetCell.setChecker(CellStatus.BLACK_QUEEN);
                }
                mCell.setChecker(CellStatus.GREY);
                return targetCell;
            }
            targetCell = getCellByCoordinates(clickedXrev, clickedY);
            if (targetCell.getStatus() == CellStatus.GREY) {
                targetCell.setChecker(CellStatus.COMP_CHECKER);
                if (checkQeen(targetCell)) {
                    targetCell.setChecker(CellStatus.BLACK_QUEEN);
                }
                mCell.setChecker(CellStatus.GREY);
                return targetCell;
            }
        }
        if (mCell.getStatus() == CellStatus.BLACK_QUEEN) {
            Cell tmpCell;
            Cell targetCell;
            Cell queenPossibleStep[] = new Cell[8];
            
            /* Queen can take any cell after fight, save possible cells to array */
            queenPossibleStep[0] = new Cell(CellStatus.NONE);
            
            /** Check top left diagonal from selected cell. When cells with status "2" ended, we check next cell - if it is enemy(status "3 (6)" ) 
             * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
             * top left
             */
            int count = 1;
            while (((tmpCell = getCellByCoordinates(mCell.getcX() - (cBoard.cellSize * count), mCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                queenPossibleStep[count - 1] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].getStatus() != CellStatus.NONE) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.setChecker(CellStatus.BLACK_QUEEN);
                mCell.setChecker(CellStatus.GREY);
                return targetCell;
            }
            /** Check top right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(mCell.getcX() + (cBoard.cellSize * count), mCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                queenPossibleStep[count - 1] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].getStatus() != CellStatus.NONE) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.setChecker(CellStatus.BLACK_QUEEN);
                mCell.setChecker(CellStatus.GREY);
                return targetCell;
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(mCell.getcX() - (cBoard.cellSize * count), mCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                queenPossibleStep[count - 1] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].getStatus() != CellStatus.NONE) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.setChecker(CellStatus.BLACK_QUEEN);
                mCell.setChecker(CellStatus.GREY);
                return targetCell;
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(mCell.getcX() + (cBoard.cellSize * count), mCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY)) {
                queenPossibleStep[count - 1] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].getStatus() != CellStatus.NONE) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.setChecker(CellStatus.BLACK_QUEEN);
                mCell.setChecker(CellStatus.GREY);
                return targetCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    /** User move */
    private Cell move(Cell activeCell, Cell targetCell) {
        if (activeCell.getStatus() == CellStatus.ACTIVE) {
            /** Target cell is top upper left cell from our active cell, check it, and it is free - paint by own checker
                top left
             */            
            if (targetCell.equals(getCellByCoordinates(activeCell.getcX() - cBoard.cellSize, activeCell.getcY() - cBoard.cellSize))) {
                if (targetCell.getStatus() == CellStatus.GREY) {
                    targetCell.setChecker(CellStatus.USER_CHECKER);
                    if (checkQeen(targetCell)) {
                        targetCell.setChecker(CellStatus.WHITE_QUEEN);
                    }
                    activeCell.setChecker(CellStatus.GREY);
                    return targetCell;
                }
            }
            if (targetCell.equals(getCellByCoordinates(activeCell.getcX() + cBoard.cellSize, activeCell.getcY() - cBoard.cellSize))) {
                /**  top right */
                if (targetCell.getStatus() == CellStatus.GREY) {
                    targetCell.setChecker(CellStatus.USER_CHECKER);
                    if (checkQeen(targetCell)) {
                        targetCell.setChecker(CellStatus.WHITE_QUEEN);
                    }
                    activeCell.setChecker(CellStatus.GREY);
                    return targetCell;
                }
            }
        }
        if (activeCell.getStatus() == CellStatus.ACTIVE_QUEEN) {
            Cell tmpCell;
            /** Check top left diagonal from selected checker */
            int count = 1;
            while ((tmpCell = getCellByCoordinates(activeCell.getcX() - (cBoard.cellSize * count), activeCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY) {
                if (tmpCell.equals(targetCell)) {
                    activeCell.setChecker(CellStatus.GREY);
                    tmpCell.setChecker(CellStatus.WHITE_QUEEN);
                    return tmpCell;
                }
                count++;
            }
            /** Check top right diagonal from selected checker */
            count = 1;
            while ((tmpCell = getCellByCoordinates(activeCell.getcX() + (cBoard.cellSize * count), activeCell.getcY() - (cBoard.cellSize * count))).getStatus() == CellStatus.GREY) {
                if (tmpCell.equals(targetCell)) {
                    activeCell.setChecker(CellStatus.GREY);
                    tmpCell.setChecker(CellStatus.WHITE_QUEEN);
                    return tmpCell;
                }
                count++;
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while ((tmpCell = getCellByCoordinates(activeCell.getcX() - (cBoard.cellSize * count), activeCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY) {
                if (tmpCell.equals(targetCell)) {
                    activeCell.setChecker(CellStatus.GREY);
                    tmpCell.setChecker(CellStatus.WHITE_QUEEN);
                    return tmpCell;
                }
                count++;
            }
            /** Check bottom right diagonal from selected checker */
            count = 1;
            while ((tmpCell = getCellByCoordinates(activeCell.getcX() + (cBoard.cellSize * count), activeCell.getcY() + (cBoard.cellSize * count))).getStatus() == CellStatus.GREY) {
                if (tmpCell.equals(targetCell)) {
                    activeCell.setChecker(CellStatus.GREY);
                    tmpCell.setChecker(CellStatus.WHITE_QUEEN);
                    return tmpCell;
                }
                count++;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    private boolean checkQeen(Cell cell) {
        String userIndexQ[] = {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};
        String compIndexQ[] = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"};
        if (cell.getStatus() == CellStatus.USER_CHECKER) {
            for (int i = 0; i < userIndexQ.length; i++) {
                if (userIndexQ[i].equals(cell.getIndex())) {
                    return true;
                }
            }
        }
        if (cell.getStatus() == CellStatus.COMP_CHECKER) {
            for (int i = 0; i < userIndexQ.length; i++) {
                if (compIndexQ[i].equals(cell.getIndex())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    void shuffleCells() {
        List<Cell> list = Arrays.asList(cBoard.cells);
        Collections.shuffle(list);
        cBoard.cells = list.toArray(cBoard.cells);
    }

}
