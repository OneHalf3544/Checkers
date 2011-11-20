package ru.javatalks.checkers.model;

import java.awt.*;

import static com.google.common.base.Preconditions.checkArgument;

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

    public Point getNewCoordinates(Point currentPosition, int distant) {
        return new Point(currentPosition.x + distant * dx, currentPosition.y + distant * dy);
    }

    public static StepDirection getByDxDy(int dx, int dy) {
        checkArgument(Math.abs(dx) == Math.abs(dy), "don't' has this direction");

        if (dy > 0) {
            return dx < 0 ? UP_LEFT : UP_RIGHT;
        }
        else {
            return dx < 0 ? DOWN_LEFT : DOWN_RIGHT;
        }
    }
}
