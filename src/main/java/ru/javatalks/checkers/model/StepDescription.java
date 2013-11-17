package ru.javatalks.checkers.model;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
    private final List<Cell> victims;
    private final Checker checker;

    StepDescription(Checker checker, Cell form, List<Cell> to, List<Cell> victim) {
        assert to.size() == victim.size();

        this.checker = checker;
        this.from = form;
        this.to = to;
        this.victims = victim;
    }

    public StepDescription(Checker checker, Cell from, Cell to) {
        this.checker = checker;
        this.from = from;
        this.to = Collections.singletonList(to);
        this.victims = Collections.singletonList(null);
    }

    public StepDescription addMove(Cell next, @Nullable Cell victim) {
        List<Cell> newTo = new ArrayList<Cell>(to);
        newTo.add(next);
        List<Cell> newVictims = new ArrayList<Cell>(victims);
        newVictims.add(victim);
        return new StepDescription(checker, from, newTo, newVictims);
    }

    public Player getPlayer() {
        return checker.getOwner();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("step ").append(from.getIndex()).append(" : ");

        for(int i = 0; i < to.size(); i++) {
            if (victims.get(i) != null) {
                result.append(victims.get(i).getIndex()).append(" : ");
            }

            result.append(this.to.get(i).getIndex());
        }
        return result.toString();
    }
}
