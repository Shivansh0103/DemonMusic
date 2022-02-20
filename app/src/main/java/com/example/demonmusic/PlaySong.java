package com.example.demonmusic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    TextView t;
    ImageView play,prev,next;
    int position;
    ArrayList<File> songs;
    String textContent;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Thread updateSeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        t=findViewById(R.id.songname);
        play=findViewById(R.id.play);
        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        seekBar=findViewById(R.id.seekBar);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        songs=(ArrayList) bundle.getParcelableArrayList("SongList");
        textContent=intent.getStringExtra("CurrentSong");
        t.setText(textContent);
        t.setSelected(true);
        position=intent.getIntExtra("Position",0);
        Uri uri=Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek=new Thread()
        {
            @Override
            public void run() {
                int currentPosition=0;
                try {
                        while (currentPosition<mediaPlayer.getDuration())
                        {
                            currentPosition=mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentPosition);
//                            sleep(800);
                        }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
        play.setOnClickListener(v -> {
            if(mediaPlayer.isPlaying()) {
                play.setImageResource(R.drawable.play);
                mediaPlayer.pause();
            }
            else
            {
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
        });
        prev.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(position!=0)
                position=position-1;
            else
                position=songs.size()-1;
            play.setImageResource(R.drawable.pause);
            Uri uri1 =Uri.parse(songs.get(position).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(), uri1);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            textContent=songs.get(position).getName();
            textContent=textContent.replace(".mp3","");
            t.setText(textContent);
            seekBar.setProgress(0);
        });
        next.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(position!=songs.size()-1)
                position=position+1;
            else
                position=0;
            play.setImageResource(R.drawable.pause);
            Uri uri12 =Uri.parse(songs.get(position).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(), uri12);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            textContent=songs.get(position).getName();
            textContent=textContent.replace(".mp3","");
            t.setText(textContent);
            seekBar.setProgress(0);
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }
}