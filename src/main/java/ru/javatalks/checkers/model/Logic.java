package ru.javatalks.checkers.model;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private static final StepDirection[] directionsForUser = {StepDirection.UP_LEFT, StepDirection.UP_RIGHT};
    private static final StepDirection[] directionsForOpponent = {StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT};

    @Autowired
    Logic(ChessBoardModel chessBoardModel) {
        this.chessBoardModel = chessBoardModel;
    }
    
    public void doStep(Cell from, Cell to) {
        assert !from.isEmpty();
        assert to.isEmpty();

        Player player = from.getChecker().getOwner();

        if (!getFighters(player).isEmpty() && !canFight(from)) {
            throw new IllegalStateException("user has fighter checker and must do step by it");
        }

        if (canFight(from)) {
            fight(from, to);
            return;
        }

        /* If there is no fighter checker we move */
        if (canMove(from)) {
            move(from, to);
        }

        throw new IllegalStateException("can't do this step");
    }
    

    public void compStep() {
        List<Cell> fighters = getFighters(Player.OPPONENT);

        if (!fighters.isEmpty()) {
            fight(fighters.get(random.nextInt(fighters.size())));
            return;
        }

        Cell compStepper = getCompStepper();
        if (compStepper.isEmpty()) {
            opponentMove(compStepper);
        }
    }

    /**
     * Check, that checker can fight
     * @param cell
     * @return true, if the specified cell can fight
     */
    private boolean canFight(Cell cell) {
        if (cell.isEmpty()) {
            return false;
        }
        return cell.hasSimpleChecker() ? isSimpleCheckerFighter(cell) : isQueenFighter(cell);
    }

    private boolean isQueenFighter(Cell cell) {
        assert cell.hasQueen();

        for (StepDirection direction : StepDirection.values()) {
            // Search a checker of other player
            Cell tmpCell;
            do {
                tmpCell = chessBoardModel.getRelativeCell(cell, direction);
            } while (cell.isEmpty());

            // if the next cell behind opponent checker is free - queen can fight
            if (tmpCell.getChecker().getOwner() != cell.getChecker().getOwner()) {
                if (getRelativeCell(tmpCell, direction).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSimpleCheckerFighter(Cell cell) {
        for (StepDirection direction : StepDirection.values()) {
            Cell relativeCell = getRelativeCell(cell, direction);
            if (relativeCell == null) {
                continue;
            }

            if (!relativeCell.isEmpty() && relativeCell.getChecker().getOwner() != cell.getChecker().getOwner()) {
                if (chessBoardModel.getRelativeCell(relativeCell, direction).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * find comp fighting checker
     * @return
     */
    private List<Cell> getFighters(Player player) {
        List<Cell> result = Lists.newArrayList();

        for (Cell cell : chessBoardModel) {
            if (!cell.isEmpty() && cell.getChecker().getOwner() == player && canFight(cell)) {
                result.add(cell);
            }
        }
        return result;
    }

    private boolean canMove(Cell cell) {
        assert !cell.isEmpty();

        if (cell.hasQueen()) {
            for (StepDirection direction : StepDirection.values()) {
                if (getRelativeCell(cell, direction).isEmpty()) {
                    return true;
                }
            }
        }

        Player player = cell.getChecker().getOwner();
        StepDirection[] directions = player == Player.OPPONENT ? directionsForOpponent : directionsForUser;

        for (StepDirection direction : directions) {
            Cell firstNext = chessBoardModel.getRelativeCell(cell, direction);
            if (firstNext.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    Cell getCompStepper() {
        for (Cell stepperCell : chessBoardModel) {
            if (stepperCell.hasOpponentChecker() && canMove(stepperCell)) {
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
    private StepDescription fight(Cell fighterCell) {
        assert fighterCell.hasOpponentChecker();

        if (fighterCell.hasSimpleChecker()) {
            for(StepDirection direction : StepDirection.values()) {
                Cell victimCell = chessBoardModel.getCellAt(fighterCell.getPosition().x + getRandomSign(), fighterCell.getPosition().y + 1);
                if (victimCell.hasUserChecker()) {
                    Cell targetCell = chessBoardModel.getRelativeCell(victimCell, direction);

                    if (targetCell.isEmpty()) {
                        return fight(fighterCell, targetCell);
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
                    return chessBoardModel.fight(fighterCell, cellAfterVictim);
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
    private StepDescription fight(Cell activeCell, Cell targetCell) {
        assert targetCell.isEmpty();

        for (StepDirection direction : StepDirection.values()){
            if (targetCell == getRelativeCell(activeCell, direction, 2)
                    && getRelativeCell(activeCell, direction).hasOpponentChecker()) {
                return chessBoardModel.fight(activeCell, direction);
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
                    Cell tmpCell;
                    do {
                        count++;
                        tmpCell = getRelativeCell(activeCell, direction, count);
                    } while (targetCell == tmpCell);

                    return chessBoardModel.fight(activeCell, targetCell);
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
    private StepDescription opponentMove(Cell mCell) {
        assert mCell.hasOpponentChecker();

        if (mCell.hasSimpleChecker()) {
            int rand = random.nextInt(2);
            StepDirection direction = directionsForOpponent[rand];
            StepDirection direction2 = directionsForOpponent[1 - rand];

            Cell targetCell = getRelativeCell(mCell, direction);
            if (targetCell.isEmpty()) {
                return chessBoardModel.move(mCell, targetCell);
            }

            Cell targetCell2 = chessBoardModel.getRelativeCell(mCell, direction2);
            if (targetCell2.isEmpty()) {
                return chessBoardModel.move(mCell, targetCell2);
            }

            throw new IllegalStateException();
        }

        Cell[] queenPossibleStep = new Cell[8];

        /* Queen can take any cell after fight, save possible cells to array */
        queenPossibleStep[0] = null;

        /**
         * Check diagonals from selected cell. When cells with status "2" ended, we check next cell - if it is enemy(status "3 (6)" )
         * we check next cell in diagonal, if it is grey cell(status "2") return true - queen should fight
         * top left
         */
        int count = 0;
        Cell tmpCell;
        while ((tmpCell = chessBoardModel.getRelativeCell(mCell, StepDirection.UP_LEFT)).isEmpty()) {
            queenPossibleStep[count] = tmpCell;
            count++;
        }
        Cell targetCell;
        if (queenPossibleStep[0].isEmpty()) {
            targetCell = queenPossibleStep[random.nextInt(count - 1)];
            return chessBoardModel.move(mCell, targetCell);
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
                return chessBoardModel.move(mCell, targetCell);
            }
        }
        throw new IllegalStateException("can not step by checker on this cell");
    }

    /**
     * Player move
     */
    private StepDescription move(Cell activeCell, Cell targetCell) {
        if (activeCell.hasSimpleChecker()) {
            for (StepDirection direction : directionsForUser) {
                if (targetCell == chessBoardModel.getRelativeCell(activeCell, direction)) {
                    return chessBoardModel.move(activeCell, direction);
                }
            }
            throw new IllegalStateException("wrong step for simple checker");
        }
        
        for (StepDirection direction : StepDirection.values()) {
            Cell tmpCell = activeCell;
            do {
                tmpCell = chessBoardModel.getRelativeCell(tmpCell, direction);
            } while (tmpCell.isEmpty());

            if (tmpCell == targetCell) {
                return chessBoardModel.move(activeCell, targetCell);
            }
        }
        throw new IllegalStateException("wrong step for queen");
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
