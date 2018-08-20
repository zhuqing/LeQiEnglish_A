package com.leqienglish.playandrecord;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;

import com.leqienglish.controller.WordListViewController;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import java.io.IOException;


/**
 * 播放音频
 */
public class PlayAudio {

    private LOGGER logger = new LOGGER(PlayAudio.class);

    SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
    private String path;

    private LQHandler.Consumer playComplete;

    public PlayAudio() {

    }

    public void play( int id,LQHandler.Consumer playComplete) {
      soundPool.play(id,100,100,1,0,1);
    }

    public int load(String path ) {


       return soundPool.load(path,10);

    }

    public void release() {
        soundPool.release();
    }
}
