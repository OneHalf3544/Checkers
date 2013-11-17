package ru.javatalks.checkers.gui;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.logic.CheckerStepException;
import ru.javatalks.checkers.logic.ChessBoardModel;
import ru.javatalks.checkers.logic.PlayerLogic;
import ru.javatalks.checkers.logic.StepValidator;
import ru.javatalks.checkers.model.Cell;
import ru.javatalks.checkers.model.Player;
import ru.javatalks.checkers.model.StepDescription;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * <p/>
 * <p/>
 * Created: 22.04.12 12:36
 * <p/>
 *
 * @author OneHalf
 */
@Service
public class UserLogic implements PlayerLogic {

    private static final Logger log = Logger.getLogger(UserLogic.class);

    private final Player player = Player.USER;

    private final ChessBoardModel chessBoardModel;

    private final StepValidator validator;

    private final BlockingQueue<Cell> clickedCells;

    private volatile boolean active = false;

    @Autowired
    UserLogic(ChessBoardModel chessBoardModel, StepValidator validator) {
        this.chessBoardModel = chessBoardModel;
        this.validator = validator;
        this.clickedCells = new LinkedBlockingQueue<Cell>();
    }

    @Override
    public StepDescription doStep() throws CheckerStepException {
        log.debug("wait a step by user");
        active = true;
        try {
            Cell current = clickedCells.take();
            Cell next = clickedCells.take();
            StepDescription step = doStep(current, next);

            while (validator.canMove(next)) {
                step = step.addMove(clickedCells.take(), null);
            }
            active = false;
            return step;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CheckerStepException(Player.USER, CheckerStepException.Cause.GAME_HAS_BEEN_INTERRUPTED);
        }
    }

    public void addClickedCell(Cell cell) {
        clickedCells.add(cell);
    }

    StepDescription doStep(Cell from, Cell to) throws CheckerStepException {
        assert !from.isEmpty();
        assert to.isEmpty();

        checkArgument(player == from.getChecker().getOwner());
        if (!validator.getFighters(player).isEmpty()) {
            if (validator.canFight(from, to)) {
                return chessBoardModel.fight(from, to);
            }
            throw new CheckerStepException(player, CheckerStepException.Cause.MUST_FIGHT);
        }

        // If there is no fighter checker we move
        if (validator.canMove(from, to)) {
            return chessBoardModel.move(from, to);
        }
        throw new CheckerStepException(player, CheckerStepException.Cause.WRONG_STEP);
    }

    @Override
    public String toString() {
        return "User";
    }

    public boolean isActive() {
        return active;
    }

}
