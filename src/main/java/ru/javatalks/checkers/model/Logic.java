package ru.javatalks.checkers.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Random;

/** 
 * This class provides game logic of checkers - move and fight
 * 
 * @author Kapellan
 */
@Service
public class Logic {

    private final ChessBoardModel chessBoardModel;

    private final Random random = new Random();

    @Autowired
    Logic(ChessBoardModel chessBoardModel) {
        this.chessBoardModel = chessBoardModel;
    }
    
    public void userStep(Cell activeCell, Cell targetCell) {
        
        if (!(getUserFighter().isEmpty() || isFighter(activeCell))) {
            throw new IllegalStateException("user has fighter checker and must do step by it");
        }

        /*
         * There is a fighter checker,
         * but we selected other checker, which is not fighter
         */
        if (isFighter(activeCell)) {
            fight(activeCell, targetCell);
            return;
        }

        /* If there is no fighter checker we move */
        if (isMover(activeCell)) {
            move(activeCell, targetCell);
        }
    }
    

    public void compStep() {
        Cell activeCell = getCompFighter();
        
        if (activeCell.isEmpty()) {
            do {
                Cell[] actCells = fight(activeCell);
                activeCell = actCells[1];
            } while (activeCell.isEmpty());

            return;
        }
        
        Cell compStepper = getCompStepper();
        if (compStepper.isEmpty()) {
            move(compStepper);
        }
    }

    /**
     * We check is checker a fighter
     * @param cell
     * @return true, if specified cell can fight
     */
    private boolean isFighter(Cell cell) {
        if (cell.isEmpty()) {
            return false;
        }

        if (cell.hasSimpleChecker()) {
            return isSimpleCheckerFighter(cell);
        }

        if (cell.hasQueen()) {
            return isQueenFighter(cell);
        }
        return false;
    }

