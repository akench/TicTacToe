package com.theflyingostrich.game.tictactoe;

/**
 * Created by Athu on 8/7/2016.
 */
public interface CpuPlayer extends Player{

    public RowColPos calculateSymbolPlacement();
    public int getDifficulty();

    public void setDifficulty(int difficulty);
}
