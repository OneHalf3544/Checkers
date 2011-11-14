package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javatalks.checkers.model.ChessBoardModel;

import javax.swing.*;
import java.awt.*;

import static ru.javatalks.checkers.model.ChessBoardModel.CELL_SIDE_NUM;

/**
 * This class draws chessboard and sets each cell's attributes - index, status, coordinates on X and Y
 *  
 * @author Kapellan
 */
class ChessBoard extends JPanel {

    @Autowired
    private ChessBoardModel chessBoardModel;

    private final CellStatus[][] cells = new CellStatus[CELL_SIDE_NUM][CELL_SIDE_NUM];

    /* The offset from left and top frame bounds  */
    private static final int offsetLeftBound = -30;
    private static final int offsetTopBound = -30;

    /* CellSize is a size of chess board cell, it is a square so all sides are same */
    static final int CELL_SIZE = 55;

    private final Dimension preferredSize
            = new Dimension(CELL_SIDE_NUM * CELL_SIZE + CELL_SIZE, CELL_SIDE_NUM * CELL_SIZE + CELL_SIZE);
    
    /* Those arrays we use in  makeIndex() method and in painting numbers of chess board in method paint()*/
    private final String[] literals = {"NULL", "a", "b", "c", "d", "e", "f", "g", "h"};//
    private final int[] reversNumbers = {0, 8, 7, 6, 5, 4, 3, 2, 1};

    ChessBoard() {
        int cellCount = 0;
        /* The cycle of vertical rows painting */
        for (int v = 0; v < CELL_SIDE_NUM; v++) {
            for (int h = 0; h < CELL_SIDE_NUM; h++) {
                /* unpaired cols in paired rows are grey */
                if ((v + h) % 2 == 0) {
                    cells[h][v] = CellStatus.WHITE;
                    continue;
                }

                // grey cells in the center of board
                if (v > 2 && v < 5) {
                    cells[h][v] = CellStatus.GREY;
                } else {
                    /* black cells we arrange by checkers. Cells upper 5 row - white checkers (enemy), under 4 row - black checkers (own) **/
                    cells[h][v] = v < CELL_SIDE_NUM / 2 ? CellStatus.COMP_CHECKER : CellStatus.USER_CHECKER;
                }
            }
        }
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
        for (int i = 1; i <= CELL_SIDE_NUM; i++) {
            /* horizontal number */
            graphics.drawString(Integer.toString(reversNumbers[i]), offsetLeftBound + 40, offsetTopBound + i * CELL_SIZE + 30);
            /* vertical literal */
            graphics.drawString(literals[i], offsetLeftBound + i * CELL_SIZE + 20, offsetTopBound + 50);
        }

        for (int x = 0; x < CELL_SIDE_NUM; x++) {
            for (int y = 0; y < CELL_SIDE_NUM; y++) {
                CellStatus cell = cells[x][y];
                cell.paintCell(graphics, cell);
            }
        }
        this.setPreferredSize(preferredSize);
        repaint();
    }

    /**
     * This method make alphabet-digital index of any cell
     * @param indexLiteralX x coordinate
     * @param indexDigitY y coordinate
     * @return The name of cell, specified by indexes
     */
    private String makeIndex(int indexLiteralX, int indexDigitY) {
        return literals[indexLiteralX] + Integer.toString(reversNumbers[indexDigitY]);
    }

}