    private boolean isQueenFighter(Cell cell) {
        assert cell.hasQueen();

        for (StepDirection direction : StepDirection.values()) {

            Cell tmpCell;
            do {
                tmpCell = chessBoardModel.getRelativeCell(cell, direction);
            } while (cell.isEmpty());

            if (tmpCell.getChecker().getOwner() != cell.getChecker().getOwner()) {
                Cell nextCell = chessBoardModel.getRelativeCell(tmpCell, direction);
                if (nextCell.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSimpleCheckerFighter(Cell cell) {
        for (StepDirection direction : StepDirection.values()) {
            Cell relativeCell = chessBoardModel.getRelativeCell(cell, direction);

            if (relativeCell.isEmpty() || relativeCell.getChecker() == cell.getChecker()) {
                continue;
            }

            if (chessBoardModel.getRelativeCell(relativeCell, direction).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * find comp fighting checker
     * @return
     */
    Cell getCompFighter() {
        for (Cell fighterCell : chessBoardModel) {
            if (fighterCell.hasOpponentChecker() && isFighter(fighterCell)) {
                return fighterCell;
            }
        }
        return null;
    }

    /**
     * find user fighting checker
     * @return
     */
    Cell getUserFighter() {
        for (Cell fighterCell : chessBoardModel) {
            if (fighterCell.hasUserChecker() && isFighter(fighterCell)) {
                return fighterCell;
            }
        }
        return null;
    }

    Cell getUserStepper() {
        for (Cell mCell : chessBoardModel) {
            if (mCell.hasUserChecker() && isMover(mCell)) {
                return mCell;
            }
        }
        return null;
    }

    private boolean isMover(Cell cell) {
        Cell firstNext;
        /* for black checkers */
        if (cell.hasOpponentChecker() && cell.hasSimpleChecker()) {
            firstNext = chessBoardModel.getRelativeCell(cell, StepDirection.DOWN_LEFT);
            if (firstNext.isEmpty()) {
                return true;
            }

            firstNext = chessBoardModel.getRelativeCell(cell, StepDirection.DOWN_RIGHT);
            if (firstNext.isEmpty()) {
                return true;
            }
        }

        if (cell.hasOpponentChecker() && cell.hasQueen()) {
            for (StepDirection direction : StepDirection.values()) {
                firstNext = chessBoardModel.getRelativeCell(cell, direction);
                if (firstNext.isEmpty()) {
                    return true;
                }
            }
        }
        /* for white (user checkers) */
        if (cell.hasUserChecker() && cell.hasSimpleChecker()) {
            firstNext = chessBoardModel.getRelativeCell(cell, StepDirection.UP_LEFT);
            if (firstNext.isEmpty()) {
                return true;
            }
            firstNext = chessBoardModel.getRelativeCell(cell, StepDirection.UP_RIGHT);
            if (firstNext.isEmpty()) {
                return true;
            }

        }
        if (cell.hasUserChecker() && cell.getChecker().isQueen()) {

            for (StepDirection direction: StepDirection.values()) {
                firstNext = chessBoardModel.getRelativeCell(cell, direction);
                if (firstNext.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    } //end isMover()

    Cell getCompStepper() {
        for (Cell stepperCell : chessBoardModel) {
            if (stepperCell.hasOpponentChecker() && isMover(stepperCell)) {
                return stepperCell;
            }
        }
        return null;
    }

    /**
     * Search a victim for specified opponent fighter and do a step.
     * @param fighterCell cell with checker, which will move and fight
     * @return values of victim and target cell
     */
    private Cell[] fight(Cell fighterCell) {
        assert fighterCell.hasOpponentChecker();

        if (fighterCell.hasSimpleChecker()) {
            for(StepDirection direction : StepDirection.values()) {
                Cell victimCell = chessBoardModel.getCellAt(fighterCell.getPosition().x + getRandomSign(), fighterCell.getPosition().y + 1);
                if (victimCell.hasUserChecker()) {
                    Cell targetCell = chessBoardModel.getRelativeCell(victimCell, direction);

                    if (targetCell.isEmpty()) {
                        targetCell.setChecker(fighterCell.getChecker());
                        victimCell.setChecker(null);
                        fighterCell.setChecker(null);
                        return new Cell[]{victimCell, targetCell};
                    }
                }
            }
        }
        if (fighterCell.hasQueen()) {
            for (StepDirection direction : StepDirection.values()) {

                Cell victimCell;
                int count = 0;
                // search a victim
                do {
                    count++;
                    victimCell = chessBoardModel.getRelativeCell(fighterCell, direction, count);
                } while (victimCell.isEmpty());

                if (victimCell.hasOpponentChecker()) {
                    continue;
                }

                Cell cellAfterVictim = chessBoardModel.getRelativeCell(victimCell, direction);
                if (cellAfterVictim.isEmpty()) {

                    victimCell.setChecker(null);
                    cellAfterVictim.setChecker(fighterCell.getChecker());
                    fighterCell.setChecker(null);

                    return new Cell[]{victimCell, cellAfterVictim};
                }
            }
        }

        throw new IllegalStateException();
    }

    /**
     * @param activeCell
     * @param targetCell
     * @return victims of fight
     */
    private Cell[] fight(Cell activeCell, Cell targetCell) {
        assert targetCell.isEmpty();

        for (StepDirection direction : StepDirection.values()){
            /*
             * Verify that second upper left cell from our active checker is free
             */
            if (targetCell.equals(getRelativeCell(activeCell, direction, 2))) {
                Cell victimCell = getRelativeCell(activeCell, direction);
                if (victimCell.hasOpponentChecker()) {
                    targetCell.setChecker(activeCell.getChecker());
                    activeCell.setChecker(null);
                    victimCell.setChecker(null);

                    return new Cell[] {victimCell, targetCell};
                }
            }
        }
        if (activeCell.hasSimpleChecker()) {
            throw new IllegalStateException("can not find step for simple checker");
        }
        
        if (activeCell.hasQueen()) {
            for (StepDirection direction : StepDirection.values()) {
                Cell victimCell;

                int count = 0;
                do {
                    count++;
                    victimCell = getRelativeCell(activeCell, direction, count);
                } while (victimCell.isEmpty());

                if (victimCell.hasOpponentChecker()) {
                    count++;

                    Cell tmpCell = getRelativeCell(activeCell, direction, count);
                    while (tmpCell.isEmpty()) {
                        if (targetCell.equals(tmpCell)) {
                            targetCell.setChecker(activeCell.getChecker());
                            victimCell.setChecker(null);
                            activeCell.setChecker(null);

                            return new Cell[]{victimCell, targetCell};
                        }
                        count++;
                    }
                }
            }
        }
        throw new IllegalStateException("can not find step");
    }

    /**
     * Computer move
     * @param mCell
     * @return
     */
    private Cell move(Cell mCell) {
        assert mCell.hasOpponentChecker();

        if (mCell.hasSimpleChecker()) {
            int randomSignX = getRandomSign();
            int clickedXrand = mCell.getPosition().x + randomSignX;
            int clickedY = mCell.getPosition().y + 1;
            int clickedXrev = mCell.getPosition().x - randomSignX;

            Cell targetCell = chessBoardModel.getCellAt(clickedXrand, clickedY);
            if (targetCell.isEmpty()) {
                targetCell.setChecker(mCell.getChecker());
                mCell.setChecker(null);
                
                return targetCell;
            }
            Cell targetCell2 = chessBoardModel.getCellAt(clickedXrev, clickedY);
            if (targetCell2.isEmpty()) {
                targetCell2.setChecker(mCell.getChecker());
                mCell.setChecker(null);
                return targetCell2;
            }
        }
        if (mCell.hasQueen()) {
            Cell tmpCell;
            Cell targetCell;
            Cell queenPossibleStep[] = new Cell[8];
            
            /* Queen can take any cell after fight, save possible cells to array */
            queenPossibleStep[0] = null;
            
            /** Check top left diagonal from selected cell. When cells with status "2" ended, we check next cell - if it is enemy(status "3 (6)" ) 
             * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
             * top left
             */
            int count = 0;
            while ((tmpCell = chessBoardModel.getRelativeCell(mCell, StepDirection.UP_LEFT)).isEmpty()) {
                queenPossibleStep[count] = tmpCell;
                count++;
            }
            if (queenPossibleStep[0].isEmpty()) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                targetCell.setChecker(mCell.getChecker());
                mCell.setChecker(null);
                return targetCell;
            }

            for (StepDirection direction : StepDirection.values()) {
                count = 0;
                do {
                    queenPossibleStep[count] = tmpCell;
                    count++;
                    tmpCell = getRelativeCell(mCell, direction, count);
                } while (tmpCell.isEmpty());

                if (queenPossibleStep[0].isEmpty()) {
                    targetCell = queenPossibleStep[random.nextInt(count)];
                    targetCell.setChecker(mCell.getChecker());
                    mCell.setChecker(null);
                    return targetCell;
                }
            }
        }
        return null;
    }

    /** Player move */
    private void move(Cell activeCell, Cell targetCell) {
        if (activeCell.hasSimpleChecker()) {
            if (targetCell.equals(chessBoardModel.getRelativeCell(activeCell, StepDirection.UP_LEFT))) {
                if (targetCell.isEmpty()) {
                    targetCell.setChecker(activeCell.getChecker());
                    activeCell.setChecker(null);
                    return;
                }
            }
            if (targetCell.equals(chessBoardModel.getRelativeCell(activeCell, StepDirection.UP_RIGHT))) {
                if (targetCell.isEmpty()) {
                    targetCell.setChecker(activeCell.getChecker());
                    activeCell.setChecker(null);
                    return;
                }
            }
        }
        if (activeCell.hasUserChecker() && activeCell.hasQueen()) {
            for (StepDirection direction : StepDirection.values()) {
                Cell tmpCell = activeCell;
                while ((tmpCell = chessBoardModel.getRelativeCell(tmpCell, direction)).isEmpty()) {
                    if (tmpCell.equals(targetCell)) {
                        activeCell.setChecker(null);
                        tmpCell.setChecker(activeCell.getChecker());
                        return;
                    }
                }
            }
        }
    }
    
    private boolean checkQueen(Cell cell) {
        if (!cell.hasSimpleChecker()) {
            return false;
        }

        if (cell.getChecker().getOwner() == Player.USER && cell.getPosition().x == 8) {
            return true;
        }

        if (cell.getChecker().getOwner() == Player.OPPONENT && cell.getPosition().x == 1) {
            return true;
        }
        return false;
    }

    public int getRandomSign() {
        return random.nextBoolean() ? -1 : 1;
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction) {
        return chessBoardModel.getRelativeCell(cell, direction);
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction, int distant) {
        return chessBoardModel.getRelativeCell(cell, direction, distant);
    }
}
