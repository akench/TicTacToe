package com.theflyingostrich.game.tictactoe;

import java.util.ArrayList;
import java.util.List;


public class ResultChecker {

    public static boolean isBoardFull(){

        BoxState[][] board = GameController.getInstance().getBoardObj().getBoardArr();

        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j] == BoxState.Empty)
                    return false;
            }
        }
        return true;

    }

    public static List<RowColPos> posOfWinningSymbols(int prevRow, int prevCol, BoxState s){

        BoxState[][] board = GameController.getInstance().getBoardObj().getBoardArr();

        RowColPos lastRowColPos = RowColPos.convertRowCol(prevRow, prevCol);
        List<RowColPos> ret = new ArrayList<RowColPos>();

        for (ThreeAdjacentBoxes adjacentCells: GameController.getInstance().getBoardObj().getWinningCombinations()) {
            if (board[adjacentCells.getFirstCell().getRow()][adjacentCells.getFirstCell().getCol()] == s &&
                    board[adjacentCells.getSecondCell().getRow()][adjacentCells.getSecondCell().getCol()] == s &&
                    board[adjacentCells.getThirdCell().getRow()][adjacentCells.getThirdCell().getCol()] == s) {
                ret.add(adjacentCells.getFirstCell());
                ret.add(adjacentCells.getSecondCell());
                ret.add(adjacentCells.getThirdCell());
            }
        }


        return ret;
    }

}