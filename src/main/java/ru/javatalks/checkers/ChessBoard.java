package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.model.ChessBoardModel;

import javax.swing.*;
import java.awt.*;

import static ru.javatalks.checkers.model.ChessBoardModel.CELL_SIDE_NUM;

/**
 * This class draws chessboard and sets each cell's attributes - index, status, coordinates on X and Y
 *  
 * @author Kapellan
 */
@Component
public class ChessBoard extends JPanel {

    @Autowired
    private ChessBoardModel chessBoardModel;

    private final CellStatus[][] cells = new CellStatus[CELL_SIDE_NUM][CELL_SIDE_NUM];

    /* The offset from left and top frame bounds  */
    private static final int OFFSET_LEFT_BOUND = -30;

    private static final int OFFSET_TOP_BOUND = -30;

    private final Dimension preferredSize
            = new Dimension(CELL_SIDE_NUM * CellStatus.CELL_SIZE + CellStatus.CELL_SIZE, CELL_SIDE_NUM * CellStatus.CELL_SIZE + CellStatus.CELL_SIZE);
    
    /* Those arrays we use in  makeIndex() method and in painting numbers of chess board in method paint()*/
    private final String[] literals = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private final String[] numbers = {"8", "7", "6", "5", "4", "3", "2", "1"};

    public ChessBoard() {
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
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font font = new Font("Dialog", Font.PLAIN, 14);
        graphics.setFont(font);
        
        drawIndexesMark(graphics);


        for (int x = 0; x < CELL_SIDE_NUM; x++) {
            for (int y = 0; y < CELL_SIDE_NUM; y++) {
                cells[x][y].paintCell((Graphics2D) graphics.create(
                        OFFSET_LEFT_BOUND + x * CellStatus.CELL_SIZE,
                        OFFSET_TOP_BOUND + y * CellStatus.CELL_SIZE,
                        CellStatus.CELL_SIZE,
                        CellStatus.CELL_SIZE));
            }
        }
        this.setPreferredSize(preferredSize);
        repaint();
    }

    private void drawIndexesMark(Graphics2D graphics) {
        /* This cycle make the numbers and literals near chess board bounds */
        for (int i = 0; i < CELL_SIDE_NUM; i++) {
            /* horizontal number */
            graphics.drawString(numbers[i], OFFSET_LEFT_BOUND + 40, OFFSET_TOP_BOUND + i * CellStatus.CELL_SIZE + 30);
            /* vertical literal */
            graphics.drawString(literals[i], OFFSET_LEFT_BOUND + i * CellStatus.CELL_SIZE + 20, OFFSET_TOP_BOUND + 50);
        }
    }
}
