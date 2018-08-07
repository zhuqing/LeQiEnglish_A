package com.leqienglish.playandrecord;

/**
 * Created by zhuqing on 2018/4/24.
 */

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.leqienglish.sf.databasetask.DataBaseTask;
import com.leqienglish.util.AppType;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;


import java.util.List;

/**
 * 播放录音
 */
public class PlayAudioThread extends DataBaseTask<String> {
    private LOGGER logger = new LOGGER(RecordAudioThread.class);
    private List<short[]> list;
    private LQHandler.Consumer consumer;

    private static  PlayAudioThread playAudioThread;




    public void playRecord(List<short[]> list, LQHandler.Consumer handler){
        if(this.isRunning()){
            this.destroy();
        }
        this.list = list;
        this.setConsumer(handler);
        this.execute();

    }

    public PlayAudioThread() {


    }



    @Override
    protected String run(Object... objects) {


        try {
            int bufferSize = AudioTrack.getMinBufferSize(AppType.frequence,
                    AudioFormat.CHANNEL_OUT_MONO, AppType.audioEncoding);
            // 实例AudioTrack
            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    AppType.frequence, AudioFormat.CHANNEL_OUT_MONO, AppType.audioEncoding,
                    bufferSize, AudioTrack.MODE_STREAM);
            // 开始播放
            track.setStereoVolume(0.7f, 0.7f);
            track.play();

            for (int i = 0; i < this.list.size(); i++) {

                short[] buffer = this.list.get(i);
                track.write(buffer, 0, buffer.length);
                if(this.cancel){
                    break;
                }
            }
            track.stop();
            track.release();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.list.clear();
        return "";
    }
}