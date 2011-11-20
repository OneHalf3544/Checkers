package ru.javatalks.checkers.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.gui.CheckerStepException;
import ru.javatalks.checkers.gui.CheckerStepException.Cause;

import java.util.List;
import java.util.Random;

/** 
 * This class provides game logic of checkers - move and fight
 * 
 * @author Kapellan
 */
@Service
public class LogicImpl implements Logic {

    private final ChessBoardModel chessBoardModel;
    private final StepChecker checker;
    private final StepRandomizer randomizer;

    private final Random random = new Random();

    @Autowired
    LogicImpl(ChessBoardModel chessBoardModel, StepChecker checker, StepRandomizer randomizer) {
        this.chessBoardModel = chessBoardModel;
        this.checker = checker;
        this.randomizer = randomizer;
    }
    
    @Override
    public StepDescription doStep(Cell from, Cell to) throws CheckerStepException {
        assert !from.isEmpty();
        assert to.isEmpty();

        Player player = from.getChecker().getOwner();

        if (!checker.getFighters(player).isEmpty()) {
            if (checker.canFight(from, to)) {
                return fight(from, to);
            }
            throw new CheckerStepException(player, Cause.MUST_FIGHT);
        }

        /* If there is no fighter checker we move */
        if (checker.canMove(from, to)) {
            return move(from, to);
        }
        throw new CheckerStepException(player, Cause.WRONG_STEP);
    }

    @Override
    public StepDescription doCompStep() {
        List<Cell> fighters = checker.getFighters(Player.OPPONENT);
        if (!fighters.isEmpty()) {
            return randomizer.randomFight(fighters.get(random.nextInt(fighters.size())));
        }

        List<Cell> compStepper = checker.getSteppers(Player.OPPONENT);
        if (!compStepper.isEmpty()) {
            return randomizer.randomMove(compStepper.get(random.nextInt(compStepper.size())));
        }

        throw new IllegalStateException("opponent don't has possible step");
    }

    /**
     * @param activeCell fighter cell
     * @param targetCell
     * @return victims of fight
     */
    @Override
    public StepDescription fight(Cell activeCell, Cell targetCell) {
        assert targetCell.isEmpty();

        return chessBoardModel.fight(activeCell, targetCell);
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
}
