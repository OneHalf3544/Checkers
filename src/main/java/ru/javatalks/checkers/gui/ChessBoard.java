package ru.javatalks.checkers.gui;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.logic.ChessBoardListener;
import ru.javatalks.checkers.logic.ChessBoardModel;
import ru.javatalks.checkers.model.*;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

import static ru.javatalks.checkers.logic.ChessBoardModel.CELL_SIDE_NUM;

/**
 * This class draws chessboard and sets each cell's attributes - index, status, coordinates on X and Y
 *  
 * @author Kapellan
 */
@Component
public class ChessBoard extends JPanel {
    
    private static final Logger logger = Logger.getLogger(ChessBoard.class);

    @Autowired
    private ChessBoardModel chessBoardModel;

    @Autowired
    private Dialog dialog;

    /** The offset from left frame bound */
    public static final int OFFSET_LEFT_BOUND = 30;

    /** The offset from top frame bound */
    public static final int OFFSET_TOP_BOUND = 30;

    private final Dimension preferredSize = new Dimension(
            CELL_SIDE_NUM * CellType.CELL_SIZE + CellType.CELL_SIZE,
            CELL_SIDE_NUM * CellType.CELL_SIZE + CellType.CELL_SIZE);

    /**
     * Horizontal indexes
     */
    private final String[] literals = {"a", "b", "c", "d", "e", "f", "g", "h"};

    /**
     * Vertical indexes
     */
    private final String[] numbers = {"8", "7", "6", "5", "4", "3", "2", "1"};

    private Cell activeCell = null;

    /**
     * Constructor of chessboard panel
     */
    public ChessBoard() {
        this.setMinimumSize(preferredSize);
        this.setPreferredSize(preferredSize);
    }

    /**
     * Initialise panel
     */
    @PostConstruct
    public void initBoard() {
        chessBoardModel.addListener(new ChessBoardChangeListener());
        repaint();
    }

    private CellType getTypeForCell(int x, int y) {
        // unpaired cols in paired rows are grey
        if ((x + y) % 2 != 0) {
            return CellType.WHITE_CELL;
        }

        Cell cell = chessBoardModel.getCellAt(x, y);
        if (cell.isEmpty()) {
            return CellType.GREY_CELL;
        }

        if (cell.hasOpponentChecker()) {
            return cell.hasQueen() ? CellType.OPPONENT_QUEEN : CellType.OPPONENT_CHECKER;
        }

        if (cell == activeCell) {
            return cell.hasQueen() ? CellType.ACTIVE_QUEEN: CellType.ACTIVE;
        }
        else {
            return cell.hasQueen() ? CellType.USER_QUEEN : CellType.USER_CHECKER;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        logger.debug("paint component");

        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font font = new Font("Dialog", Font.PLAIN, 14);
        graphics.setFont(font);
        
        drawIndexesMark(graphics);

        for (int x = 0; x < CELL_SIDE_NUM; x++) {
            for (int y = 0; y < CELL_SIDE_NUM; y++) {
                getTypeForCell(x, CELL_SIDE_NUM - y - 1).paintCell((Graphics2D) graphics.create(
                        OFFSET_LEFT_BOUND + x * CellType.CELL_SIZE,
                        OFFSET_TOP_BOUND + y * CellType.CELL_SIZE,
                        CellType.CELL_SIZE,
                        CellType.CELL_SIZE));
            }
        }
        this.setPreferredSize(preferredSize);
    }

    private void drawIndexesMark(Graphics2D graphics) {
        /* This cycle make the numbers and literals near chess board bounds */
        for (int i = 0; i < CELL_SIDE_NUM; i++) {
            /* vertical number */
            graphics.drawString(numbers[i],
                    OFFSET_LEFT_BOUND - CellType.CELL_SIZE / 2 ,
                    OFFSET_TOP_BOUND + i * CellType.CELL_SIZE + CellType.CELL_SIZE / 2);

            /* horizontal literal */
            graphics.drawString(literals[i],
                    OFFSET_LEFT_BOUND + CellType.CELL_SIZE / 2 + i * CellType.CELL_SIZE,
                    OFFSET_TOP_BOUND - 10);
        }
    }

    public void setActiveCell(Cell activeCell) {
        this.activeCell = activeCell;
        repaint();
    }

    public boolean hasActiveChecker() {
        return activeCell != null;
    }

    public Cell getActiveCell() {
        return activeCell;
    }

    Cell getCellByCoordinates(int clickedX, int clickedY) {
        int x = (clickedX - OFFSET_LEFT_BOUND) / CellType.CELL_SIZE;
        int y = CELL_SIDE_NUM - (clickedY - OFFSET_TOP_BOUND) / CellType.CELL_SIZE -1;

        if (x >= 0 && x < 8 && y >= 0 && y < 8) {
            return chessBoardModel.getCellAt(x, y);
        }
        return null;
    }

    private class ChessBoardChangeListener implements ChessBoardListener {
        @Override
        public void boardChanged() {
            repaint();
            dialog.checkGameStatus();
        }

        @Override
        public void moved(StepDescription step) {}
    }
}
