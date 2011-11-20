package ru.javatalks.checkers.model;

import ru.javatalks.checkers.gui.CheckerStepException;

public interface Logic {

    /**
     * Make a step (move or fight) with checker on "from" cell by moving this to "to" cell
     * @param from
     * @param to
     */
    public StepDescription doStep(Cell from, Cell to) throws CheckerStepException;

    /**
     * Do a step by computer opponent.
     * @return Description of made step
     */
    public StepDescription doCompStep();

    /**
     * @param fighterCell
     * @param targetCell
     * @return victims of fight
     */
    public StepDescription fight(Cell fighterCell, Cell targetCell);

    /**
     * Player move
     * @param activeCell
     * @param targetCell
     * @return
     */
    public StepDescription move(Cell activeCell, Cell targetCell);
}
