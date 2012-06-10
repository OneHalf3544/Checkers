package ru.javatalks.checkers.model;

import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Helper class for build description of a step
 * <p/>
 * <p/>
 * Created: 29.04.12 15:23
 * <p/>
 *
 * @author OneHalf
 */
public class StepDescriptionBuilder {

    private boolean turkishStep = false;

    private final Checker checker;
    private final Cell from;
    private final List<Cell> victims = Lists.newArrayList();
    private final List<Cell> to = Lists.newArrayList();

    public StepDescriptionBuilder(Checker checker, Cell from) {
        this.checker = checker;
        this.from = from;
    }

    public StepDescriptionBuilder step(Cell to) {
        checkState(!turkishStep);

        this.to.add(to);
        return this;
    }

    public StepDescriptionBuilder step(Cell victim, Cell to) {
        assert turkishStep || victims.isEmpty();

        turkishStep = true;

        this.victims.add(victim);
        this.to.add(to);

        return this;
    }

    public StepDescription build() {
        if (turkishStep) {
            return new StepDescription(checker, from, to, victims);
        }
        return new StepDescription(checker, from, to.get(0));
    }
}
