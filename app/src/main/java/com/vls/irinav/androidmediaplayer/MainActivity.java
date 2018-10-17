package com.vls.irinav.androidmediaplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private SeekBar mSeekbar;
    private ImageView artistImage;
    private TextView artistName, songName, timeLeft, timeRight;
    private Button prevButton, playButton, nextButton;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();

        mSeekbar.setMax(mediaPlayer.getDuration());
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if ( fromUser ) {
                    mediaPlayer.seekTo(progress);
                    updateSeekbar();
                }

                updateSeekbar();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setupUI() {

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create( getApplicationContext(), R.raw.vivaldi_sonata_eminor);

        artistImage = (ImageView) findViewById(R.id.artistImage);
        artistName = (TextView) findViewById(R.id.artistNameId);
        songName = (TextView) findViewById(R.id.songNameId);
        timeLeft = (TextView) findViewById(R.id.leftTime);
        timeRight = (TextView) findViewById(R.id.rightTime);
        mSeekbar = (SeekBar) findViewById(R.id.mSeekBar);
        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        playButton = (Button) findViewById(R.id.playButton);

        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        playButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevButton:
                backMusic();
                break;

            case R.id.playButton:
                if (!mediaPlayer.isPlaying()) {
                    startMusic();
                } else {
                    pauseMusic();
                }
                break;

            case R.id.nextButton:
                nextMusic();
                break;
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
           playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);

        }
    }

    public void updateSeekbar() {
        if (mediaPlayer != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

            int currentPos = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();

            timeLeft.setText(dateFormat.format( new Date(currentPos) ));
            timeRight.setText(dateFormat.format(new Date(duration)));
        }
    }

    public void updateThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {

                    while ( mediaPlayer != null && mediaPlayer.isPlaying() ) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();

                                mSeekbar.setMax(newMax);
                                mSeekbar.setProgress(newPosition);

                                // update the text
                                timeLeft.setText(String.valueOf( new SimpleDateFormat("mm:ss").format( new Date( mediaPlayer.getCurrentPosition()))));
                                timeRight.setText( String.valueOf(
                                        new SimpleDateFormat("mm:ss").format( new Date (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()) )
                                ));
                            }
                        });
                    }

                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void backMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
            pauseMusic();
        }
    }

    public void nextMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
            pauseMusic();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}


