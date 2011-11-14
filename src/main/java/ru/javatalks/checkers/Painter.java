package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.model.Cell;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** 
 * This class provides the painter for checkers
 * 
 * @author Kapellan
 */
@Service
class Painter implements Runnable {

    private Menu menu;
    private String userResultCheckersNum;

    @Autowired
    Painter(Menu menu) {
        this.menu = menu;
    }

    public void run() {
        compStep();
        menu.tArea.append(menu.stepUserText + "\n");
        menu.tArea.setCaretPosition(menu.tArea.getDocument().getLength());
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
            } else {
                menu.act.resetActiveCell();
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


    void compStep() {
        menu.resultBuf = menu.stepCompText + "\n";
        activeCell = getCompFighter();
        if (activeCell.getStatus() != CellStatus.NONE) {
            do {
                menu.resultBuf += activeCell.getIndex();
                if (isFighter(activeCell)) {
                    menu.resultBuf += ":";
                }
                actCells = fight(activeCell);
                victimCell = actCells[0];
                activeCell = actCells[1];
                if(isFighter(activeCell)) {
                    addToTurkishArr(victimCell);
                }
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
            menu.resultBuf += activeCell.getIndex();
            activeCell = move(activeCell);
            menu.resultBuf += ":" + activeCell.getIndex();
            menu.resultBuf += "\n";
            menu.customResult();
            menu.tArea.setCaretPosition(menu.tArea.getDocument().getLength());
            return;
        }
    }
}
