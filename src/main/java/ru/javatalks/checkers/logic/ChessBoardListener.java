package ru.javatalks.checkers.logic;

import ru.javatalks.checkers.model.StepDescription;

/**
 * Chessboard changes listener
 *
 * Date: 15.11.11
 * Time: 0:18
 *
 * @author OneHalf
 */
public interface ChessBoardListener {

    /**
     * On any changes on chessboard
     */
    public void boardChanged();

    /**
     * On any step (move or fight)
     * @param step step description
     */
    public void moved(StepDescription step);
}
