package com.leqienglish.playandrecord;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class LQMediaPlayer {

    private static LOGGER logger = new LOGGER(LQMediaPlayer.class);
    private String filePath;
    private MediaPlayer mediaPlayer;
    private LQHandler.Consumer completeConsumer;
    private int times;

    private int PLAY_TIMES = 3;


    private boolean isPlay = true;


    public LQMediaPlayer() {

    }

    public void pause() {
        isPlay = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();

        }
    }

    private void initListner() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //mediaPlayer.
                if (!isPlay) {
                    return;
                }
                times++;
                if (PLAY_TIMES == times) {
                    mediaPlayer.release();
                    if (completeConsumer != null) {
                        completeConsumer.accept(null);
                    }
                } else {
                    //播放间隔2秒
                    Observable.just(mediaPlayer).delay(2L, TimeUnit.SECONDS)
                            .subscribe(new Consumer<MediaPlayer>() {
                                @Override
                                public void accept(MediaPlayer mediaPlayer) throws Exception {
                                    mediaPlayer.start();
                                }
                            });

                }

            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (!isPlay) {
                    return;
                }
                mp.start();
            }
        });
    }

    /**
     * 播放
     *
     * @param mp               文件路径
     * @param playTimes        播放次数
     * @param completeConsumer
     */
    public void play(MediaPlayer mp, int playTimes, LQHandler.Consumer completeConsumer) {
        PLAY_TIMES = playTimes;
        this.times = 0;
        isPlay = true;
        this.completeConsumer = completeConsumer;
        this.mediaPlayer = mp;
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                initListner();
                mediaPlayer.start();
                return null;
            }
        };

        asyncTask.execute();


    }

    /**
     * 播放
     *
     * @param filePath            文件路径
     * @param playTimes        播放次数
     * @param completeConsumer
     */
    public void play(String filePath, int playTimes, LQHandler.Consumer completeConsumer) {
        PLAY_TIMES = playTimes;
        this.times = 0;
        isPlay = true;
        this.completeConsumer = completeConsumer;

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mediaPlayer = new MediaPlayer();
                initListner();
                File file = new File(filePath);

                logger.d("filePath:" + filePath + "\texit=" + file.length());
                try {

                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        asyncTask.execute();


    }
}
