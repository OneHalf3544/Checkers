package ru.javatalks.checkers.logic;

import com.google.common.collect.Lists;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.model.Cell;
import ru.javatalks.checkers.model.Player;
import ru.javatalks.checkers.model.StepDirection;

import java.util.List;

/**
 * Date: 20.11.11
 * Time: 9:05
 *
 * @author OneHalf
 */
@Service
public class StepValidator {

    private final ChessBoardModel chessBoardModel;

    private final List<StepDirection> directionsForUser
            = Lists.newArrayList(StepDirection.UP_LEFT, StepDirection.UP_RIGHT);

    private final List<StepDirection> directionsForOpponent
            = Lists.newArrayList(StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT);

    private final List<StepDirection> allDirections
            = Lists.newArrayList(StepDirection.values());


    @Autowired
    StepValidator(ChessBoardModel chessBoardModel) {
        this.chessBoardModel = chessBoardModel;
    }

    /**
     * Check, that checker can fight
     * @param cell
     * @return true, if the specified cell can fight
     */
    public boolean canFight(Cell cell) {
        if (cell == null || cell.isEmpty()) {
            return false;
        }
        return cell.hasSimpleChecker() ? isSimpleCheckerFighter(cell) : isQueenFighter(cell);
    }

    private boolean isQueenFighter(Cell fighterCell) {
        assert fighterCell.hasQueen();

        for (StepDirection direction : StepDirection.values()) {
            // Search a checker of other player
            if (isQueenFighter(fighterCell, direction)) {
                return true;
            }
        }
        return false;
    }

    private boolean isQueenFighter(Cell fighterCell, StepDirection direction) {
        Cell victimCell = searchNextNonEmpty(fighterCell, direction);

        if (victimCell == null) {
            return false;
        }

        // if the next cell behind opponent checker is free - queen can fight
        if (victimCell.getChecker().getOwner() != fighterCell.getChecker().getOwner()) {
            Cell targetCell = getRelativeCell(victimCell, direction);
            if (targetCell != null && targetCell.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean canQueenFight(Cell fighterCell, Cell targetCell) {
        if (targetCell == null || !targetCell.isEmpty()) {
            return false;
        }

        StepDirection direction = chessBoardModel.getDirection(fighterCell, targetCell);

        Cell victimCell = searchNextNonEmpty(fighterCell, direction);
        if (victimCell == null || victimCell.getChecker().getOwner() == fighterCell.getChecker().getOwner()) {
            return false;
        }

        int distToVictim = distanceBetweenCells(fighterCell, victimCell);
        int distToTarget = distanceBetweenCells(fighterCell, targetCell);

        if (distToTarget <= distToVictim) {
            return false;
        }

        Cell nextNonEmptyCell = searchNextNonEmpty(victimCell, direction);
        if (nextNonEmptyCell != null) {
            int maxDist = distanceBetweenCells(fighterCell, nextNonEmptyCell);
            if (distToVictim >= maxDist) {
                return false;
            }
        }

        return true;
    }

    private int distanceBetweenCells(Cell fighterCell, Cell victimCell) {
        return Math.abs(fighterCell.getX() - victimCell.getX());
    }

    private boolean isSimpleCheckerFighter(Cell cell) {
        for (StepDirection direction : StepDirection.values()) {
            Cell targetCell = chessBoardModel.getRelativeCell(cell, direction, 2);
            if (canSimpleCheckerFight(cell, targetCell)) {
                return true;
            }
        }
        return false;
    }

    private boolean canSimpleCheckerFight(Cell fighter, Cell targetCell) {
        if (targetCell == null || !targetCell.isEmpty()) {
            return false;
        }

        StepDirection direction = chessBoardModel.getDirection(fighter, targetCell);
        Cell victimCell = getRelativeCell(fighter, direction);

        if (victimCell == null || victimCell.isEmpty() || victimCell.hasCheckerOf(fighter.getChecker().getOwner())) {
            return false;
        }

        return targetCell == getRelativeCell(victimCell, direction);
    }

    public List<Cell> getFighters(Player player) {
        List<Cell> result = Lists.newArrayList();

        for (Cell cell : chessBoardModel) {
            if (cell.hasCheckerOf(player) && canFight(cell)) {
                result.add(cell);
            }
        }
        return result;
    }

    public List<Cell> getSteppers(Player player) {
        List<Cell> result = Lists.newArrayList();

        for (Cell stepperCell : chessBoardModel) {
            if (stepperCell.hasCheckerOf(player) && canMove(stepperCell)) {
                result.add(stepperCell);
            }
        }
        return result;
    }

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

    public boolean canFight(Cell cell, Cell targetCell) {
        if (cell == null || cell.isEmpty() || targetCell == null) {
            return false;
        }

        return cell.hasSimpleChecker()
                ? canSimpleCheckerFight(cell, targetCell)
                : canQueenFight(cell, targetCell);
    }

    public boolean canMove(Cell activeCell, Cell targetCell) {
        if (targetCell == null || !targetCell.isEmpty()) {
            return false;
        }

        StepDirection direction = chessBoardModel.getDirection(activeCell, targetCell);

        if (activeCell.hasSimpleChecker()) {
            return targetCell == getRelativeCell(activeCell, direction)
                    && getPossibleDirectionFor(activeCell.getChecker().getOwner()).contains(direction);
        }

        // can queen move
        Cell nextNonEmpty = searchNextNonEmpty(activeCell, direction);
        if (nextNonEmpty == null) {
            return true;
        }
        // if cells between active and target cell doesn't has any checkers
        int maxDx = Math.abs(nextNonEmpty.getX() - activeCell.getX());
        int dx = Math.abs(targetCell.getX() - activeCell.getX());
        return maxDx >= dx;
    }

    public List<StepDirection> getPossibleDirectionFor(@NotNull Player player) {
        return player == Player.USER ? directionsForUser : directionsForOpponent;
    }

    public Cell searchNextNonEmpty(Cell activeCell, StepDirection direction) {
        return chessBoardModel.searchNextNonEmpty(activeCell, direction);
    }

    public Cell getRelativeCell(Cell cell, StepDirection direction) {
        return chessBoardModel.getRelativeCell(cell, direction);
    }
}
