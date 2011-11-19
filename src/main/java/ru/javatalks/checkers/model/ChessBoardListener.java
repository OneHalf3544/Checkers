package ru.javatalks.checkers.model;

/**
 * Date: 15.11.11
 * Time: 0:18
 *
 * @author OneHalf
 */
public interface ChessBoardListener {
    
    public void boardChanged();

    public void moved(StepDescription step);
}
