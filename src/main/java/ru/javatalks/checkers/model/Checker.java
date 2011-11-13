package ru.javatalks.checkers.model;

/**
 * Date: 13.11.11
 * Time: 12:13
 *
 * @author OneHalf
 */
public class Checker {

    private final User owner;

    private boolean isQueen = false;

    public Checker(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public boolean isQueen() {
        return isQueen;
    }

    public void makeQueen() {
        isQueen = true;
    }
}
