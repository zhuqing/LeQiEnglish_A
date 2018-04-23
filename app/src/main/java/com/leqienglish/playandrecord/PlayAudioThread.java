package com.leqienglish.playandrecord;

/**
 * Created by zhuqing on 2018/4/24.
 */
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

import com.leqienglish.util.AppType;


import java.util.List;

/**
 * 播放录音
 */
public class PlayAudioThread extends Thread {
    private List<short[]> list;
    private Handler handler;

    public PlayAudioThread(List<short[]> list, Handler handler) {
        this.list = list;
        this.handler = handler;
    }

    public void run() {


        try {
            int bufferSize = AudioTrack.getMinBufferSize(AppType.frequence,
                    AudioFormat.CHANNEL_OUT_MONO, AppType.audioEncoding);
            Log.d(this.getName(), "====start play=====bufferSize=" + bufferSize);
            // 实例AudioTrack
            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    AppType.frequence, AudioFormat.CHANNEL_OUT_MONO, AppType.audioEncoding,
                    bufferSize, AudioTrack.MODE_STREAM);
            // 开始播放
            track.setStereoVolume(0.7f, 0.7f);
            track.play();

            for( int i = 0;  i < this.list.size() ; i++){

                short[] buffer = this.list.get(i);
                Log.d(this.getName(), "====start play=====bufferSize=" + buffer.length);
                track.write(buffer, 0, buffer.length);
            }
            track.stop();
            track.release();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.list.clear();
        this.handler.sendEmptyMessage(AppType.PLAY_RECORD_OVER);
    }
}