package ru.javatalks.checkers;

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
    private Cell turkishArr[] = new Cell[12];
    
    
    void userStep(Cell activeCell, Cell targetCell) {
        
        /*
         * There is a fighter checker,
         * but we selected other checker, which is not fighter 
         */
        if (getUserFighter().status != CellStatus.NONE && isFighter(activeCell) == false) {
            menu.resultBuf = menu.userHasFighterText + getUserFighter().index + "\n";
            menu.customResult();
            return;
        }
        
        /* First we must fight */
        if (isFighter(activeCell)) {
            Cell actCells[] = fight(activeCell, targetCell);
            Cell victimCell = actCells[0];
            if (!inActionFlag) {
                userResultCheckersNum = activeCell.index;
            }
            activeCell = actCells[1];
            if (activeCell.status == CellStatus.NONE) {
                menu.resultBuf = menu.userMustFightText + "\n";                
                menu.customResult();
                return;
            }

            addToTurkishArr(victimCell);
            if (isFighter(activeCell)) {
                menu.act.setActiveCell(activeCell);
                inActionFlag = true;
                userResultCheckersNum += ":" + activeCell.index;
                return;
            }
            if (!isFighter(activeCell)) {
                menu.act.resetActiveCell();
                resetTurkishArr();
                inActionFlag = false;
                userResultCheckersNum += ":" + activeCell.index;
                menu.resultBuf = userResultCheckersNum + "\n";
                menu.customResult();
                nextStepCompFlag = true;                
                return;
                 
            }            
        }

        /* If there is no fighter checker we move */
        if (isMover(activeCell)) {
            Cell mCell = move(activeCell, targetCell);
            if (mCell.status == CellStatus.USER_CHECKER || mCell.status == CellStatus.WHITE_QUEEN) {
                menu.resultBuf = activeCell.index + ":" + mCell.index + "\n";
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
            if (activeCell.status != CellStatus.NONE) {
                do {
                    menu.resultBuf += activeCell.index;
                    Thread.sleep(500);
                    if (isFighter(activeCell)) {
                        menu.resultBuf += ":";
                    }
                    actCells = fight(activeCell);
                    victimCell = actCells[0];
                    activeCell = actCells[1];
                        if(isFighter(activeCell))
                        addToTurkishArr(victimCell);
                } while (activeCell.status != CellStatus.NONE);
                menu.resultBuf += "\n";
                resetTurkishArr();                
                menu.customResult();
                menu.tArea.setCaretPosition(menu.tArea.getDocument().getLength());
                nextStepCompFlag = false;               
                return;
            }
            activeCell = getCompStepper();
            if (activeCell.status != CellStatus.NONE) {
                Thread.sleep(700);
                menu.resultBuf += activeCell.index;
                activeCell = move(activeCell);
                menu.resultBuf += ":" + activeCell.index;
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
        cell.status = CellStatus.CHECKER_IN_FIGHT;
        for (int i = 0; i < turkishArr.length; i++) {
            if (turkishArr[i].status == CellStatus.NONE) {
                turkishArr[i] = cell;
                return true;
            }
        }
        return false;
    }

    private void resetTurkishArr() {
        for (int i = 0; i < turkishArr.length; i++) {
            turkishArr[i].status = CellStatus.GREY;
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

    /** We check is checker a fighter */
    private boolean isFighter(Cell cell) {
        Cell firstNext;
        Cell secondNext;
        /* for black checkers */
        if (cell.status == CellStatus.COMP_CHECKER) {
            /* left upper next cell */
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.USER_CHECKER || firstNext.status == CellStatus.WHITE_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.cX - cBoard.cellSize, firstNext.cY - cBoard.cellSize);
                if (secondNext.status == CellStatus.GREY) {
                    return true;
                }
            }
            /* right upper cell */
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.USER_CHECKER || firstNext.status == CellStatus.WHITE_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.cX + cBoard.cellSize, firstNext.cY - cBoard.cellSize);
                if (secondNext.status == CellStatus.GREY) {
                    return true;
                }
            }
            /* right bottom cell */
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.USER_CHECKER || firstNext.status == CellStatus.WHITE_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.cX + cBoard.cellSize, firstNext.cY + cBoard.cellSize);
                if (secondNext.status == CellStatus.GREY) {
                    return true;
                }
            }
            /* left bottom cell */
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.USER_CHECKER || firstNext.status == CellStatus.WHITE_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.cX - cBoard.cellSize, firstNext.cY + cBoard.cellSize);
                if (secondNext.status == CellStatus.GREY) {
                    return true;
                }
            }
        }/* for comp queen */
        if (cell.status == CellStatus.BLACK_QUEEN) {
            Cell tmpCell;
            /** Check top left diagonal from selected checker. When cells with status "2" ended, we check next cell - if it is enemy(status "3 (6)" ) 
             * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
             */
            int count = 1;
            while (((tmpCell = getCellByCoordinates(cell.cX - (cBoard.cellSize * count), cell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.USER_CHECKER || tmpCell.status == CellStatus.WHITE_QUEEN) {
                if (getCellByCoordinates(tmpCell.cX - cBoard.cellSize, tmpCell.cY - cBoard.cellSize).status == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check top right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.cX + (cBoard.cellSize * count), cell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.USER_CHECKER || tmpCell.status == CellStatus.WHITE_QUEEN) {
                if (getCellByCoordinates(tmpCell.cX + cBoard.cellSize, tmpCell.cY - cBoard.cellSize).status == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.cX - (cBoard.cellSize * count), cell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.USER_CHECKER || tmpCell.status == CellStatus.WHITE_QUEEN) {
                if (getCellByCoordinates(tmpCell.cX - cBoard.cellSize, tmpCell.cY + cBoard.cellSize).status == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check bottom right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.cX + (cBoard.cellSize * count), cell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.USER_CHECKER || tmpCell.status == CellStatus.WHITE_QUEEN) {
                if (getCellByCoordinates(tmpCell.cX + cBoard.cellSize, tmpCell.cY + cBoard.cellSize).status == CellStatus.GREY) {
                    return true;
                }
            }
        }

        /* for white checkers */
        if (cell.status == CellStatus.USER_CHECKER || cell.status == CellStatus.ACTIVE) {
            /** left up next cell */
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.COMP_CHECKER || firstNext.status == CellStatus.BLACK_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.cX - cBoard.cellSize, firstNext.cY - cBoard.cellSize);
                if (secondNext.status == CellStatus.GREY) {
                    return true;
                }
            }
            /** right up cell */
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.COMP_CHECKER || firstNext.status == CellStatus.BLACK_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.cX + cBoard.cellSize, firstNext.cY - cBoard.cellSize);
                if (secondNext.status == CellStatus.GREY) {
                    return true;
                }
            }
            /** right bottom cell */
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.COMP_CHECKER || firstNext.status == CellStatus.BLACK_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.cX + cBoard.cellSize, firstNext.cY + cBoard.cellSize);
                if (secondNext.status == CellStatus.GREY) {
                    return true;
                }
            }
            /** left bottom cell */
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.COMP_CHECKER || firstNext.status == CellStatus.BLACK_QUEEN) {
                secondNext = getCellByCoordinates(firstNext.cX - cBoard.cellSize, firstNext.cY + cBoard.cellSize);
                if (secondNext.status == CellStatus.GREY) {
                    return true;
                }
            }
        }
        /* for user queen and active queen */
        if (cell.status == CellStatus.WHITE_QUEEN || cell.status == CellStatus.ACTIVE_QUEEN) {
            Cell tmpCell;
            /** Check top left diagonal from selected checker. When cells with status "2" ended, we check next cell - if it is enemy(status "4 (7)" ) 
             * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
             */
            int count = 1;
            while (((tmpCell = getCellByCoordinates(cell.cX - (cBoard.cellSize * count), cell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.COMP_CHECKER || tmpCell.status == CellStatus.BLACK_QUEEN) {
                if (getCellByCoordinates(tmpCell.cX - cBoard.cellSize, tmpCell.cY - cBoard.cellSize).status == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check top right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.cX + (cBoard.cellSize * count), cell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.COMP_CHECKER || tmpCell.status == CellStatus.BLACK_QUEEN) {
                if (getCellByCoordinates(tmpCell.cX + cBoard.cellSize, tmpCell.cY - cBoard.cellSize).status == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.cX - (cBoard.cellSize * count), cell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.COMP_CHECKER || tmpCell.status == CellStatus.BLACK_QUEEN) {
                if (getCellByCoordinates(tmpCell.cX - cBoard.cellSize, tmpCell.cY + cBoard.cellSize).status == CellStatus.GREY) {
                    return true;
                }
            }
            /** Check bottom right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(cell.cX + (cBoard.cellSize * count), cell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.COMP_CHECKER || tmpCell.status == CellStatus.BLACK_QUEEN) {
                if (getCellByCoordinates(tmpCell.cX + cBoard.cellSize, tmpCell.cY + cBoard.cellSize).status == CellStatus.GREY) {
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
            if ((fighterCell.status == CellStatus.COMP_CHECKER || fighterCell.status == CellStatus.BLACK_QUEEN) && isFighter(fighterCell)) {
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
            if ((fighterCell.status == CellStatus.USER_CHECKER || fighterCell.status == CellStatus.ACTIVE || fighterCell.status == CellStatus.WHITE_QUEEN || fighterCell.status == CellStatus.ACTIVE_QUEEN) && isFighter(fighterCell)) {
                return fighterCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    Cell getUserStepper() {
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            Cell mCell = cBoard.cells[cCounter];
            if ((mCell.status == CellStatus.USER_CHECKER || mCell.status == CellStatus.ACTIVE || mCell.status == CellStatus.WHITE_QUEEN || mCell.status == CellStatus.ACTIVE_QUEEN) && isMover(mCell)) {
                return mCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    private boolean isMover(Cell cell) {
        Cell firstNext;
        /* for black checkers */
        if (cell.status == CellStatus.COMP_CHECKER) {
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }

        }
        if (cell.status == CellStatus.BLACK_QUEEN) {
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
        }
        /* for white (user checkers) */
        if (cell.status == CellStatus.USER_CHECKER || cell.status == CellStatus.ACTIVE) {
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }

        }
        if (cell.status == CellStatus.WHITE_QUEEN || cell.status == CellStatus.ACTIVE_QUEEN) {
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY - cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.cX - cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
            firstNext = getCellByCoordinates(cell.cX + cBoard.cellSize, cell.cY + cBoard.cellSize);
            if (firstNext.status == CellStatus.GREY) {
                return true;
            }
        }
        return false;
    } //end isMover()

    Cell getCompStepper() {
        Cell stepperCell = null;
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            stepperCell = cBoard.cells[cCounter];
            if ((stepperCell.status == CellStatus.COMP_CHECKER || stepperCell.status == CellStatus.BLACK_QUEEN) && isMover(stepperCell)) {
                return stepperCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    private Cell[] fight(Cell fCell) {
        Cell retCell[] = {new Cell(CellStatus.NONE), new Cell(CellStatus.NONE)};
        Cell victimCell;
        Cell targetCell;

        if (fCell.status == CellStatus.COMP_CHECKER) {
            int randomSignX = getRandomSign();
            int clickedXrand = fCell.cX + cBoard.cellSize * randomSignX;
            int clickedY = fCell.cY + cBoard.cellSize;
            int clickedXrev = fCell.cX + cBoard.cellSize * randomSignX * (-1);
            int clickedYrev = fCell.cY - cBoard.cellSize;

            victimCell = getCellByCoordinates(clickedXrand, clickedY);
            if (victimCell.status == CellStatus.USER_CHECKER || victimCell.status == CellStatus.WHITE_QUEEN) {
                targetCell = getCellByCoordinates(victimCell.cX + cBoard.cellSize * randomSignX, victimCell.cY + cBoard.cellSize);
                if (targetCell.status == CellStatus.GREY) {
                    victimCell.status = CellStatus.GREY;
                    targetCell.status = CellStatus.COMP_CHECKER;
                    if (checkQeen(targetCell)) {
                        targetCell.status = CellStatus.BLACK_QUEEN;
                    }
                    fCell.status = CellStatus.GREY;
                    cBoard.userCheckers--;
                    retCell[0] = victimCell;
                    retCell[1] = targetCell;
                    return retCell;
                }
            }
            // if while did not breake we take opposite element by X
            victimCell = getCellByCoordinates(clickedXrev, clickedY);
            if (victimCell.status == CellStatus.USER_CHECKER || victimCell.status == CellStatus.WHITE_QUEEN) {
                targetCell = getCellByCoordinates(victimCell.cX + cBoard.cellSize * randomSignX * (-1), victimCell.cY + cBoard.cellSize);
                if (targetCell.status == CellStatus.GREY) {
                    victimCell.status = CellStatus.GREY;
                    targetCell.status = CellStatus.COMP_CHECKER;
                    if (checkQeen(targetCell)) {
                        targetCell.status = CellStatus.BLACK_QUEEN;
                    }
                    fCell.status = CellStatus.GREY;
                    cBoard.userCheckers--;
                    retCell[0] = victimCell;
                    retCell[1] = targetCell;
                    return retCell;
                }
            }
            ///////// The same for reverse fight (up by Y)
            victimCell = getCellByCoordinates(clickedXrand, clickedYrev);
            if (victimCell.status == CellStatus.USER_CHECKER || victimCell.status == CellStatus.WHITE_QUEEN) {
                targetCell = getCellByCoordinates(victimCell.cX + cBoard.cellSize * randomSignX, victimCell.cY - cBoard.cellSize);
                if (targetCell.status == CellStatus.GREY) {
                    victimCell.status = CellStatus.GREY;
                    targetCell.status = CellStatus.COMP_CHECKER;
                    if (checkQeen(targetCell)) {
                        targetCell.status = CellStatus.BLACK_QUEEN;
                    }
                    fCell.status = CellStatus.GREY;
                    cBoard.userCheckers--;
                    retCell[0] = victimCell;
                    retCell[1] = targetCell;
                    return retCell;
                }
            }
            // if while did not breake we take opposite element by X
            victimCell = getCellByCoordinates(clickedXrev, clickedYrev);
            if (victimCell.status == CellStatus.USER_CHECKER || victimCell.status == CellStatus.WHITE_QUEEN) {
                targetCell = getCellByCoordinates(victimCell.cX + cBoard.cellSize * randomSignX * (-1), victimCell.cY - cBoard.cellSize);
                if (targetCell.status == CellStatus.GREY) {
                    victimCell.status = CellStatus.GREY;
                    targetCell.status = CellStatus.COMP_CHECKER;
                    if (checkQeen(targetCell)) {
                        targetCell.status = CellStatus.BLACK_QUEEN;
                    }
                    fCell.status = CellStatus.GREY;
                    cBoard.userCheckers--;
                    retCell[0] = victimCell;
                    retCell[1] = targetCell;
                    return retCell;
                }
            }
        }
        if (fCell.status == CellStatus.BLACK_QUEEN) {
            Cell tmpCell;
            int count = 1;
            while (((tmpCell = getCellByCoordinates(fCell.cX - (cBoard.cellSize * count), fCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.USER_CHECKER || tmpCell.status == CellStatus.WHITE_QUEEN) {
                count++;
                victimCell = tmpCell;
                tmpCell = (getCellByCoordinates(fCell.cX - (cBoard.cellSize * count), fCell.cY - (cBoard.cellSize * count)));
                while (((tmpCell = getCellByCoordinates(fCell.cX - (cBoard.cellSize * count), fCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                    targetCell = tmpCell;
                    if (targetCell.equals(tmpCell)) {
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.BLACK_QUEEN;
                        fCell.status = CellStatus.GREY;
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
            while (((tmpCell = getCellByCoordinates(fCell.cX + (cBoard.cellSize * count), fCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.USER_CHECKER || tmpCell.status == CellStatus.WHITE_QUEEN) {
                count++;
                victimCell = tmpCell;
                tmpCell = (getCellByCoordinates(fCell.cX + (cBoard.cellSize * count), fCell.cY - (cBoard.cellSize * count)));
                while (((tmpCell = getCellByCoordinates(fCell.cX + (cBoard.cellSize * count), fCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                    targetCell = tmpCell;
                    if (targetCell.equals(tmpCell)) {
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.BLACK_QUEEN;
                        fCell.status = CellStatus.GREY;
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
            while (((tmpCell = getCellByCoordinates(fCell.cX - (cBoard.cellSize * count), fCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.USER_CHECKER || tmpCell.status == CellStatus.WHITE_QUEEN) {
                count++;
                victimCell = tmpCell;
                tmpCell = (getCellByCoordinates(fCell.cX - (cBoard.cellSize * count), fCell.cY + (cBoard.cellSize * count)));
                targetCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(fCell.cX - (cBoard.cellSize * count), fCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                    targetCell = tmpCell;
                    if (targetCell.equals(tmpCell)) {
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.BLACK_QUEEN;
                        fCell.status = CellStatus.GREY;
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
            while (((tmpCell = getCellByCoordinates(fCell.cX + (cBoard.cellSize * count), fCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.USER_CHECKER || tmpCell.status == CellStatus.WHITE_QUEEN) {
                count++;
                victimCell = tmpCell;
                targetCell = (getCellByCoordinates(fCell.cX + (cBoard.cellSize * count), fCell.cY + (cBoard.cellSize * count)));
                while (((tmpCell = getCellByCoordinates(fCell.cX + (cBoard.cellSize * count), fCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.BLACK_QUEEN;
                        fCell.status = CellStatus.GREY;
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

        if (activeCell.status == CellStatus.ACTIVE) {
            /* Verify that second upper left cell from our active checker is free   */
            if (targetCell.equals(getCellByCoordinates(activeCell.cX - 2 * cBoard.cellSize, activeCell.cY - 2 * cBoard.cellSize))) {
                if (targetCell.status == CellStatus.GREY) {
                    /* Verify that first upper left cell from our active checker is enemy (status 4 or 7) */
                    victimCell = getCellByCoordinates(activeCell.cX - cBoard.cellSize, activeCell.cY - cBoard.cellSize);
                    if (victimCell.status == CellStatus.COMP_CHECKER || victimCell.status == CellStatus.BLACK_QUEEN) {
                        activeCell.status = CellStatus.GREY;
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.USER_CHECKER;
                        if (checkQeen(targetCell)) {
                            targetCell.status = CellStatus.WHITE_QUEEN;
                        }
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;

                    }
                }

            }
            if (targetCell.equals(getCellByCoordinates(activeCell.cX + 2 * cBoard.cellSize, activeCell.cY - 2 * cBoard.cellSize))) {
                if (targetCell.status == CellStatus.GREY) {
                    victimCell = getCellByCoordinates(activeCell.cX + cBoard.cellSize, activeCell.cY - cBoard.cellSize);
                    if (victimCell.status == CellStatus.COMP_CHECKER || victimCell.status == CellStatus.BLACK_QUEEN) {
                        activeCell.status = CellStatus.GREY;
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.USER_CHECKER;
                        if (checkQeen(targetCell)) {
                            targetCell.status = CellStatus.WHITE_QUEEN;
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
            if (targetCell.equals(getCellByCoordinates(activeCell.cX - 2 * cBoard.cellSize, activeCell.cY + 2 * cBoard.cellSize))) {
                if (targetCell.status == CellStatus.GREY) {
                    victimCell = getCellByCoordinates(activeCell.cX - cBoard.cellSize, activeCell.cY + cBoard.cellSize);
                    if (victimCell.status == CellStatus.COMP_CHECKER || victimCell.status == CellStatus.BLACK_QUEEN) {
                        activeCell.status = CellStatus.GREY;
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.USER_CHECKER;
                        if (checkQeen(targetCell)) {
                            targetCell.status = CellStatus.WHITE_QUEEN;
                        }
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                }

                /* right bottom cell */            }
            if (targetCell.equals(getCellByCoordinates(activeCell.cX + 2 * cBoard.cellSize, activeCell.cY + 2 * cBoard.cellSize))) {                
                if (targetCell.status == CellStatus.GREY) {
                    victimCell = getCellByCoordinates(activeCell.cX + cBoard.cellSize, activeCell.cY + cBoard.cellSize);
                    if (victimCell.status == CellStatus.COMP_CHECKER || victimCell.status == CellStatus.BLACK_QUEEN) {
                        activeCell.status = CellStatus.GREY;
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.USER_CHECKER;
                        if (checkQeen(targetCell)) {
                            targetCell.status = CellStatus.WHITE_QUEEN;
                        }
                        cBoard.compCheckers--;
                        retCell[0] = victimCell;
                        retCell[1] = targetCell;
                        return retCell;
                    }
                }
            }
        }
        if (activeCell.status == CellStatus.ACTIVE_QUEEN) {
            Cell tmpCell;
            /** Check top left diagonal from selected cell. When cells with status "2" ended, we check next cell - if it is enemy(status "4 (7)" ) 
             * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
             * 
             * top left diagonal 
             */            
            int count = 1;
            while (((tmpCell = getCellByCoordinates(activeCell.cX - (cBoard.cellSize * count), activeCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.COMP_CHECKER || tmpCell.status == CellStatus.BLACK_QUEEN) {
                count++;
                victimCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(activeCell.cX - (cBoard.cellSize * count), activeCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.WHITE_QUEEN;
                        activeCell.status = CellStatus.GREY;
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
            while (((tmpCell = getCellByCoordinates(activeCell.cX + (cBoard.cellSize * count), activeCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.COMP_CHECKER || tmpCell.status == CellStatus.BLACK_QUEEN) {
                count++;
                victimCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(activeCell.cX + (cBoard.cellSize * count), activeCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.WHITE_QUEEN;
                        activeCell.status = CellStatus.GREY;
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
            while (((tmpCell = getCellByCoordinates(activeCell.cX - (cBoard.cellSize * count), activeCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.COMP_CHECKER || tmpCell.status == CellStatus.BLACK_QUEEN) {
                count++;
                victimCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(activeCell.cX - (cBoard.cellSize * count), activeCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.WHITE_QUEEN;
                        activeCell.status = CellStatus.GREY;
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
            while (((tmpCell = getCellByCoordinates(activeCell.cX + (cBoard.cellSize * count), activeCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                count++;
            }
            if (tmpCell.status == CellStatus.COMP_CHECKER || tmpCell.status == CellStatus.BLACK_QUEEN) {
                count++;
                victimCell = tmpCell;
                while (((tmpCell = getCellByCoordinates(activeCell.cX + (cBoard.cellSize * count), activeCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                    if (targetCell.equals(tmpCell)) {
                        victimCell.status = CellStatus.GREY;
                        targetCell.status = CellStatus.WHITE_QUEEN;
                        activeCell.status = CellStatus.GREY;
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
        if (mCell.status == CellStatus.COMP_CHECKER) {
            int randomSignX = getRandomSign();
            int clickedXrand = mCell.cX + cBoard.cellSize * randomSignX;
            int clickedY = mCell.cY + cBoard.cellSize;
            int clickedXrev = mCell.cX - cBoard.cellSize * randomSignX;
            Cell targetCell;

            targetCell = getCellByCoordinates(clickedXrand, clickedY);
            if (targetCell.status == CellStatus.GREY) {
                targetCell.status = CellStatus.COMP_CHECKER;
                if (checkQeen(targetCell)) {
                    targetCell.status = CellStatus.BLACK_QUEEN;
                }
                mCell.status = CellStatus.GREY;
                return targetCell;
            }
            targetCell = getCellByCoordinates(clickedXrev, clickedY);
            if (targetCell.status == CellStatus.GREY) {
                targetCell.status = CellStatus.COMP_CHECKER;
                if (checkQeen(targetCell)) {
                    targetCell.status = CellStatus.BLACK_QUEEN;
                }
                mCell.status = CellStatus.GREY;
                return targetCell;
            }
        }
        if (mCell.status == CellStatus.BLACK_QUEEN) {
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
            while (((tmpCell = getCellByCoordinates(mCell.cX - (cBoard.cellSize * count), mCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                queenPossibleStep[count - 1] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].status != CellStatus.NONE) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.status = CellStatus.BLACK_QUEEN;
                mCell.status = CellStatus.GREY;
                return targetCell;
            }
            /** Check top right diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(mCell.cX + (cBoard.cellSize * count), mCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                queenPossibleStep[count - 1] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].status != CellStatus.NONE) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.status = CellStatus.BLACK_QUEEN;
                mCell.status = CellStatus.GREY;
                return targetCell;
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(mCell.cX - (cBoard.cellSize * count), mCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                queenPossibleStep[count - 1] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].status != CellStatus.NONE) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.status = CellStatus.BLACK_QUEEN;
                mCell.status = CellStatus.GREY;
                return targetCell;
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while (((tmpCell = getCellByCoordinates(mCell.cX + (cBoard.cellSize * count), mCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY)) {
                queenPossibleStep[count - 1] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].status != CellStatus.NONE) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.status = CellStatus.BLACK_QUEEN;
                mCell.status = CellStatus.GREY;
                return targetCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    /** User move */
    private Cell move(Cell activeCell, Cell targetCell) {
        if (activeCell.status == CellStatus.ACTIVE) {
            /** Target cell is top upper left cell from our active cell, check it, and it is free - paint by own checker
                top left
             */            
            if (targetCell.equals(getCellByCoordinates(activeCell.cX - cBoard.cellSize, activeCell.cY - cBoard.cellSize))) {                
                if (targetCell.status == CellStatus.GREY) {
                    targetCell.status = CellStatus.USER_CHECKER;
                    if (checkQeen(targetCell)) {
                        targetCell.status = CellStatus.WHITE_QUEEN;
                    }
                    activeCell.status = CellStatus.GREY;
                    return targetCell;
                }
            }
            if (targetCell.equals(getCellByCoordinates(activeCell.cX + cBoard.cellSize, activeCell.cY - cBoard.cellSize))) {
                /**  top right */
                if (targetCell.status == CellStatus.GREY) {
                    targetCell.status = CellStatus.USER_CHECKER;
                    if (checkQeen(targetCell)) {
                        targetCell.status = CellStatus.WHITE_QUEEN;
                    }
                    activeCell.status = CellStatus.GREY;
                    return targetCell;
                }
            }
        }
        if (activeCell.status == CellStatus.ACTIVE_QUEEN) {
            Cell tmpCell;
            /** Check top left diagonal from selected checker */
            int count = 1;
            while ((tmpCell = getCellByCoordinates(activeCell.cX - (cBoard.cellSize * count), activeCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY) {
                if (tmpCell.equals(targetCell)) {
                    activeCell.status = CellStatus.GREY;
                    tmpCell.status = CellStatus.WHITE_QUEEN;
                    return tmpCell;
                }
                count++;
            }
            /** Check top right diagonal from selected checker */
            count = 1;
            while ((tmpCell = getCellByCoordinates(activeCell.cX + (cBoard.cellSize * count), activeCell.cY - (cBoard.cellSize * count))).status == CellStatus.GREY) {
                if (tmpCell.equals(targetCell)) {
                    activeCell.status = CellStatus.GREY;
                    tmpCell.status = CellStatus.WHITE_QUEEN;
                    return tmpCell;
                }
                count++;
            }
            /** Check bottom left diagonal from selected checker */
            count = 1;
            while ((tmpCell = getCellByCoordinates(activeCell.cX - (cBoard.cellSize * count), activeCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY) {
                if (tmpCell.equals(targetCell)) {
                    activeCell.status = CellStatus.GREY;
                    tmpCell.status = CellStatus.WHITE_QUEEN;
                    return tmpCell;
                }
                count++;
            }
            /** Check bottom right diagonal from selected checker */
            count = 1;
            while ((tmpCell = getCellByCoordinates(activeCell.cX + (cBoard.cellSize * count), activeCell.cY + (cBoard.cellSize * count))).status == CellStatus.GREY) {
                if (tmpCell.equals(targetCell)) {
                    activeCell.status = CellStatus.GREY;
                    tmpCell.status = CellStatus.WHITE_QUEEN;
                    return tmpCell;
                }
                count++;
            }
        }
        return new Cell(CellStatus.NONE);
    }
    
    /** Search cell in array of cells. If cell with such coordinates exists, return it. If not - create new cell and set it's status like "0" */
    Cell getCellByCoordinates(int clickedX, int clickedY) {
        Cell tmpCell;
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            tmpCell = cBoard.cells[cCounter];
            if ((clickedX >= (tmpCell.cX))
                    & (clickedX < (tmpCell.cX + cBoard.cellSize))
                    & (clickedY >= (tmpCell.cY))
                    & (clickedY < (tmpCell.cY + cBoard.cellSize))) {
                return tmpCell;
            }
        }
        return new Cell(CellStatus.NONE);
    }

    private boolean checkQeen(Cell cell) {
        String userIndexQ[] = {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};
        String compIndexQ[] = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"};
        if (cell.status == CellStatus.USER_CHECKER) {
            for (int i = 0; i < userIndexQ.length; i++) {
                if (userIndexQ[i].equals(cell.index)) {
                    return true;
                }
            }
        }
        if (cell.status == CellStatus.COMP_CHECKER) {
            for (int i = 0; i < userIndexQ.length; i++) {
                if (compIndexQ[i].equals(cell.index)) {
                    return true;
                }
            }
        }
        return false;
    }

    Cell getActiveCell() {
        Cell cell;
        for (int cCounter = 0; cCounter < cBoard.cellNum; cCounter++) {
            cell = cBoard.cells[cCounter];
            if (cell.status == CellStatus.ACTIVE || cell.status == CellStatus.ACTIVE_QUEEN) {
                return cell;
            }
        }
        return new Cell(CellStatus.NONE);
    }
    
    void shuffleCells() {
        List<Cell> list = Arrays.asList(cBoard.cells);
        Collections.shuffle(list);
        cBoard.cells = list.toArray(cBoard.cells);
    }

    Logic(ChessBoard cBoard, Menu menu) {
        this.cBoard = cBoard;
        this.menu = menu;
        initTurkishArr();
    }
}
