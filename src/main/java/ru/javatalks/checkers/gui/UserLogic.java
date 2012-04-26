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

    private CheckBoardMouseListener checkBoardListener;

    private final Object lockObject = new Object();

    private StepDescription lastStep;

    @Autowired
    UserLogic(ChessBoardModel chessBoardModel, StepValidator validator, CheckBoardMouseListener checkBoardListener) {
        this.chessBoardModel = chessBoardModel;
        this.validator = validator;
        this.checkBoardListener = checkBoardListener;
    }

    @Override
    public StepDescription doStep() throws CheckerStepException {
        log.info("wait a step by user");
        try {
            synchronized (lockObject) {
                checkBoardListener.setActiveState();
                lockObject.wait();
                return lastStep;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    StepDescription doStep(Cell from, Cell to) throws CheckerStepException {
        assert !from.isEmpty();
        assert to.isEmpty();

        checkArgument(player == from.getChecker().getOwner());
        synchronized (lockObject) {

            if (!validator.getFighters(player).isEmpty()) {
                if (validator.canFight(from, to)) {
                    lastStep = chessBoardModel.fight(from, to);
                    lockObject.notifyAll();
                    return lastStep;
                }
                throw new CheckerStepException(player, CheckerStepException.Cause.MUST_FIGHT);
            }

            // If there is no fighter checker we move
            if (validator.canMove(from, to)) {
                lastStep = chessBoardModel.move(from, to);
                lockObject.notifyAll();
                return lastStep;
            }
            throw new CheckerStepException(player, CheckerStepException.Cause.WRONG_STEP);
        }
    }

    @Override
    public String toString() {
        return "User";
    }
}
