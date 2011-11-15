package ru.javatalks.checkers.model;

import java.awt.*;

public class Cell {

    private Checker checker;
    private final int x;
    private final int y;

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean hasOpponentChecker() {
        return checker != null && checker.getOwner() == Player.OPPONENT;
    }

    public boolean hasUserChecker() {
        return checker != null && checker.getOwner() == Player.USER;
    }

    public boolean isEmpty() {
        return checker == null;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    public String getIndex() {
        return String.valueOf('a' + x) + y;
    }
}
