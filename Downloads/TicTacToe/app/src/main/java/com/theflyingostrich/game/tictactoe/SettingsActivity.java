package com.theflyingostrich.game.tictactoe;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private Switch soundSwitch, musicSwitch;


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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_layout);

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);

        soundSwitch = (Switch)findViewById(R.id.soundSwitch);
        musicSwitch = (Switch)findViewById(R.id.musicSwitch);
        soundSwitch.setOnCheckedChangeListener(this);
        musicSwitch.setOnCheckedChangeListener(this);

        soundSwitch.setChecked(GameController.getInstance(prefs).getSoundState());
        musicSwitch.setChecked(GameController.getInstance(prefs).getMusicState());


        if(GameController.getInstance(prefs).getSoundState() == false){
            soundSwitch.setText("Sound: OFF  ");
        }
        else if(GameController.getInstance(prefs).getSoundState() == true){
            soundSwitch.setText("Sound ON   ");
        }

        if(GameController.getInstance(prefs).getMusicState() == false){
            musicSwitch.setText("Music: OFF  ");
        }
        else if(GameController.getInstance(prefs).getSoundState() == true){
            musicSwitch.setText("Music: ON   ");
        }




    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

        SharedPreferences prefs = getSharedPreferences(GameController.PREFS, MODE_PRIVATE);

        if (compoundButton == soundSwitch) {
            if (checked) {
                soundSwitch.setText("Sound: ON   ");
                GameController.getInstance(prefs).setSoundState(true);
                SharedPreferences.Editor editor = getSharedPreferences(GameController.PREFS, MODE_PRIVATE).edit();
                editor.putBoolean("soundPref", true);
                editor.apply();
            } else {
                soundSwitch.setText("Sound: OFF  ");
                GameController.getInstance(prefs).setSoundState(false);
                SharedPreferences.Editor editor = getSharedPreferences(GameController.PREFS, MODE_PRIVATE).edit();
                editor.putBoolean("soundPref", false);
                editor.apply();
            }
        }

        else if(compoundButton == musicSwitch){
            if (checked) {
                musicSwitch.setText("Music: ON   ");
                GameController.getInstance(prefs).setMusicState(true);
                SharedPreferences.Editor editor = getSharedPreferences(GameController.PREFS, MODE_PRIVATE).edit();
                editor.putBoolean("musicPref", true);
                editor.apply();
                MediaPlayer musicMediaPlayer = MediaPlayer.create(this, R.raw.background_music);
                GameController.getInstance(prefs).startMusicFromLastPosition(musicMediaPlayer);
            } else {
                musicSwitch.setText("Music: OFF  ");
                GameController.getInstance(prefs).setMusicState(false);
                SharedPreferences.Editor editor = getSharedPreferences(GameController.PREFS, MODE_PRIVATE).edit();
                editor.putBoolean("musicPref", false);
                editor.apply();
                GameController.getInstance(prefs).stopMusic();
            }
        }

            MediaPlayer mediaPlayer =  MediaPlayer.create(this, R.raw.o_sound);
            mediaPlayer = MediaPlayer.create(this, R.raw.x_sound);
            GameController.getInstance(prefs).playSound(mediaPlayer);

    }


}
