package com.leqienglish.playandrecord;

import android.media.MediaPlayer;

import com.leqienglish.data.content.MyRecitingContentDataCache;
import com.leqienglish.entity.PlayEntity;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import java.io.IOException;

import xyz.tobebetter.entity.english.play.AudioPlayPoint;

/**
 * Created by zhuqing on 2018/4/24.
 */

public class PlayMediaPlayerThread extends Thread {
    private LOGGER logger = new LOGGER(PlayMediaPlayerThread.class);
    private AudioPlayPoint playEntity;
    private String resource;
    private LQHandler.Consumer playComplete;

    public AudioPlayPoint getPlayEntity() {
        return playEntity;
    }

    public void setPlayEntity(AudioPlayPoint playEntity) {
        this.playEntity = playEntity;
    }

    private MediaPlayer mediaPlayer;

    public PlayMediaPlayerThread(String resource) {
        //  this.setPlayComplete(complete);
        this.resource = resource;

    }

    public void run() {
        if (this.getPlayEntity() == null) {
            return;
        }
        try {
            this.initMediaPlayer();
            this.mediaPlayer.seekTo(Integer.valueOf(this.getPlayEntity().getStartTime() + ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMediaPlayer() throws IOException {
        if (this.mediaPlayer != null) {
            return;
        }
        logger.d("initMediaPlayer source="+resource);
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setDataSource(resource);
        this.mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(MediaPlayer mp) {

                mediaPlayer.start();
                new PlayThread(mediaPlayer, getPlayComplete()).start();

            }
        });

        this.mediaPlayer.prepare();


    }

    public LQHandler.Consumer getPlayComplete() {
        return playComplete;
    }

    public void setPlayComplete(LQHandler.Consumer playComplete) {
        this.playComplete = playComplete;
    }

    public class PlayThread extends Thread {
        private MediaPlayer mediaPlayer;
        private LQHandler.Consumer playComplete;

        public PlayThread(MediaPlayer mediaPlayer, LQHandler.Consumer playComplete) {
            this.mediaPlayer = mediaPlayer;
            this.playComplete = playComplete;
        }

        public void run() {
            int end = Integer.valueOf(getPlayEntity().getEndTime() + "");
            while (true) {
                if (!mediaPlayer.isPlaying()) {
                    if (playComplete != null)
                        playComplete.accept(getPlayEntity());
                    break;
                }
                if (mediaPlayer.getCurrentPosition() > end) {
                    mediaPlayer.pause();
                    if (playComplete != null)
                        playComplete.accept(getPlayEntity());
                    break;

                }
            }

        }
    }
}
