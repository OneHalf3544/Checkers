package ru.javatalks.checkers.model;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/** 
 * This class provides game logic of checkers - move and fight
 * 
 * @author Kapellan
 */
@Service
public class LogicImpl implements Logic {

    private final ChessBoardModel chessBoardModel;

    private final Random random = new Random();

    private final List<StepDirection> directionsForUser
            = Lists.newArrayList(StepDirection.UP_LEFT, StepDirection.UP_RIGHT);

    private final List<StepDirection> directionsForOpponent
            = Lists.newArrayList(StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT);

    private final List<StepDirection> allDirections
            = Lists.newArrayList(StepDirection.values());


    @Autowired
    LogicImpl(ChessBoardModel chessBoardModel) {
        this.chessBoardModel = chessBoardModel;
    }
    
    @Override
    public StepDescription doStep(Cell from, Cell to) {
        assert !from.isEmpty();
        assert to.isEmpty();

        Player player = from.getChecker().getOwner();

        if (!getFighters(player).isEmpty()) {
            if (canFight(from, to)) {
                return fight(from, to);
            }
            throw new IllegalStateException("user has fighter checker and must do step by it");
        }

        /* If there is no fighter checker we move */
        if (canMove(from, to)) {
            return move(from, to);
        }
        throw new IllegalStateException("can't do this step");
    }

    @Override
    public StepDescription doCompStep() {
        List<Cell> fighters = getFighters(Player.OPPONENT);
        if (!fighters.isEmpty()) {
            return fightByOpponent(fighters.get(random.nextInt(fighters.size())));
        }

        List<Cell> compStepper = getSteppers(Player.OPPONENT);
        if (!compStepper.isEmpty()) {
            return randomMove(compStepper.get(random.nextInt(compStepper.size())));
        }

        throw new IllegalStateException("opponent don't has possible step");
    }

    /**
     * Check, that checker can fight
     * @param cell
     * @return true, if the specified cell can fight
     */
    @Override
    public boolean canFight(Cell cell) {
        if (cell == null || cell.isEmpty()) {
            return false;
        }
        return cell.hasSimpleChecker() ? isSimpleCheckerFighter(cell) : isQueenFighter(cell);
    }

    private boolean isQueenFighter(Cell cell) {
        assert cell.hasQueen();

        for (StepDirection direction : StepDirection.values()) {
            // Search a checker of other player
            if (isQueenFighter(cell, direction)) {
                return true;
            }
        }
        return false;
    }

