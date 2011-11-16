package ru.javatalks.checkers.model;

/**
 * Date: 16.11.11
 * Time: 21:11
 *
 * @author OneHalf
 */
public class StepDescription {

    private final Cell form;
    private final Cell to;
    private final Cell victim;

    public StepDescription(Cell form, Cell to, Cell victim) {
        this.form = form;
        this.to = to;
        this.victim = victim;
    }

    public StepDescription(Cell from, Cell to) {
        this.form = from;
        this.to = to;
        this.victim = null;
    }

    public Cell getForm() {
        return form;
    }

    public Cell getTo() {
        return to;
    }

    public Cell getVictim() {
        return victim;
    }
}
