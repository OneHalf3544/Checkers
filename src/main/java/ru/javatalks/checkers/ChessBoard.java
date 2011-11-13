package ru.javatalks.checkers;

import ru.javatalks.checkers.model.Cell;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Font;

/**
 * This class draws chessboard and sets each cell's attributes - index, status, coordinates on X and Y
 *  
 * @author Kapellan
 */
class ChessBoard extends JPanel {
    
    /* The number of own and enemy's checkers */
    int userCheckers = 12;
    int compCheckers = 12;

    /* cellSideNumber is using for different kinds of checkers. Russian checkers use chess board 8x8 cells.  */
    private final int cellSideNum = 8;
    final int cellNum = cellSideNum * cellSideNum;
    Cell cells[] = new Cell[cellNum];

    /* Each cell's coordinates (left upper corner) */
    int cX;
    int cY;
    
    /* Each checker's diameter */
    private int checkerX;
    private int checkerY;
    
    /* The offset from left and top frame bounds  */
    private final int offsetLeftBound = -30;
    private final int offsetTopBound = -30;

    /* CellSize is a size of chess board cell, it is a square so all sides are same */
    final int cellSize = 55;

    /* Diameter of checkers, inner round of queen */
    private final int checkerDiameter = 50;
    private final int queenInnerDiameter = 40;
    private final int queenInnerOffset = (checkerDiameter - queenInnerDiameter) / 2;
    private final Dimension preferredSize = new Dimension(cellSideNum * cellSize + cellSize, cellSideNum * cellSize + cellSize);
    
    
    /* Those arrays we use in  makeIndex() method and in painting numbers of chess board in method paint()*/
    private final String literals[] = {"NULL", "a", "b", "c", "d", "e", "f", "g", "h"};//
    private final int reversNumbers[] = {0, 8, 7, 6, 5, 4, 3, 2, 1};

    ChessBoard() {
        int cellCount = 0;
        /* The cycle of vertical rows painting */
        for (int vert = 1; vert < cellSideNum + 1; vert++) {
            /* unpaired rows **/
            if (vert % 2 != 0) {
                /* The cycle of horizontal painting. We change black and white squares, also left bottom korner must be grey,
                 *  so we use conditions which check it */
                for (int hor = 1; hor < (cellSideNum + 1); hor++) {
                    cX = offsetLeftBound + (hor * cellSize);
                    cY = offsetTopBound + (vert * cellSize);
                    /* unpaired cols in unpaired rows are white */
                    if (hor % 2 != 0) {
                        cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, CellStatus.WHITE);
                        cellCount++;
                    }
                    /* paired cols in paired rows are grey **/
                    if (hor % 2 == 0) {
                        /* black cells we arrange by checkers. Cells upper 5 row - white checkers (enemy), under 4 row - black checkers (own) **/
                        if (vert > cellSideNum / 2 + 1) {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, CellStatus.USER_CHECKER);
                            cellCount++;
                        } else if (vert < cellSideNum / 2) {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, CellStatus.COMP_CHECKER);
                            cellCount++;
                        } else {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, CellStatus.GREY);
                            cellCount++;
                        }
                    }
                }
            } /* double rows */ else {
                for (int hor = 1; hor <= cellSideNum; hor++) {
                    cX = offsetLeftBound + hor * cellSize;
                    cY = offsetTopBound + vert * cellSize;
                    /* unpaired cols in paired rows are grey */
                    if (hor % 2 != 0) {
                        /* black cells we arrange by checkers. Cells upper 5 row - white checkers (enemy), under 4 row - black checkers (own) **/
                        if (vert > cellSideNum / 2 + 1) {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, CellStatus.WHITE);
                            cellCount++;
                        } else if (vert < cellSideNum / 2) {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, CellStatus.COMP_CHECKER);
                            cellCount++;
                        } else {
                            cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, CellStatus.GREY);
                            cellCount++;
                        }
                    }
                    /* paired cols in paired rows are white */
                    if (hor % 2 == 0) {
                        cells[cellCount] = new Cell(makeIndex(hor, vert), cX, cY, CellStatus.WHITE);
                        cellCount++;
                    }
                }
            }
        }
        setCheckersNum();
        this.setMinimumSize(preferredSize);
        this.setPreferredSize(preferredSize);

    }// End of constructor

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font font = new Font("Dialog", Font.PLAIN, 14);
        graphics.setFont(font);

        /* This cycle make's numbers and literals near chess board bounds */
        for (int i = 1; i <= cellSideNum; i++) {
            /* horizontal number */
            graphics.drawString(Integer.toString(reversNumbers[i]), offsetLeftBound + 40, offsetTopBound + i * cellSize + 30);
            /* vertical literal */
            graphics.drawString(literals[i], offsetLeftBound + i * cellSize + 20, offsetTopBound + 50);
        }

        for (int cCount = 0; cCount < cellNum; cCount++) {
            Cell cell = cells[cCount];
            checkerX = cell.getcX() + cellSize / 2 - checkerDiameter / 2;
            checkerY = cell.getcY() + cellSize / 2 - checkerDiameter / 2;

            cell.getStatus().paintCell(graphics, cell);
        }
        this.setPreferredSize(preferredSize);
        repaint();
    }

    /**
     * This method make's alphabet-digital index of any cell
     * @param indexLiteralX x coordinate
     * @param indexDigitY y coordinate
     * @return The name of cell, specified by indexes
     */
    private String makeIndex(int indexLiteralX, int indexDigitY) {
        return literals[indexLiteralX] + Integer.toString(reversNumbers[indexDigitY]);
    }
    
    private void setCheckersNum() {
        int compNum = 0;
        int userNum = 0;
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].getStatus() == CellStatus.COMP_CHECKER || cells[i].getStatus() == CellStatus.BLACK_QUEEN) {
                compNum++;
            }
            if (cells[i].getStatus() == CellStatus.USER_CHECKER
                    || cells[i].getStatus() == CellStatus.ACTIVE
                    || cells[i].getStatus() == CellStatus.WHITE_QUEEN
                    || cells[i].getStatus() == CellStatus.ACTIVE_QUEEN) {
                userNum++;
            }
        }
        compCheckers = compNum;
        userCheckers = userNum;
    }
}
