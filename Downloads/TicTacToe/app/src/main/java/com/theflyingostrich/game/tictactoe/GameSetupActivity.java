package com.theflyingostrich.game.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class GameSetupActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar difficultySeekBar, pointsToWinSeekBar;
    private TextView difficultyText, easyText, hardText, difficultyNumText;
    private ImageView phoneImg, playerImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_setup_layout);
        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);

        difficultySeekBar = (SeekBar) findViewById(R.id.difficultySeekBar);
        pointsToWinSeekBar = (SeekBar)findViewById(R.id.pointsToWinSeekBar);
        difficultySeekBar.setOnSeekBarChangeListener(this);
        pointsToWinSeekBar.setOnSeekBarChangeListener(this);

        difficultyText = (TextView) findViewById(R.id.difficultyTextView);
        easyText = (TextView)findViewById(R.id.easyTextView);
        hardText = (TextView)findViewById(R.id.hardTextView);
        difficultyNumText = (TextView)findViewById(R.id.difficultyNumTextView);
        phoneImg = (ImageView)findViewById(R.id.phoneImage);
        playerImg = (ImageView)findViewById(R.id.playerTwoImage);

        difficultySeekBar.setProgress(GameController.getInstance(prefs).getDifficulty());

        int numPlayers = GameController.getInstance(prefs).getNumPlayers();

        if(numPlayers == 2){
            difficultyText.setVisibility(View.INVISIBLE);
            difficultyNumText.setVisibility(View.INVISIBLE);
            difficultySeekBar.setVisibility(View.INVISIBLE);
            easyText.setVisibility(View.INVISIBLE);
            hardText.setVisibility(View.INVISIBLE);
            playerImg.setVisibility(View.VISIBLE);
        }
        else {
            phoneImg.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        GameController.getInstance(prefs).stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        MediaPlayer musicMediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        GameController.getInstance(prefs).startMusicFromLastPosition(musicMediaPlayer);
        int difficultyStored = GameController.getInstance(prefs).getDifficulty();
        String x = "" + difficultyStored;
        difficultyNumText.setText(x);
        difficultySeekBar.setProgress(difficultyStored);
        int pointsToWin = GameController.getInstance(prefs).getPointsToWin();
        //Since progress bar value starts from 0, but we measure points to win from 1
        pointsToWinSeekBar.setProgress(pointsToWin - 1);

    }

    public void clickPlay(View view){

        Intent intent = new Intent(this, ClassicGameActivity.class);
        startActivity(intent);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        if(seekBar == difficultySeekBar) {
            String x = "" + progress;
            difficultyNumText.setText(x);
            GameController.getInstance(prefs).setDifficulty(progress);
            SharedPreferences.Editor editor = getSharedPreferences(GameController.PREFS, MODE_PRIVATE).edit();
            editor.putInt("difficulty", progress);
            editor.apply();
        }
        else if(seekBar == pointsToWinSeekBar) {
            GameController.getInstance(prefs).setPointsToWin(progress + 1);
            SharedPreferences.Editor editor = getSharedPreferences(GameController.PREFS, MODE_PRIVATE).edit();
            editor.putInt("pointsToWin", progress + 1);
            editor.apply();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
