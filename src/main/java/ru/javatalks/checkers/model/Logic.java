package ru.javatalks.checkers.model;

import java.util.List;

public interface Logic {

    /**
     * Make a step (move or fight) with checker on "from" cell by moving this to "to" cell
     * @param from
     * @param to
     */
    public StepDescription doStep(Cell from, Cell to);

    /**
     * Do a step by computer opponent.
     * @return Description of made step
     */
    public StepDescription doCompStep();

    /**
     * Check, that checker can fight
     * @param cell
     * @return true, if a checker on the specified cell can fight
     */
    public boolean canFight(Cell cell);

    public boolean canMove(Cell cell);

    public boolean canFight(Cell cell, Cell targetCell);

    public boolean canMove(Cell cell, Cell targetCell);

    /**
     * find comp fighting checker
     * @param player
     * @return
     */
    public List<Cell> getFighters(Player player);

    public List<Cell> getSteppers(Player player);

    /**
     * Search a victim for specified opponent fighter and do a step.
     * @param fighterCell cell with checker, which will move and fight
     * @return values of victim and target cell
     */
    public StepDescription fightByOpponent(Cell fighterCell);

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
