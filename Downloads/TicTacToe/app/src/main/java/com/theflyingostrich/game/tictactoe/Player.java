package com.theflyingostrich.game.tictactoe;

/**
 * Created by Athu on 8/18/2016.
 */
public interface Player {

    public BoxState getMySymbol();
    public void setMySymbol(BoxState s);
    public int getScore();
    public void setScore(int s);

}
