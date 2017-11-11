package com.theflyingostrich.game.tictactoe;


public class ThreeAdjacentBoxes {
    private RowColPos firstCell;
    private RowColPos secondCell;
    private RowColPos thirdCell;

    public ThreeAdjacentBoxes(RowColPos firstCell, RowColPos secondCell, RowColPos thirdCell) {
        this.firstCell = firstCell;
        this.secondCell = secondCell;
        this.thirdCell = thirdCell;
    }

    public RowColPos getFirstCell() {
        return firstCell;
    }

    public void setFirstCell(RowColPos firstCell) {
        this.firstCell = firstCell;
    }

    public RowColPos getSecondCell() {
        return secondCell;
    }

    public void setSecondCell(RowColPos secondCell) {
        this.secondCell = secondCell;
    }

    public RowColPos getThirdCell() {
        return thirdCell;
    }

    public void setThirdCell(RowColPos thirdCell) {
        this.thirdCell = thirdCell;
    }


    public boolean contains(RowColPos rps) {
        if (rps == firstCell || rps == secondCell || rps == thirdCell) {
            return true;
        }
        return false;
    }
}