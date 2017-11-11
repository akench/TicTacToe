package com.theflyingostrich.game.tictactoe;

import java.util.ArrayList;
import java.util.Collection;

public enum RowColPos {
    TOP_LEFT(0,0),
    TOP_CENTER(0,1),
    TOP_RIGHT(0,2),
    MID_LEFT(1,0),
    MID_CENTER(1,1),
    MID_RIGHT(1,2),
    BOTTOM_LEFT(2,0),
    BOTTOM_CENTER(2,1),
    BOTTOM_RIGHT(2,2);

    private int row;
    private int col;

    RowColPos(int row, int col ){
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }


public static RowColPos convertRowCol(int row, int col) {
    if (row == 0) {
        if (col == 0) {
            return RowColPos.TOP_LEFT;
        }
        else if (col == 1){
            return RowColPos.TOP_CENTER;
        }
        else if (col == 2) {
            return RowColPos.TOP_RIGHT;
        }
    }
    if (row == 1) {
        if (col == 0) {
            return RowColPos.MID_LEFT;
        }
        else if (col == 1){
            return RowColPos.MID_CENTER;
        }
        else if (col == 2) {
            return RowColPos.MID_RIGHT;
        }
    }if (row == 2) {
        if (col == 0) {
            return RowColPos.BOTTOM_LEFT;
        }
        else if (col == 1){
            return RowColPos.BOTTOM_CENTER;
        }
        else if (col == 2) {
            return RowColPos.BOTTOM_RIGHT;
        }
    }
    return null;
}
    public boolean isCorner() {
        if (this == RowColPos.TOP_LEFT || this == RowColPos.TOP_RIGHT || this == RowColPos.BOTTOM_LEFT || this == RowColPos.BOTTOM_RIGHT) {
            return true;
        }
        else {
            return false;
        }
    }
    public boolean isEdge() {
        if (this == RowColPos.TOP_CENTER || this == RowColPos.MID_LEFT || this == RowColPos.MID_RIGHT || this == RowColPos.BOTTOM_CENTER) {
            return true;
        }
        else {
            return false;
        }
    }


    public static Collection<RowColPos> getAllCornerPos() {
        Collection<RowColPos> result = new ArrayList<RowColPos>();
        result.add(RowColPos.TOP_LEFT);
        result.add(RowColPos.TOP_RIGHT);
        result.add(RowColPos.BOTTOM_LEFT);
        result.add(RowColPos.BOTTOM_RIGHT);
        return result;
    }
    public static Collection<RowColPos> getAllCornerAndMidCenterPos() {
        Collection<RowColPos> result = new ArrayList<RowColPos>();
        result.add(RowColPos.TOP_LEFT);
        result.add(RowColPos.TOP_RIGHT);
        result.add(RowColPos.BOTTOM_LEFT);
        result.add(RowColPos.BOTTOM_RIGHT);
        result.add(RowColPos.MID_CENTER);
        return result;
    }
    public boolean isAdjacentCell(RowColPos otherpos) {
        int rowDiff = this.row - otherpos.getRow();
        int colDiff = this.col - otherpos.getCol();
        double distance = Math.sqrt( (rowDiff*rowDiff) + (colDiff*colDiff));
        if (distance < 2.0) {
            return true;
        }
        else {
            return false;
        }
    }
}
