package ru.javatalks.checkers;

import ru.javatalks.checkers.model.Cell;

import java.awt.*;

/**
 * Date: 12.11.11
 * Time: 21:26
 *
 * @author OneHalf
 */
public enum CellStatus {

    NONE(false) {
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            throw new IllegalStateException("can not draw cell of this type");
        }
    },

    WHITE(true) { // 1
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.WHITE);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
        }
    },
    GREY(false) { // 2
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
        }
    },
    USER_CHECKER(true) { // 3
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
            graphics.setPaint(Color.WHITE);
            graphics.fillOval(checkerX, checkerY, checkerDiameter, checkerDiameter);
        }
    },
    COMP_CHECKER(false) { // 4
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
            graphics.setPaint(Color.BLACK);
            graphics.fillOval(checkerX, checkerY, checkerDiameter, checkerDiameter);
        }
    },
    ACTIVE(true) { // 5
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
            graphics.setPaint(Color.RED);
            graphics.fillOval(checkerX, checkerY, checkerDiameter, checkerDiameter);
        }
    },
    WHITE_QUEEN(true) { // 6
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
            graphics.setPaint(Color.WHITE);
            graphics.fillOval(checkerX, checkerY, checkerDiameter, checkerDiameter);
            graphics.setPaint(Color.LIGHT_GRAY);
            graphics.fillOval(checkerX + queenInnerOffset, checkerY + queenInnerOffset, queenInnerDiameter, queenInnerDiameter);

        }
    },
    BLACK_QUEEN(false) { // 7
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
            graphics.setPaint(Color.BLACK);
            graphics.fillOval(checkerX, checkerY, checkerDiameter, checkerDiameter);
            graphics.setPaint(Color.LIGHT_GRAY);
            graphics.fillOval(checkerX + queenInnerOffset, checkerY + queenInnerOffset, queenInnerDiameter, queenInnerDiameter);
        }
    },
    ACTIVE_QUEEN(true) { // 8
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
            graphics.setPaint(Color.WHITE);
            graphics.fillOval(checkerX, checkerY, checkerDiameter, checkerDiameter);
            graphics.setPaint(Color.RED);
            graphics.fillOval(checkerX + queenInnerOffset, checkerY + queenInnerOffset, queenInnerDiameter, queenInnerDiameter);
        }
    },
    CHECKER_IN_FIGHT(false) { //9
        @Override
        public void paintCell(Graphics2D graphics, Cell cell) {
            graphics.setPaint(Color.GRAY);
            graphics.fillRect(cell.getcX(), cell.getcY(), cellSize, cellSize);
            graphics.setPaint(Color.GREEN);
            graphics.fillOval(checkerX, checkerY, checkerDiameter, checkerDiameter);
        }
    };


    private final boolean userCell;

    CellStatus(boolean isUserCell) {
        this.userCell = isUserCell;
    }

    public abstract void paintCell(Graphics2D graphics, Cell cell);
}
