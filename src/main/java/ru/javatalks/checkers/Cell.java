package ru.javatalks.checkers;

public class Cell {

    String index;
    CellStatus status;
    int cX;
    int cY;

     Cell(String index, int cX, int cY, CellStatus status) {
        this.index = index;
        this.cX = cX;
        this.cY = cY;
        this.status = status;
    }

    public Cell(CellStatus status) {
        this.status = status;
    }
}
