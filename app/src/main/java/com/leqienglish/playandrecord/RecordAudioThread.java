package com.leqienglish.playandrecord;

/**
 * Created by zhuqing on 2018/4/24.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.leqienglish.data.content.MyRecitingContentDataCache;
import com.leqienglish.sf.databasetask.DataBaseTask;
import com.leqienglish.util.AppType;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import java.util.ArrayList;
import java.util.List;

/***
 * 录音线程
 *
 * @author guona
 *
 */
public class RecordAudioThread extends DataBaseTask<List<short[]>> {
    private LOGGER logger = new LOGGER(RecordAudioThread.class);
    private List<short[]> byteBufferList;
    private long duration;

  //  private LQHandler.Consumer< List<short[]>> consumer;
    private boolean isRecording = false;

    public RecordAudioThread( long duration,
                        LQHandler.Consumer< List<short[]>> handler) {
        super(handler);
        this.duration = duration;
        logger.v("时长："+duration);
       // this.consumer = handler;
        this.byteBufferList= new ArrayList<>();
    }

    public int getValidSampleRates() {
        for (int rate : new int[] {44100, 22050, 11025, 16000, 8000}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
              return bufferSize;
            }
        }

        return -1;
    }

    public void run() {

    }

    @Override
    protected List<short[]> doInBackground(Object... objects) {

        if (isRecording) {
            return byteBufferList;
        }

        isRecording = true;
        try {
            int bufferSize =getValidSampleRates();


            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    AppType.frequence,AudioFormat.CHANNEL_IN_MONO, AppType.audioEncoding,
                    bufferSize);

            // 实例AudioTrack

            short[] buffer = new short[bufferSize];

            long startTime = System.currentTimeMillis();
            record.startRecording();
            // 开始播放
            //track.setStereoVolume(0.7f, 0.7f);
            //track.play();
            while ((System.currentTimeMillis() - startTime) < this.duration) {
                int bufferReadResult = record.read(buffer, 0, buffer.length);
                short[] data = new short[bufferReadResult];
                System.arraycopy(buffer, 0, data, 0, bufferReadResult);
                this.byteBufferList.add(data);

            }

            record.stop();
            record.release();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


       // Log.d(this.getName(), "====end=====");
        isRecording = false;

        return byteBufferList;
    }
}