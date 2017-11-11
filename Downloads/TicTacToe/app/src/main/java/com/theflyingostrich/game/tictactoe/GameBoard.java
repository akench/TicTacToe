package com.theflyingostrich.game.tictactoe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class GameBoard {

    private BoxState[][] boardArr;
    private static final int SIZE = 3;
    private Collection<ThreeAdjacentBoxes> winningCombinations;
    public GameBoard(){

        boardArr = new BoxState[SIZE][SIZE];
        winningCombinations = new ArrayList<ThreeAdjacentBoxes>();

        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                boardArr[i][j] = BoxState.Empty;
            }
        }
        winningCombinations.add(new ThreeAdjacentBoxes(RowColPos.TOP_LEFT, RowColPos.TOP_CENTER, RowColPos.TOP_RIGHT));
        winningCombinations.add(new ThreeAdjacentBoxes(RowColPos.MID_LEFT, RowColPos.MID_CENTER, RowColPos.MID_RIGHT));
        winningCombinations.add(new ThreeAdjacentBoxes(RowColPos.BOTTOM_LEFT, RowColPos.BOTTOM_CENTER, RowColPos.BOTTOM_RIGHT));
        winningCombinations.add(new ThreeAdjacentBoxes(RowColPos.TOP_LEFT, RowColPos.MID_LEFT, RowColPos.BOTTOM_LEFT));
        winningCombinations.add(new ThreeAdjacentBoxes(RowColPos.TOP_CENTER, RowColPos.MID_CENTER, RowColPos.BOTTOM_CENTER));
        winningCombinations.add(new ThreeAdjacentBoxes(RowColPos.TOP_RIGHT, RowColPos.MID_RIGHT, RowColPos.BOTTOM_RIGHT));
        winningCombinations.add(new ThreeAdjacentBoxes(RowColPos.TOP_LEFT, RowColPos.MID_CENTER, RowColPos.BOTTOM_RIGHT));
        winningCombinations.add(new ThreeAdjacentBoxes(RowColPos.BOTTOM_LEFT, RowColPos.MID_CENTER, RowColPos.TOP_RIGHT));

    }
    public Collection<ThreeAdjacentBoxes> getWinningCombinations () {
        return winningCombinations;
    }
    public BoxState[][] getBoardArr() {
        //Copy to prevent multi-thread concurrent write
        BoxState[][] boardArrCopy = new BoxState[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                boardArrCopy[i][j] = boardArr[i][j];
            }
        }
        return boardArrCopy;
    }
    public boolean isBoxEmpty(int r, int c){
        BoxState b = boardArr[r][c];
        boolean result = false;
        if(b.equals(BoxState.Empty)) {
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }
    public boolean isBoxOccupied(int row, int col) {
        if (isBoxEmpty(row, col)) {
            return false;
        }
        else {
            return true;
        }
    }
    public void setBoxState(int row, int col, BoxState boxState ) {
        boardArr[row][col] = boxState;
    }

    public boolean isBoardFull() {
        boolean result = true;
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if (boardArr[i][j] == BoxState.Empty) {
                    result = false;
                }
            }
        }
        return result;
    }
    public BoxState getBoxState(RowColPos rowColPos) {
        return boardArr[rowColPos.getRow()][rowColPos.getCol()];
    }
    public int getCountOccupiedBoxes(){
        int result = 0;
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if (boardArr[i][j] != BoxState.Empty) {
                    result++;
                }
            }
        }
        return result;
    }
    Collection<RowColPos> getRowCosPosForBoxState(BoxState s) {
        Collection<RowColPos> result = new ArrayList<RowColPos>();
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if (boardArr[i][j] == s) {
                    result.add(RowColPos.convertRowCol(i,j));
                }
            }
        }
        return result;
    }
    public GameBoard getDeepCopy() {
        GameBoard gb = new GameBoard();
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                gb.boardArr[i][j] = boardArr[i][j];
            }
        }
        return gb;
    }
    public Collection<ThreeAdjacentBoxes> getPossibleWinComboWithRowCol(RowColPos rps) {
        Collection<ThreeAdjacentBoxes> result = new ArrayList<ThreeAdjacentBoxes>();
        Iterator<ThreeAdjacentBoxes> iter = winningCombinations.iterator();
        while(iter.hasNext()) {
            ThreeAdjacentBoxes adjacentBoxes = iter.next();
            if(adjacentBoxes.contains(rps)) {
                result.add(adjacentBoxes);
            }
        }
        return result;
    }

}
