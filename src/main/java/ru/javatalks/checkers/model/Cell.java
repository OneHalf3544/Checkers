package ru.javatalks.checkers.model;

import java.awt.*;

public class Cell {

    private Checker checker;
    private final Point position;

    Cell(int x, int y, Checker checker) {
        this.position = new Point(x, y);
        this.checker = checker;
    }

    public boolean hasOpponentChecker() {
        return checker.getOwner() == Player.OPPONENT;
    }

    public boolean hasUserChecker() {
        return checker.getOwner() == Player.USER;
    }

    public boolean isEmpty() {
        return checker.getOwner() == null;
    }

    public boolean hasQueen() {
        return checker != null && checker.isQueen();
    }

    public boolean hasSimpleChecker() {
        return !(checker == null || checker.isQueen());
    }

    public Checker getChecker() {
        return checker;
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    public Point getPosition() {
        return position;
    }
}
