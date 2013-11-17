package ru.javatalks.checkers.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

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

    public StepDescription addMove(StepDescription nextStep) {
        checkArgument(nextStep.isFight());
        checkArgument(Objects.equals(to.get(to.size() - 1), nextStep.from));

        List<Cell> newTo = new ArrayList<Cell>(to);
        newTo.addAll(nextStep.to);
        List<Cell> newVictims = new ArrayList<Cell>(victims);
        newVictims.addAll(nextStep.victims);
        return new StepDescription(checker, from, newTo, newVictims);
    }

    public boolean isFight() {
        return victims.get(0) != null;
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

            result.append(to.get(i).getIndex());
        }
        return result.toString();
    }
}
