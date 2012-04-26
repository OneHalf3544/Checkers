package ru.javatalks.checkers.logic;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.model.Cell;
import ru.javatalks.checkers.model.StepDescription;
import ru.javatalks.checkers.model.StepDirection;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/** 
 * This class provides game logic of checkers - move and fight
 * 
 * @author Kapellan
 */
@Service
public class StepRandomizer {

    private final ChessBoardModel chessBoardModel;
    private final StepValidator validator;

    private final Random random = new Random();

    private final List<StepDirection> directionsForOpponent
            = Lists.newArrayList(StepDirection.DOWN_LEFT, StepDirection.DOWN_RIGHT);

    private final List<StepDirection> allDirections
            = Lists.newArrayList(StepDirection.values());

    @Autowired
    StepRandomizer(ChessBoardModel chessBoardModel, StepValidator validator) {
        this.chessBoardModel = chessBoardModel;
        this.validator = validator;
    }

    /**
     * Search a victim for specified opponent fighter and do a step.
     * @param fighterCell cell with checker, which will move and fight
     * @return values of victim and target cell
     */
    public StepDescription randomFight(Cell fighterCell) {
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
                        return chessBoardModel.fight(fighterCell, targetCell);
                    }
                }
            }
            throw new IllegalStateException();
        }

        for (StepDirection direction : StepDirection.values()) {
            Cell victimCell = searchNextNonEmpty(fighterCell, direction);
            if (victimCell == null || victimCell.hasOpponentChecker()) {
                continue;
            }

            Cell cellAfterVictim = getRelativeCell(victimCell, direction);
            if (cellAfterVictim != null && cellAfterVictim.isEmpty()) {
                return chessBoardModel.fight(fighterCell, cellAfterVictim);
            }
        }
        throw new IllegalStateException();
    }

    /**
     * Computer move
     * @param movedChecker
     * @return
     */
    public StepDescription randomMove(Cell movedChecker) {
        assert movedChecker.hasOpponentChecker();

        if (movedChecker.hasSimpleChecker()) {
            return randomMoveWithSimpleChecker(movedChecker);
        }

        return randomMoveWithQueen(movedChecker);
    }

    private StepDescription randomMoveWithQueen(Cell mCell) {

        Collections.shuffle(allDirections);
        for (StepDirection direction : allDirections) {
            /* Queen can take any cell after fight, save possible cells to array */
            List<Cell> possibleStep = Lists.newArrayList();

            /**
             * Check diagonals from selected cell. When empty cells ended, we check next cell - if it is enemy
             * we check next cell in diagonal, if it is empty cell return true - queen should fight
             */
            Cell tmpCell = chessBoardModel.getRelativeCell(mCell, direction);
            while (tmpCell != null && tmpCell.isEmpty()) {
                possibleStep.add(tmpCell);
                tmpCell = chessBoardModel.getRelativeCell(tmpCell, direction);
            }

            if (!possibleStep.isEmpty()) {
                Cell targetCell = possibleStep.get(random.nextInt(possibleStep.size()));
                return chessBoardModel.move(mCell, targetCell);
            }
        }
        throw new IllegalStateException("can not step by checker on this cell");
    }

    private StepDescription randomMoveWithSimpleChecker(Cell mCell) {
        Collections.shuffle(directionsForOpponent);

        for (StepDirection direction : directionsForOpponent) {
            Cell targetCell = getRelativeCell(mCell, direction);
            if (validator.canMove(mCell, targetCell)) {
                return chessBoardModel.move(mCell, targetCell);
            }
        }
        throw new IllegalStateException();
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
