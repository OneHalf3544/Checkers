package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.model.Cell;
import ru.javatalks.checkers.model.ChessBoardModel;

/** 
 * This class provides the painter for checkers
 * 
 * @author Kapellan
 */
@Service
class Painter {

    private final Dialog dialog;
    
    @Autowired
    private ChessBoardModel chessBoardModel;

    @Autowired
    Painter(Dialog dialog) {
        this.dialog = dialog;
    }

    public void run() {
        compStep();
        dialog.tArea.append(dialog.stepUserText + "\n");
        dialog.tArea.setCaretPosition(dialog.tArea.getDocument().getLength());
    }

    void userStep(Cell activeCell, Cell targetCell) {

        /*
         * There is a fighter checker,
         * but we selected other checker, which is not fighter
         */
        if (getUserFighter().getStatus() != CellStatus.NONE && isFighter(activeCell) == false) {
            dialog.resultBuf = dialog.userHasFighterText + getUserFighter().getIndex() + "\n";
            dialog.customResult();
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
                dialog.resultBuf = dialog.userMustFightText + "\n";
                dialog.customResult();
                return;
            }

            addToTurkishArr(victimCell);
            if (isFighter(activeCell)) {
                dialog.act.setActiveCell(activeCell);
                inActionFlag = true;
                userResultCheckersNum += ":" + activeCell.getIndex();
                return;
            } else {
                dialog.act.resetActiveCell();
                inActionFlag = false;
                userResultCheckersNum += ":" + activeCell.getIndex();
                dialog.resultBuf = userResultCheckersNum + "\n";
                dialog.customResult();
                nextStepCompFlag = true;
                return;

            }
        }

        /* If there is no fighter checker we move */
        if (isMover(activeCell)) {
            Cell mCell = move(activeCell, targetCell);
            if (mCell.getStatus() == CellStatus.USER_CHECKER || mCell.getStatus() == CellStatus.WHITE_QUEEN) {
                dialog.resultBuf = activeCell.getIndex() + ":" + mCell.getIndex() + "\n";
                nextStepCompFlag = true;
            } else {
                dialog.resultBuf = dialog.wrongNextCellText + "\n";
            }
            dialog.customResult();
            dialog.tArea.setCaretPosition(dialog.tArea.getDocument().getLength());
            return;
        }
    } // End of userStep


    void compStep() {
        dialog.resultBuf = dialog.stepCompText + "\n";
        activeCell = getCompFighter();
        if (activeCell.getStatus() != CellStatus.NONE) {
            do {
                dialog.resultBuf += activeCell.getIndex();
                if (isFighter(activeCell)) {
                    dialog.resultBuf += ":";
                }
                actCells = fight(activeCell);
                victimCell = actCells[0];
                activeCell = actCells[1];
                if(isFighter(activeCell)) {
                    addToTurkishArr(victimCell);
                }
            } while (activeCell.getStatus() != CellStatus.NONE);
            dialog.resultBuf += "\n";
            resetTurkishArr();
            dialog.customResult();
            dialog.tArea.setCaretPosition(dialog.tArea.getDocument().getLength());
            nextStepCompFlag = false;
            return;
        }
        activeCell = getCompStepper();
        if (activeCell.getStatus() != CellStatus.NONE) {
            dialog.resultBuf += activeCell.getIndex();
            activeCell = move(activeCell);
            dialog.resultBuf += ":" + activeCell.getIndex();
            dialog.resultBuf += "\n";
            dialog.customResult();
            dialog.tArea.setCaretPosition(dialog.tArea.getDocument().getLength());
            return;
        }
    }
}
