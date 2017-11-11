package com.theflyingostrich.game.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ClassicGameActivity extends AppCompatActivity {

    ImageView boardImageView, imageViewX, imageViewO;
    int buttonClickedID;
    Button buttonClicked;
    Bitmap blankBitmap, bitmapX, bitmapO;
    boolean buttonClickedMemory[][];
    int roundCounter;
    View screenView;
    TextView p2TextView;
    TextView p1TextView;
    static final int CPU_DELAY = 500;
    static final int X_COLOR = Color.rgb(25,125,22);
    static final int O_COLOR = Color.rgb(204, 33, 15);
    static final int BOARD_COLOR = Color.rgb(120, 4,14);
    static final int WIN_LINE_COLOR = Color.rgb(00, 00, 00);
    static final int BACKGROUND = Color.rgb(255,170,41);


    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        GameController.getInstance(prefs).stopMusic();
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        MediaPlayer musicMediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        GameController.getInstance(prefs).startMusicFromLastPosition(musicMediaPlayer);
    }
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        buttonClickedMemory = new boolean[3][3];

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);

        setContentView(R.layout.classic_game_layout);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        roundCounter = 0;

        blankBitmap = Bitmap.createBitmap(1000,1000,Bitmap.Config.ARGB_8888);
        bitmapX = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        bitmapO = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        p1TextView = (TextView) findViewById(R.id.p1TextView);
        p2TextView = (TextView) findViewById(R.id.p2TextView);

        if(GameController.getInstance(prefs).getNumPlayers() == 1) {
            p1TextView.setText("You");
            p2TextView.setText("CPU level " + GameController.getInstance(prefs).getDifficulty());
        }
        else {
            p1TextView.setText("Player 1");
            p2TextView.setText("Player 2");
        }

        GameController.getInstance(prefs).setRoundStartingPlayerNum(FirstSecondCPU.FIRST);
        GameController.getInstance(prefs).setCurrentPlayerNum(FirstSecondCPU.FIRST);


        resetRound(screenView);

        updateTurnIndicator();

        GameController.getInstance(prefs).getPlayer1().setScore(0);
        if(GameController.getInstance(prefs).getNumPlayers() == 1) {
            GameController.getInstance(prefs).getCpuPlayer().setScore(0);
        }
        else{
            GameController.getInstance(prefs).getPlayer2().setScore(0);
        }
        updateScore();

    }

    public void sendButtonPos(View view){
        try {
            SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
            screenView = view;
            buttonClickedID = view.getId();
            buttonClicked = (Button) (findViewById(buttonClickedID));
            int r = 0, c = 0;

            if (buttonClickedID == R.id.b00) {
                r = 0;
                c = 0;
            } else if (buttonClickedID == R.id.b01) {
                r = 0;
                c = 1;
            } else if (buttonClickedID == R.id.b02) {
                r = 0;
                c = 2;
            } else if (buttonClickedID == R.id.b10) {
                r = 1;
                c = 0;
            } else if (buttonClickedID == R.id.b11) {
                r = 1;
                c = 1;
            } else if (buttonClickedID == R.id.b12) {
                r = 1;
                c = 2;
            } else if (buttonClickedID == R.id.b20) {
                r = 2;
                c = 0;
            } else if (buttonClickedID == R.id.b21) {
                r = 2;
                c = 1;
            } else if (buttonClickedID == R.id.b22) {
                r = 2;
                c = 2;
            }

            if (buttonClickedMemory[r][c]) {
                return;
            }

            Player currentPlayer = GameController.getInstance(prefs).getCurrentPlayer();

            List<RowColPos> pos = GameController.getInstance(prefs).placeSymbol(r, c, currentPlayer.getMySymbol());
            RowColPos rowColPosCurrent = RowColPos.convertRowCol(r, c);
            drawSymbol(rowColPosCurrent, currentPlayer.getMySymbol());

            if (pos.size() > 0) {
                PixelPos p0 = GameController.getInstance(prefs).convertPositionToPixel(pos.get(0));
                PixelPos p1 = GameController.getInstance(prefs).convertPositionToPixel(pos.get(1));
                PixelPos p2 = GameController.getInstance(prefs).convertPositionToPixel(pos.get(2));
                drawWinningLine(p0, p1, p2);

                GameController.getInstance(prefs).getCurrentPlayer().setScore(GameController.getInstance(prefs).getCurrentPlayer().getScore() + 1);

                MediaPlayer mediaPlayer;
                //if the current player's score = the number of points to win
                if(GameController.getInstance(prefs).getCurrentPlayer().getScore() == GameController.getInstance(prefs).getPointsToWin()){
                    mediaPlayer = MediaPlayer.create(this, R.raw.win_game);
                }
                else {
                    mediaPlayer = MediaPlayer.create(this, R.raw.win_round);
                }
                GameController.getInstance(prefs).playSound(mediaPlayer);
                updateScore();

                showWinningButton(currentPlayer);
                return;
            }


            if (GameController.getInstance(prefs).isBoardFull()) {

                //if the board is full, set the draw button to visible
                Button b = (Button) (findViewById(R.id.drawButton));
                b.setVisibility(View.VISIBLE);
                return;
            }

            final View myView = view;


            GameController.getInstance(prefs).switchCurrentPlayer();
            updateTurnIndicator();

            if (GameController.getInstance(prefs).getNumPlayers() == 1) {

                disableAllButtons(view);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        cpuPlay(myView);
                        enableAllButtons(myView);
                    }
                }, CPU_DELAY);


            }

        }
        catch (Exception e) {
            int i=0;
        }

    }

    public void cpuPlay(View view){

        try {
            SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
            RowColPos rowColCpu = GameController.getInstance(prefs).calculateCpuSymbolPlacement();

            drawSymbol(rowColCpu, GameController.getInstance(prefs).getCpuPlayer().getMySymbol());
            view.invalidate();
            List<RowColPos> pos2 = ResultChecker.posOfWinningSymbols(rowColCpu.getRow(), rowColCpu.getCol(), GameController.getInstance(prefs).getCpuPlayer().getMySymbol());

            if (pos2.size() > 0) {
                PixelPos pixPos0 = GameController.getInstance(prefs).convertPositionToPixel(pos2.get(0));
                PixelPos pixPos1 = GameController.getInstance(prefs).convertPositionToPixel(pos2.get(1));
                PixelPos pixPos2 = GameController.getInstance(prefs).convertPositionToPixel(pos2.get(2));
                GameController.getInstance(prefs).getCpuPlayer().setScore(GameController.getInstance(prefs).getCpuPlayer().getScore() + 1);
                updateScore();
                drawWinningLine(pixPos0, pixPos1, pixPos2);
                showWinningButton(GameController.getInstance(prefs).getCpuPlayer());

                MediaPlayer mediaPlayer;
                if(GameController.getInstance(prefs).getCpuPlayer().getScore() == GameController.getInstance(prefs).getPointsToWin()){
                    mediaPlayer = MediaPlayer.create(this, R.raw.lost_game);
                }
                else {
                    mediaPlayer = MediaPlayer.create(this, R.raw.lost_round);
                }
                GameController.getInstance(prefs).playSound(mediaPlayer);

                return;
            }

            if (GameController.getInstance(prefs).isBoardFull()) {

                Button b = (Button) (findViewById(R.id.drawButton));
                b.setVisibility(View.VISIBLE);
                return;
            }

            GameController.getInstance(prefs).switchCurrentPlayer();
            updateTurnIndicator();
        }
        catch (Exception e) {
            int i =0;
        }

    }

    public void drawSymbol(RowColPos rowColPos, BoxState symbol){

        final Canvas canvas = new Canvas(blankBitmap);
        final Paint paint = new Paint();
        paint.setStrokeWidth(30);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        final PixelPos pixelPos = GameController.getInstance(prefs).convertPositionToPixel(rowColPos);

        buttonClickedMemory[rowColPos.getRow()][rowColPos.getCol()] = true;

        if(symbol == BoxState.O){
            paint.setColor(O_COLOR);
            canvas.drawCircle(pixelPos.getX(), pixelPos.getY(), 80, paint);

            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.o_sound);
            GameController.getInstance(prefs).playSound(mediaPlayer);

        }
        else if(symbol == BoxState.X){
            paint.setColor(X_COLOR);
            canvas.drawLine(pixelPos.getX() - 75, pixelPos.getY() + 75, pixelPos.getX() + 75, pixelPos.getY() - 75, paint);
            canvas.drawLine(pixelPos.getX() + 75, pixelPos.getY() + 75, pixelPos.getX() - 75, pixelPos.getY() - 75, paint);

            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.x_sound);
            GameController.getInstance(prefs).playSound(mediaPlayer);

        }
        screenView.invalidate();

    }


    private void drawWinningLine(PixelPos p0, PixelPos p1, PixelPos p2) {
        Canvas canvas = new Canvas(blankBitmap);
        Paint paint = new Paint();
        paint.setStrokeWidth(40);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(WIN_LINE_COLOR);

        canvas.drawLine(p0.getX(), p0.getY(), p1.getX(), p1.getY(), paint);
        canvas.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), paint);
    }


    private void resetRound(View view) {
        for (int i=0; i< 3; i++) {
            buttonClickedMemory[i][0] = false;
            buttonClickedMemory[i][1] = false;
            buttonClickedMemory[i][2] = false;
        }
        Canvas canvas = new Canvas(blankBitmap);

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        boardImageView = (ImageView)(findViewById(R.id.board_image));
        boardImageView.setImageBitmap(blankBitmap);
        imageViewX = (ImageView)(findViewById(R.id.x_image)) ;
        imageViewX.setImageBitmap(bitmapX);
        imageViewO = (ImageView)(findViewById(R.id.o_image)) ;
        imageViewO.setImageBitmap(bitmapO);

        GameController.getInstance(prefs).resetBoard();

        canvas.drawColor(BACKGROUND);

        Paint paint = new Paint();
        paint.setColor(BOARD_COLOR);
        paint.setStrokeWidth(20);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(345, 10, 345, 980, paint);
        canvas.drawLine(655, 10, 655, 980, paint);
        canvas.drawLine(10, 345, 980, 345, paint);
        canvas.drawLine(10, 655, 980, 655, paint);



        if(roundCounter > 0) {
            GameController.getInstance(prefs).switchRoundStartingPlayer();
        }
        roundCounter++;

        updateTurnIndicator();

        if(GameController.getInstance(prefs).getRoundStartingPlayer() == FirstSecondCPU.CPU){
            cpuPlay(view);
        }


    }

    public void updateTurnIndicator(){
        Canvas canvas_x = new Canvas(bitmapX);
        Canvas canvas_o = new Canvas(bitmapO);
        canvas_x.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas_o.drawColor(0, PorterDuff.Mode.CLEAR);

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);

        Paint p = new Paint();
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStyle(Paint.Style.STROKE);

        if(GameController.getInstance(prefs).getCurrentPlayer().getMySymbol() == BoxState.X){
            p.setColor(Color.WHITE);
            p.setStrokeWidth(35);
            canvas_x.drawLine(20, 20, 80, 80, p);
            canvas_x.drawLine(20, 80, 80, 20, p);
            p.setColor(X_COLOR);
            p.setStrokeWidth(20);

            canvas_x.drawLine(20, 20, 80, 80, p);
            canvas_x.drawLine(20, 80, 80, 20, p);

            p.setColor(O_COLOR);
            canvas_o.drawCircle(50, 50, 30, p);
        }
        else if(GameController.getInstance(prefs).getCurrentPlayer().getMySymbol() == BoxState.O){
            p.setColor(Color.WHITE);
            p.setStrokeWidth(35);
            canvas_o.drawCircle(50, 50, 30, p);
            p.setColor(O_COLOR);
            p.setStrokeWidth(20);
            canvas_o.drawCircle(50, 50, 30, p);

            p.setColor(X_COLOR);
            canvas_x.drawLine(20, 20, 80, 80, p);
            canvas_x.drawLine(20, 80, 80, 20, p);
        }

       imageViewX.invalidate();
       imageViewO.invalidate();
    }

    public void updateScore(){

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        String text = "" + GameController.getInstance(prefs).getPlayer1().getScore() + "  :  " ;

        if(GameController.getInstance(prefs).getNumPlayers()==2) {
            text += GameController.getInstance(prefs).getPlayer2().getScore();
        }
        else {
            text += GameController.getInstance(prefs).getCpuPlayer().getScore();
        }
            TextView textView = (TextView) (findViewById(R.id.scoreText));
            textView.setText(text);


    }

    public void showWinningButton(Player p){

        Button b;

        for (int i=0; i< 3; i++) {
            buttonClickedMemory[i][0] = true;
            buttonClickedMemory[i][1] = true;
            buttonClickedMemory[i][2] = true;
        }

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);

        if(p.getScore() == GameController.getInstance(prefs).getPointsToWin()){
            b = (Button)(findViewById(R.id.winGameButton));
        }
        else {
            b = (Button)(findViewById(R.id.winRoundButton));
        }
        b.setVisibility(View.VISIBLE);
        b.setText(GameController.getInstance(prefs).getWinningString(GameController.getInstance(prefs).getNumPlayers(), p));
    }


    public void clickRoundWinButton(View view){
        resetRound(screenView);
        Button b = (Button)(findViewById(R.id.winRoundButton));
        b.setVisibility(View.INVISIBLE);
    }
    public void clickGameWinButton(View view){

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);

        //If in 1 player mode
        //  If player won, then increase level and start new game. If already at max level, go to game setup screen
        //  If cpu won, then restart game at same level
        //If in 2 player mode, then restart new game
        if(GameController.getInstance(prefs).getNumPlayers() == 1) {
            //If in 1 player mode, if player 1 win then level up
            if (GameController.getInstance(prefs).getPlayer1().getScore() == GameController.getInstance(prefs).getPointsToWin()) {
                int currentDifficulty = GameController.getInstance(prefs).getDifficulty();
                if (currentDifficulty < GameController.MAX_DIFFICULTY) {
                    GameController.getInstance(prefs).setDifficulty(currentDifficulty + 1);
                    Intent intent = new Intent(ClassicGameActivity.this, ClassicGameActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Button b = (Button) (findViewById(R.id.winGameButton));
                    b.setVisibility(View.INVISIBLE);
                }
                else {
                    Intent intent = new Intent(ClassicGameActivity.this, GameSetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Button b = (Button) (findViewById(R.id.winGameButton));
                    b.setVisibility(View.INVISIBLE);
                }
            }
            //CPU won this game, so restart game at this level
            else {
                Intent intent = new Intent(ClassicGameActivity.this, ClassicGameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Button b = (Button) (findViewById(R.id.winGameButton));
                b.setVisibility(View.INVISIBLE);
            }
        }
        //in two player mode, start new game
        else {
            Intent intent = new Intent(ClassicGameActivity.this, GameSetupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Button b = (Button) (findViewById(R.id.winGameButton));
            b.setVisibility(View.INVISIBLE);
        }
    }


    public void clickDrawButton(View view){
        resetRound(screenView);
        Button b = (Button)(findViewById(R.id.drawButton));
        b.setVisibility(View.INVISIBLE);
    }

    public void disableAllButtons(View view){
        ((findViewById(R.id.b00))).setEnabled(false);
        ((findViewById(R.id.b01))).setEnabled(false);
        ((findViewById(R.id.b02))).setEnabled(false);
        ((findViewById(R.id.b10))).setEnabled(false);
        ((findViewById(R.id.b11))).setEnabled(false);
        ((findViewById(R.id.b12))).setEnabled(false);
        ((findViewById(R.id.b20))).setEnabled(false);
        ((findViewById(R.id.b21))).setEnabled(false);
        ((findViewById(R.id.b22))).setEnabled(false);
    }

    public void enableAllButtons(View view){
        ((findViewById(R.id.b00))).setEnabled(true);
        ((findViewById(R.id.b01))).setEnabled(true);
        ((findViewById(R.id.b02))).setEnabled(true);
        ((findViewById(R.id.b10))).setEnabled(true);
        ((findViewById(R.id.b11))).setEnabled(true);
        ((findViewById(R.id.b12))).setEnabled(true);
        ((findViewById(R.id.b20))).setEnabled(true);
        ((findViewById(R.id.b21))).setEnabled(true);
        ((findViewById(R.id.b22))).setEnabled(true);
    }


}
