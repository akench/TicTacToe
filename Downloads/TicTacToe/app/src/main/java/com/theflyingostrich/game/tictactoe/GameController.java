package com.theflyingostrich.game.tictactoe;

import android.content.SharedPreferences;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Athu on 8/7/2016.
 */
public class GameController {

    private static GameController instance;
    private int numPlayers;
    private GameBoard boardObj;
    private ResultChecker resultChecker;
    private int pointsToWin;
    private HumanPlayer player1;
    private HumanPlayer player2;
    private CpuPlayer cpuPlayer;
    private FirstSecondCPU roundStartingPlayerNum = FirstSecondCPU.FIRST;
    private FirstSecondCPU currentPlayerNum = FirstSecondCPU.FIRST;
    private MediaPlayer soundMediaPlayer;
    private MediaPlayer musicMediaPlayer;
    private boolean soundState, musicState;
    private int difficulty;
    public static int MAX_DIFFICULTY = 10;
    public static final String PREFS = "Prefs";
    private int lastMusicPosition = 0;

    public FirstSecondCPU getRoundStartingPlayer() {
        return roundStartingPlayerNum;
    }

    public void setRoundStartingPlayerNum(FirstSecondCPU roundStartingPlayerNum) {
        this.roundStartingPlayerNum = roundStartingPlayerNum;
    }

    public void switchRoundStartingPlayer(){
        if(numPlayers == 1) {
            if (roundStartingPlayerNum == FirstSecondCPU.FIRST) {
                setRoundStartingPlayerNum(FirstSecondCPU.CPU);
                currentPlayerNum = FirstSecondCPU.CPU;
            }
            else{
                setRoundStartingPlayerNum(FirstSecondCPU.FIRST);
                currentPlayerNum = FirstSecondCPU.FIRST;
            }
        }
        else {
            if(roundStartingPlayerNum == FirstSecondCPU.FIRST){
                setRoundStartingPlayerNum(FirstSecondCPU.SECOND);
                currentPlayerNum = FirstSecondCPU.SECOND;
            }
            else{
                setRoundStartingPlayerNum(FirstSecondCPU.FIRST);
                currentPlayerNum = FirstSecondCPU.FIRST;
            }
        }
    }



    public Player getCurrentPlayer() {
        if (numPlayers == 1) {
            if(currentPlayerNum == FirstSecondCPU.FIRST){
                return player1;
            }
            else{
                return cpuPlayer;
            }

        }
        else if (numPlayers == 2) {
            if (currentPlayerNum == FirstSecondCPU.FIRST) {
                return player1;
            }
            else {
                return player2;
            }
        }
        return null;
    }
    public void switchCurrentPlayer() {
        if(numPlayers == 1){
            if(currentPlayerNum == FirstSecondCPU.FIRST) {
                currentPlayerNum = FirstSecondCPU.CPU;
            }
            else{
                currentPlayerNum = FirstSecondCPU.FIRST;
            }
        }
        else {
            if(currentPlayerNum == FirstSecondCPU.FIRST){
                currentPlayerNum = FirstSecondCPU.SECOND;
            }
            else{
                currentPlayerNum = FirstSecondCPU.FIRST;
            }
        }
    }
    public void setCurrentPlayerNum(FirstSecondCPU x){
        currentPlayerNum = x;
    }
    public HumanPlayer getPlayer1() {
        return player1;
    }

    public void setPlayer1(HumanPlayer player1) {
        this.player1 = player1;
    }

    public HumanPlayer getPlayer2() {
        return player2;
    }

    public void setPlayer2(HumanPlayer player2) {
        this.player2 = player2;
    }

    public CpuPlayer getCpuPlayer() {
        return cpuPlayer;
    }

    public void setCpuPlayer(CpuPlayer cpuPlayer) {
        this.cpuPlayer = cpuPlayer;
    }

    public GameController() {
        boardObj = new GameBoard();
        resultChecker = new ResultChecker();
        //TODO value from SharedPreference file
        musicState = true;
        soundState = true;
        difficulty = 0;
    }

    public static GameController getInstance(SharedPreferences prefs ) {
        if (instance == null) {
            instance = new GameController();
            if(prefs !=null) {
                //Initialize State from preferences file
                //NumPlayers, Player1, Player2, Difficulty, pointsToWinGame
                instance.soundState = prefs.getBoolean("soundPref", true);
                instance.musicState = prefs.getBoolean("musicPref", true);
                instance.numPlayers = prefs.getInt("numPlayers", 1);
                instance.difficulty = prefs.getInt("difficulty", 0);
                instance.pointsToWin = prefs.getInt("pointsToWin", 2);
                //setup both players and cpu player
                HumanPlayer player1 = new HumanPlayer();
                player1.setMySymbol(BoxState.X);
                instance.setPlayer1(player1);

                CpuPlayer cpuPlayer = new CpuPlayerDynamic(instance.getDifficulty());
                if (player1.getMySymbol() == BoxState.X) {
                    cpuPlayer.setMySymbol(BoxState.O);
                }
                else {
                    cpuPlayer.setMySymbol(BoxState.X);
                }
                instance.setCpuPlayer(cpuPlayer);

                HumanPlayer player2 = new HumanPlayer();
                if(instance.getPlayer1().getMySymbol() == BoxState.X) {
                    player2.setMySymbol(BoxState.O);
                }
                else {
                    player2.setMySymbol(BoxState.X);
                }
                instance.setPlayer2(player2);

            }
        }
        return instance;
    }
    public static GameController getInstance() {
        return instance;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int n) {
        numPlayers = n;
    }

