package com.leqienglish.playandrecord;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.RxThreadFactory;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;

public class PlayMediaPlayer {

    private static LOGGER logger = new LOGGER(PlayMediaPlayerThread.class);

    private static PlayMediaPlayer playMediaPlayer;

    private AudioPlayPoint audioPlayPoint;

    private String resource;

    private boolean isplaying = false;

    private MediaPlayer mediaPlayer;

    private LQHandler.Consumer playComplete;

    private PlayMediaPlayer() {

    }


    public static PlayMediaPlayer getInstance() {
        if (playMediaPlayer == null) {
            playMediaPlayer = new PlayMediaPlayer();
        }

        return playMediaPlayer;
    }

    public void destory() {
        mediaPlayer.pause();
    }

    public void play(AudioPlayPoint audioPlayPoint, LQHandler.Consumer playComplete) {
        this.setAudioPlayPoint(audioPlayPoint);
        this.setPlayComplete(playComplete);
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                logger.d("play" + audioPlayPoint.getStartTime());
                isplaying = true;
                mediaPlayer.seekTo((int) audioPlayPoint.getStartTime());
                return null;
            }
        };
        task.execute();

    }

    private void initMediaPlayer() throws IOException {
        if (this.mediaPlayer != null) {
            return;
        }
        logger.d("initMediaPlayer source=" + resource);
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setDataSource(resource);
        this.mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(MediaPlayer mp) {

                mediaPlayer.start();
                new PlayMediaPlayer.PlayThread(mediaPlayer, getPlayComplete()).start();

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

    public AudioPlayPoint getAudioPlayPoint() {
        return audioPlayPoint;
    }

    public void setAudioPlayPoint(AudioPlayPoint audioPlayPoint) {
        this.audioPlayPoint = audioPlayPoint;
    }

    public boolean isIsplaying() {
        return isplaying;
    }

    /**
     * 检测播放时间，到时间后或者结束后 调研 playComplete Consumer
     */
    public class PlayThread extends Thread {
        private MediaPlayer mediaPlayer;
        private LQHandler.Consumer playComplete;

        public PlayThread(MediaPlayer mediaPlayer, LQHandler.Consumer playComplete) {
            this.mediaPlayer = mediaPlayer;
            this.playComplete = playComplete;
        }

        public void run() {
            int end = Integer.valueOf(getAudioPlayPoint().getEndTime() + "");
            while (true) {
                if (!mediaPlayer.isPlaying()) {
                    commit();
                    break;
                }
                if (mediaPlayer.getCurrentPosition() > end) {
                    mediaPlayer.pause();
                    commit();
                    break;

                }
            }

        }
    }

    private void commit() {
        if (playComplete == null) {
            isplaying = false;
            return;
        }
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {

                playComplete.accept(getAudioPlayPoint());
                isplaying = false;
            }
        });
    }


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) throws IOException {
        this.resource = resource;
        initMediaPlayer();
    }


}
