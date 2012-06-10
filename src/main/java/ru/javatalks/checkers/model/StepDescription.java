package ru.javatalks.checkers.model;

import java.util.Collections;
import java.util.List;

/**
 * Date: 16.11.11
 * Time: 21:11
 *
 * @author OneHalf
 */
public class StepDescription {

    private final Cell from;
    private final List<Cell> to;
    private final List<Cell> victim;
    private final Checker checker;

    StepDescription(Checker checker, Cell form, List<Cell> to, List<Cell> victim) {
        assert to.size() != victim.size();

        this.checker = checker;
        this.from = form;
        this.to = to;
        this.victim = victim;
    }

    public StepDescription(Checker checker, Cell from, Cell to) {
        this.checker = checker;
        this.from = from;
        this.to = Collections.singletonList(to);
        this.victim = null;
    }

    public Player getPlayer() {
        return checker.getOwner();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("step ").append(from.getIndex()).append(" : ");

        for(int i = 0; i < to.size(); i++) {
            if (victim != null) {
                result.append(victim.get(i).getIndex()).append(" : ");
            }

            result.append(this.to.get(i).getIndex());
        }
        return result.toString();
    }
}
