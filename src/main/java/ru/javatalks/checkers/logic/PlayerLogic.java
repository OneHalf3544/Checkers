package ru.javatalks.checkers.logic;

import ru.javatalks.checkers.model.StepDescription;

/**
 * <p/>
 * <p/>
 * Created: 22.04.12 12:35
 * <p/>
 *
 * @author OneHalf
 */
public interface PlayerLogic {

    /**
     * Do a step
     * @return step description
     * @throws CheckerStepException
     */
    public StepDescription doStep() throws CheckerStepException;
}
