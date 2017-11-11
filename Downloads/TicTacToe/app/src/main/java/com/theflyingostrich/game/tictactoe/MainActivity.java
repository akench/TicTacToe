package com.theflyingostrich.game.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        //Intent svc = new Intent(this, BackgroundSoundService.class);
        //startService(svc);
        MediaPlayer musicMediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        GameController.getInstance(prefs).startMusicFromLastPosition(musicMediaPlayer);
    }

    public void startGameSetupActivity(View view) {

        Intent intent = new Intent(this, GameSetupActivity.class);

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);
        overridePendingTransition(R.animator.settings_animation, R.animator.settings_animation2);


        int buttonClickedID = ((ImageButton)view).getId();
        int numPlayers = 1;

        if(buttonClickedID == R.id.singleplayer_button){
            numPlayers = 1;
        }
        else if(buttonClickedID == R.id.multiplayer_button){
            numPlayers = 2;
        }
        GameController.getInstance(prefs).setNumPlayers(numPlayers);
        //Save numPlayers to prefs also
        SharedPreferences.Editor editor = getSharedPreferences(GameController.PREFS, MODE_PRIVATE).edit();
        editor.putInt("numPlayers", numPlayers);
        editor.apply();

        startActivity(intent);
    }

    public void startSettingsActivity(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}
