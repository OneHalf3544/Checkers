package ru.javatalks.checkers.logic;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.model.Cell;
import ru.javatalks.checkers.model.Player;
import ru.javatalks.checkers.model.StepDescription;

import java.util.List;
import java.util.Random;

/**
 * <p/>
 * <p/>
 * Created: 22.04.12 12:36
 * <p/>
 *
 * @author OneHalf
 */
@Service
public class ComputerLogic implements PlayerLogic {

    private static final Logger log = Logger.getLogger(ComputerLogic.class);

    private static final Random random = new Random();

    private final Player player = Player.OPPONENT;
    private final StepRandomizer randomizer;
    private final StepValidator validator;

    @Autowired
    ComputerLogic(StepValidator validator, StepRandomizer randomizer) {
        this.validator = validator;
        this.randomizer = randomizer;
    }

    /**
     * Make a step by computer
     * @return Step description
     */
    public StepDescription doStep() {
        log.info("step by computer");
        List<Cell> fighters = validator.getFighters(player);
        if (!fighters.isEmpty()) {
            return randomizer.randomFight(fighters.get(random.nextInt(fighters.size())));
        }

        List<Cell> compStepper = validator.getSteppers(player);
        if (!compStepper.isEmpty()) {
            return randomizer.randomMove(compStepper.get(random.nextInt(compStepper.size())));
        }

        throw new IllegalStateException("opponent don't has possible step");
    }

    @Override
    public String toString() {
        return "Computer player";
    }
}