    public GameBoard getBoardObj() {
        return boardObj;
    }

    public List<RowColPos> placeSymbol(int r, int c, BoxState s) {
        List<RowColPos> result = new ArrayList<RowColPos>();
        if (isBoxEmpty(r, c)) {

            boardObj.setBoxState(r, c, s);
            result = resultChecker.posOfWinningSymbols(r, c, s);
        }
        return result;
    }

    public boolean isBoxEmpty(int r, int c) {
        return boardObj.isBoxEmpty(r, c);
    }

    public boolean isBoxOccupied(int r, int c) {
        return boardObj.isBoxOccupied(r, c);
    }

    public RowColPos calculateCpuSymbolPlacement() {
     RowColPos rowColPos = cpuPlayer.calculateSymbolPlacement();
        boardObj.setBoxState(rowColPos.getRow(), rowColPos.getCol(), cpuPlayer.getMySymbol());
        return rowColPos;
    }

    public PixelPos convertPositionToPixel(RowColPos rowCol){
        PixelPos retValue = new PixelPos(-1, -1);

        if(rowCol.getRow() == 0){
            //170
            retValue.setY(175);
        }
        if(rowCol.getRow() == 1){
            //510
            retValue.setY(494);
        }
        if(rowCol.getRow() == 2){
            //850
            retValue.setY(817);
        }
        if(rowCol.getCol() == 0){
            //170
            retValue.setX(175);
        }
        if(rowCol.getCol() == 1){
            //510
            retValue.setX(494);
        }
        if(rowCol.getCol() == 2){
            //850
            retValue.setX(817);
        }

        return retValue;
    }

    public String getWinningString(int numPlayers, Player p) {

        String ret = "";
        if(numPlayers == 1) {
            if(p == getPlayer1()) {
                ret += "You have ";
            }
            else if(p == getCpuPlayer()){
                ret+= "CPU has ";
            }
        }
        else {
            if(p == getPlayer1()){
                ret += "Player 1 has ";
            }
            else if(p == getPlayer2()){
                ret += "Player 2 has ";
            }
        }

        if(p.getScore() == pointsToWin){
            ret += "won the game!";
            //if 1 player mode and player won is player1 and diffifulty is not MAX level yet then
            // say that they have defeated level X
            if (numPlayers == 1 && p == getPlayer1()) {
                ret += " Level " + difficulty + " completed.";
                if (difficulty < MAX_DIFFICULTY) {
                    ret+= " Next level starting";
                }
                else {

                }
            }
        }
        else if(p.getScore() != pointsToWin) {
            ret += "won the round!";
        }

        ret += " Click to Continue";
        return ret;
    }

    public void resetBoard() {
        boardObj = new GameBoard();
    }
    public boolean isBoardFull() {
        return boardObj.isBoardFull();
    }
    public void setPointsToWin(int n){
        pointsToWin = n;
    }
    public int getPointsToWin(){
        return pointsToWin;
    }




    private synchronized void resetSoundMediaPlayer() {
        if (soundMediaPlayer !=null) {
            if (soundMediaPlayer.isPlaying()) {
                soundMediaPlayer.pause();
            }
            soundMediaPlayer.release();
            soundMediaPlayer = null;
        }
    }
    public void playSound(MediaPlayer mp) {
        resetSoundMediaPlayer();
        if(GameController.getInstance(null).getSoundState() == true) {
            this.soundMediaPlayer = mp;
            mp.start();
            try {
                Thread.sleep(300);
            }
            catch(Exception e){

            }

        }
    }

    public void startMusicFromLastPosition(MediaPlayer mp) {
        //call stopMusic to save last position and also release old music player object
        stopMusic();
        this.musicMediaPlayer = mp;
        if(musicState == true) {
            musicMediaPlayer.setVolume(.3f, .3f);
            musicMediaPlayer.seekTo(lastMusicPosition);
            musicMediaPlayer.setLooping(true);
            musicMediaPlayer.start();
        }



    }
    public synchronized void stopMusic() {
        if (musicMediaPlayer !=null) {
            if (musicMediaPlayer.isPlaying()) {
                lastMusicPosition = musicMediaPlayer.getCurrentPosition();
                musicMediaPlayer.pause();
            }
            musicMediaPlayer.release();
            musicMediaPlayer = null;
        }
    }

    public boolean getSoundState(){
        return soundState;
    }
    public void setSoundState(boolean b){
        //TODO save to sharedPreference file
        soundState = b;
    }
    public boolean getMusicState(){
        return musicState;
    }
    public void setMusicState(boolean b){
    //TODO save to sharedPreference file
        musicState = b;
    }

    public boolean incrementDifficulty(){
        //TODO save to sharedPreference file
        boolean maxDifficultyReached = false;
        if (difficulty < 10) {
            difficulty++;
        }
        if (difficulty == 10)
        {
            maxDifficultyReached = true;
        }
        return maxDifficultyReached;
    }

    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int d){
        difficulty = d;
        cpuPlayer.setDifficulty(d);
    }
}
