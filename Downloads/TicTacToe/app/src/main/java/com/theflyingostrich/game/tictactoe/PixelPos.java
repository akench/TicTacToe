package com.theflyingostrich.game.tictactoe;

/**
 * Created by Athu on 8/12/2016.
 */
public class PixelPos {
    private int x;
    private int y;

    public PixelPos(int xcoord, int ycoord){
        x = xcoord;
        y = ycoord;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {

        this.y = y;
    }


}
