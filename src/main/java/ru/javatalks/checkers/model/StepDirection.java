package ru.javatalks.checkers.model;

import java.awt.*;

/**
 * Date: 13.11.11
 * Time: 2:52
 *
 * @author OneHalf
 */
public enum StepDirection {

    UP_LEFT(-1, 1),
    UP_RIGHT(1, 1),
    DOWN_LEFT(-1, -1),
    DOWN_RIGHT(1, -1);

    private final int dx;
    private final int dy;

    private StepDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Point getNewCoordinates(Point currentPosition) {
        return getNewCoordinates(currentPosition, 1);
    }

    public Point getNewCoordinates(Point currentPosition, int distant) {
        return new Point(currentPosition.x + distant * dx, currentPosition.y + distant * dy);
    }
}
