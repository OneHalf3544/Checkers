package ru.javatalks.checkers.gui;

import java.awt.*;

/**
 * Date: 12.11.11
 * Time: 21:26
 *
 * @author OneHalf
 */
public enum CellType {

    WHITE_CELL {
        @Override
        public void paintCell(Graphics2D graphics) {
            drawCell(graphics, Color.WHITE);
        }
    },
    GREY_CELL {
        @Override
        public void paintCell(Graphics2D graphics) {
            drawCell(graphics, Color.GRAY);
        }
    },
    USER_CHECKER {

        @Override
        public void paintCell(Graphics2D graphics) {
            drawSimpleChecker(graphics, Color.WHITE);
        }
    },
    OPPONENT_CHECKER {
        @Override
        public void paintCell(Graphics2D graphics) {
            drawSimpleChecker(graphics, Color.BLACK);
        }
    },
    USER_QUEEN {
        @Override
        public void paintCell(Graphics2D graphics) {
            drawQueen(graphics, Color.LIGHT_GRAY, Color.WHITE);
        }
    },
    OPPONENT_QUEEN {
        @Override
        public void paintCell(Graphics2D graphics) {
            drawQueen(graphics, Color.LIGHT_GRAY, Color.BLACK);
        }
    },
    ACTIVE {
        @Override
        public void paintCell(Graphics2D graphics) {
            drawSimpleChecker(graphics, Color.RED);
        }
    },
    ACTIVE_QUEEN {
        @Override
        public void paintCell(Graphics2D graphics) {
            drawQueen(graphics, Color.RED, Color.WHITE);
        }
    },
    CHECKER_IN_FIGHT {
        @Override
        public void paintCell(Graphics2D graphics) {
            drawSimpleChecker(graphics, Color.GREEN);
        }
    };

    /* CELL_SIZE is a size of chess board cell, it is a square so all sides are same */
    public static final int CELL_SIZE = 55;

    /* Diameter of checkers, inner round of queen */
    private static final int CHECKER_DIAMETER = 50;

    private static final int QUEEN_INNER_DIAMETER = 40;

    private static final int CIRCLE_OFFSET = (CELL_SIZE - CHECKER_DIAMETER) / 2;

    private static final int QUEEN_INNER_OFFSET = (CHECKER_DIAMETER - QUEEN_INNER_DIAMETER) / 2;

    public abstract void paintCell(Graphics2D graphics);

    private static void drawCell(Graphics2D graphics, Color color) {
        graphics.setPaint(color);
        graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
    }

    private static void drawSimpleChecker(Graphics2D graphics, Color checkerColor) {
        drawCell(graphics, Color.GRAY);

        graphics.setPaint(checkerColor);
        graphics.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, CHECKER_DIAMETER, CHECKER_DIAMETER);
    }

    private static void drawQueen(Graphics2D graphics, Color innerCircleColor, Color checkerColor) {
        drawSimpleChecker(graphics, checkerColor);

        graphics.setPaint(innerCircleColor);
        graphics.fillOval(
                CIRCLE_OFFSET + QUEEN_INNER_OFFSET, CIRCLE_OFFSET + QUEEN_INNER_OFFSET,
                QUEEN_INNER_DIAMETER, QUEEN_INNER_DIAMETER);
    }
}
