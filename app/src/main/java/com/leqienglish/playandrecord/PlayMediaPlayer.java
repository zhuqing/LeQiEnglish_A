package com.leqienglish.playandrecord;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;

public class PlayMediaPlayer {

    private static LOGGER logger = new LOGGER(PlayMediaPlayer.class);

    public final static String COMPLETE = "COMPLETE";
    public final static String PAUSE_CHAGNE = "PAUSE_CHAGNE";
    public final static String PAUSE_NOT_CHAGNE = "PAUSE_NOT_CHAGNE";
    private static PlayMediaPlayer playMediaPlayer;

    private AudioPlayPoint audioPlayPoint;

    private String resource;


    private String completeType = COMPLETE;

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

    public void destory(String type) {
        if(!mediaPlayer.isPlaying()){
            return;
        }
        completeType = type;

        mediaPlayer.pause();


    }

    public void play(AudioPlayPoint audioPlayPoint, LQHandler.Consumer playComplete) {
        this.setAudioPlayPoint(audioPlayPoint);
        completeType = PAUSE_NOT_CHAGNE;
        this.setPlayComplete(playComplete);
        if(this.mediaPlayer.isPlaying()){
            completeType = PAUSE_NOT_CHAGNE;
            mediaPlayer.pause();
        }

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
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
                completeType = COMPLETE;

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

    public boolean isplaying() {
        return mediaPlayer.isPlaying();
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

            return;
        }
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {

                playComplete.accept(completeType);

            }
        });
    }


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) throws IOException {
        this.resource = resource;
        mediaPlayer = null;
        initMediaPlayer();
    }


}
