package ru.javatalks.checkers.model;

/**
 * Date: 16.11.11
 * Time: 21:11
 *
 * @author OneHalf
 */
public class StepDescription {

    private final Cell from;
    private final Cell to;
    private final Cell victim;
    private final Checker checker;

    public StepDescription(Checker checker, Cell form, Cell to, Cell victim) {
        this.checker = checker;
        this.from = form;
        this.to = to;
        this.victim = victim;
    }

    public StepDescription(Checker checker, Cell from, Cell to) {
        this.checker = checker;
        this.from = from;
        this.to = to;
        this.victim = null;
    }

    public Player getPlayer() {
        return checker.getOwner();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("step ").append(from.getIndex()).append(" : ");

        if (victim != null) {
            result.append(victim.getIndex()).append(" : ");
        }

        return result.append(to.getIndex()).toString();
    }
}
