package ru.javatalks.checkers;

import java.awt.*;

/**
 * Date: 12.11.11
 * Time: 21:26
 *
 * @author OneHalf
 */
public enum CellStatus {

    NONE {
        @Override
        public void paintCell(Graphics2D graphics) {
            throw new IllegalStateException("can not draw cell of this type");
        }
    },

    WHITE {
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.WHITE);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        }
    },
    GREY {
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        }
    },
    USER_CHECKER { // 3
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            graphics.setPaint(Color.WHITE);
            graphics.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, CHECKER_DIAMETER, CHECKER_DIAMETER);
        }
    },
    COMP_CHECKER {
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            graphics.setPaint(Color.BLACK);
            graphics.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, CHECKER_DIAMETER, CHECKER_DIAMETER);
        }
    },
    ACTIVE {
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            graphics.setPaint(Color.RED);
            graphics.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, CHECKER_DIAMETER, CHECKER_DIAMETER);
        }
    },
    WHITE_QUEEN {
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            graphics.setPaint(Color.WHITE);
            graphics.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, CHECKER_DIAMETER, CHECKER_DIAMETER);
            graphics.setPaint(Color.LIGHT_GRAY);
            graphics.fillOval(
                    CIRCLE_OFFSET + QUEEN_INNER_OFFSET, CIRCLE_OFFSET + QUEEN_INNER_OFFSET,
                    QUEEN_INNER_DIAMETER, QUEEN_INNER_DIAMETER);

        }
    },
    BLACK_QUEEN {
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            graphics.setPaint(Color.BLACK);
            graphics.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, CHECKER_DIAMETER, CHECKER_DIAMETER);
            graphics.setPaint(Color.LIGHT_GRAY);
            graphics.fillOval(
                    CIRCLE_OFFSET + QUEEN_INNER_OFFSET, CIRCLE_OFFSET + QUEEN_INNER_OFFSET,
                    QUEEN_INNER_DIAMETER, QUEEN_INNER_DIAMETER);
        }
    },
    ACTIVE_QUEEN {
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            graphics.setPaint(Color.WHITE);
            graphics.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, CHECKER_DIAMETER, CHECKER_DIAMETER);
            graphics.setPaint(Color.RED);
            graphics.fillOval(
                    CIRCLE_OFFSET + QUEEN_INNER_OFFSET, CIRCLE_OFFSET + QUEEN_INNER_OFFSET,
                    QUEEN_INNER_DIAMETER, QUEEN_INNER_DIAMETER);
        }
    },
    CHECKER_IN_FIGHT {
        @Override
        public void paintCell(Graphics2D graphics) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
            graphics.setPaint(Color.GREEN);
            graphics.fillOval(CIRCLE_OFFSET, CIRCLE_OFFSET, CHECKER_DIAMETER, CHECKER_DIAMETER);
        }
    };

    /* CELL_SIZE is a size of chess board cell, it is a square so all sides are same */
    public static final int CELL_SIZE = 55;

    /* Diameter of checkers, inner round of queen */
    private static final int CHECKER_DIAMETER = 50;
    private static final int QUEEN_INNER_DIAMETER = 40;

    private static final int CIRCLE_OFFSET = (CELL_SIZE  - CHECKER_DIAMETER) / 2;
    private static final int QUEEN_INNER_OFFSET = (CHECKER_DIAMETER - QUEEN_INNER_DIAMETER) / 2;

    public abstract void paintCell(Graphics2D graphics);
}
