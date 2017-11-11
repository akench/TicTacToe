package com.theflyingostrich.game.tictactoe;

/**
 * Created by Athu on 8/7/2016.
 */
public class HumanPlayer implements Player{

    private BoxState mySymbol;
    private int score;

    public HumanPlayer(){
        score = 0;
    }

    public BoxState getMySymbol(){
        return mySymbol;
    }
    public void setMySymbol(BoxState s){
        mySymbol = s;
    }
    public int getScore(){
        return score;
    }
    public void setScore(int s){
        score = s;
    }
}