    private boolean isQueenFighter(Cell cell, StepDirection direction) {
        Cell tmpCell = searchNextNonEmpty(cell, direction);

        if (tmpCell == null) {
            return false;
        }

        // if the next cell behind opponent checker is free - queen can fight
        if (tmpCell.getChecker().getOwner() != cell.getChecker().getOwner()) {
            Cell targetCell = getRelativeCell(tmpCell, direction);
            if (targetCell != null && targetCell.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isSimpleCheckerFighter(Cell cell) {
        for (StepDirection direction : StepDirection.values()) {
            if (isSimpleCheckerFighter(cell, direction)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSimpleCheckerFighter(Cell cell, StepDirection direction) {
        Cell relativeCell = getRelativeCell(cell, direction);
        if (relativeCell != null && !relativeCell.isEmpty() && !relativeCell.hasCheckerOf(cell.getChecker().getOwner())) {
            Cell targetCell = getRelativeCell(relativeCell, direction);
            if (targetCell != null && targetCell.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Cell> getFighters(Player player) {
        List<Cell> result = Lists.newArrayList();

        for (Cell cell : chessBoardModel) {
            if (cell.hasCheckerOf(player) && canFight(cell)) {
                result.add(cell);
            }
        }
        return result;
    }

    @Override
    public List<Cell> getSteppers(Player player) {
        List<Cell> result = Lists.newArrayList();

        for (Cell stepperCell : chessBoardModel) {
            if (stepperCell.hasCheckerOf(player) && canMove(stepperCell)) {
                result.add(stepperCell);
            }
        }
        return result;
    }

    @Override
    public boolean canMove(Cell cell) {
        assert !cell.isEmpty();

        Player player = cell.getChecker().getOwner();

        List<StepDirection> directions;
        if (cell.hasSimpleChecker()) {
            directions = player == Player.OPPONENT ? directionsForOpponent : directionsForUser;
        } else {
            directions = allDirections;
        }

        for (StepDirection direction : directions) {
            if (canMove(cell, getRelativeCell(cell, direction))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canFight(Cell cell, Cell targetCell) {
        if (cell == null || cell.isEmpty()) {
            return false;
        }
        StepDirection direction = chessBoardModel.getDirection(cell, targetCell);
        return cell.hasSimpleChecker()
                ? isSimpleCheckerFighter(cell, direction)
                : isQueenFighter(cell, direction);
    }

    /**
     * Search a victim for specified opponent fighter and do a step.
     * @param fighterCell cell with checker, which will move and fight
     * @return values of victim and target cell
     */
    @Override
    public StepDescription fightByOpponent(Cell fighterCell) {
        assert fighterCell.hasOpponentChecker();

        if (fighterCell.hasSimpleChecker()) {
            for(StepDirection direction : StepDirection.values()) {
                Cell victimCell = getRelativeCell(fighterCell, direction);
                if (victimCell == null) {
                    continue;
                }
                if (victimCell.hasUserChecker()) {
                    Cell targetCell = getRelativeCell(victimCell, direction);

                    if (targetCell != null && targetCell.isEmpty()) {
                        return fight(fighterCell, targetCell);
                    }
                }
            }
            throw new IllegalStateException();
        }

        for (StepDirection direction : StepDirection.values()) {
            Cell victimCell = searchNextNonEmpty(fighterCell, direction);
            if (victimCell.hasOpponentChecker()) {
                continue;
            }

            Cell cellAfterVictim = getRelativeCell(victimCell, direction);
            if (cellAfterVictim.isEmpty()) {
                return chessBoardModel.fight(fighterCell, cellAfterVictim);
            }
        }
        throw new IllegalStateException();
    }

    /**
     * @param activeCell
     * @param targetCell
     * @return victims of fight
     */
    @Override
    public StepDescription fight(Cell activeCell, Cell targetCell) {
        assert targetCell.isEmpty();

        StepDirection direction = chessBoardModel.getDirection(activeCell, targetCell);

        if (activeCell.hasSimpleChecker()) {
            return fightWithSimpleChecker(activeCell, direction);
        }

        Cell victimCell = searchNextNonEmpty(activeCell, direction);

        int count = 0;
        Cell tmpCell;
        do {
            count++;
            tmpCell = getRelativeCell(victimCell, direction, count);
        } while (targetCell == tmpCell);

        return chessBoardModel.fight(activeCell, targetCell);
    }

    private StepDescription fightWithSimpleChecker(Cell activeCell, StepDirection direction) {
        return chessBoardModel.fight(activeCell, direction);
    }

    /**
     * Computer move
     * @param movedChecker
     * @return
     */
    private StepDescription randomMove(Cell movedChecker) {
        assert movedChecker.hasOpponentChecker();

        if (movedChecker.hasSimpleChecker()) {
            return randomMoveWithSimpleChecker(movedChecker);
        }

        return randomMoveWithQueen(movedChecker);
    }

    private StepDescription randomMoveWithQueen(Cell mCell) {
        Collections.shuffle(allDirections);
        for (StepDirection direction : allDirections) {
            Cell[] queenPossibleStep = new Cell[8];

            /* Queen can take any cell after fight, save possible cells to array */
            queenPossibleStep[0] = null;

            /**
            * Check diagonals from selected cell. When empty cells ended, we check next cell - if it is enemy
            * we check next cell in diagonal, if it is empty cell return true - queen should fight
            */
            int count = 0;
            Cell tmpCell;
            while ((tmpCell = chessBoardModel.getRelativeCell(mCell, direction)).isEmpty()) {
                queenPossibleStep[count] = tmpCell;
                count++;
            }
            Cell targetCell;
            if (queenPossibleStep[0].isEmpty()) {
                targetCell = queenPossibleStep[random.nextInt(count - 1)];
                return chessBoardModel.move(mCell, targetCell);
            }

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

    private StepDescription randomMoveWithSimpleChecker(Cell mCell) {
        Collections.shuffle(directionsForOpponent);

        for (StepDirection direction : directionsForOpponent) {
            Cell targetCell = getRelativeCell(mCell, direction);
            if (canMove(mCell, targetCell)) {
                return chessBoardModel.move(mCell, targetCell);
            }
        }
        throw new IllegalStateException();
    }

    /**
     * Move checker from one cell to other
     * @param activeCell source cell
     * @param targetCell destination cell
     */
    @Override
    public StepDescription move(Cell activeCell, Cell targetCell) {
        return chessBoardModel.move(activeCell, targetCell);
    }

    @Override
    public boolean canMove(Cell activeCell, Cell targetCell) {
        if (targetCell == null || !targetCell.isEmpty()) {
            return false;
        }

        StepDirection direction = chessBoardModel.getDirection(activeCell, targetCell);

        if (activeCell.hasSimpleChecker()) {
            return targetCell == getRelativeCell(activeCell, direction);
        }

        // can queen move
        Cell nextNonEmpty = searchNextNonEmpty(activeCell, direction);
        if (nextNonEmpty == null) {
            return true;
        }
        // if cells between active and target cell doesn't has any checkers
        int maxDx = nextNonEmpty.getX() - activeCell.getX();
        int dx = targetCell.getX() - activeCell.getX();
        return maxDx >= dx;
    }

    public Cell searchNextNonEmpty(Cell activeCell, StepDirection direction) {
        return chessBoardModel.searchNextNonEmpty(activeCell, direction);
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction) {
        return chessBoardModel.getRelativeCell(cell, direction);
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction, int distant) {
        return chessBoardModel.getRelativeCell(cell, direction, distant);
    }
}
