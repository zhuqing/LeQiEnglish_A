package com.leqienglish.playandrecord;

import android.media.MediaPlayer;

import com.leqienglish.entity.PlayEntity;
import com.leqienglish.util.LQHandler;

import java.io.IOException;

/**
 * Created by zhuqing on 2018/4/24.
 */

public class PlayMediaPlayerThread extends Thread{
    private PlayEntity playEntity;
    private String resource;
    private LQHandler.Consumer playComplete;

    public PlayEntity getPlayEntity() {
        return playEntity;
    }

    public void setPlayEntity(PlayEntity playEntity) {
        this.playEntity = playEntity;
    }

    private MediaPlayer mediaPlayer;

    public PlayMediaPlayerThread(String resource , LQHandler.Consumer complete)  {
        this.playComplete = complete;
        this.resource = resource;

    }

    public void run(){
        if(this.getPlayEntity() == null || this.playComplete == null){
            return;
        }
        try {
            this.initMediaPlayer();
            this.mediaPlayer.seekTo(Integer.valueOf( this.getPlayEntity().getStart()/1000+""));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void  initMediaPlayer() throws IOException {
        if(this.mediaPlayer !=null){
            return;
        }
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setDataSource(resource);
        this.mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener(){

            @Override
            public void onSeekComplete(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mediaPlayer.start();
                int end = Integer.valueOf(getPlayEntity().getEnd()/1000+"");
                while (true){
                    if(!mediaPlayer.isPlaying()){
                        playComplete.applay(getPlayEntity());
                        break;
                    }
                    if(mediaPlayer.getCurrentPosition()>end){
                        playComplete.applay(getPlayEntity());
                        break;

                    }
                }


            }});
        this.mediaPlayer.prepare();
        this.mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
    }
}
