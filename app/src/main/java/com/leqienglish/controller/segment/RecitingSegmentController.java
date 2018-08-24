package com.leqienglish.controller.segment;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.playandrecord.LQMediaPlayer;
import com.leqienglish.playandrecord.PlayAudioThread;
import com.leqienglish.playandrecord.RecordAudioThread;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.util.time.CountDownUtil;

import java.util.List;


import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;

public class RecitingSegmentController extends ControllerAbstract {
    private Button startButton;

    private  RecordAudioThread recordAudioThread;

    private TextView countDownTextView;

    private Segment segment;



    private int playStatus = 0;//0:没有播放，1录英；2。播放

    private AudioPlayPoint audioPlayPoint;
    private  CountDownUtil countDownUtil;


    public RecitingSegmentController(View view, Segment segment) {
        super(view);
        this.segment = segment;
    }

    @Override
    public void init() {

        this.startButton = (Button) this.findViewById(R.id.reciting_borad_start);
        this.countDownTextView = (TextView) this.findViewById(R.id.reciting_borad_count_down);

        this.initListener();
        reload();
    }

    private void initListener() {
        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playStatus == 2){
                    
                }

                if(playStatus == 1){

                    if(recordAudioThread!=null){
                        recordAudioThread.destroy();
                        playRecord(recordAudioThread.getByteBufferList());
                    }else{
                        cancelCountDown();
                        playStatus =0;
                    }

                    return;
                }else if( playStatus == 0){
                    playStatus = 1;

                    playDing();
                }
            }
        });
    }


    private void cancelCountDown(){
        if(countDownUtil == null||audioPlayPoint == null){
            return;
        }
        playStatus = 0;
        countDownUtil.cancel();
        this.countDownTextView.setText(StringUtil.toMinsAndSeconds(getDuring()));
        startButton.setBackgroundResource(R.drawable.background_green_selector_blue_50dp);
        startButton.setText(R.string.start);
    }

    private void playDing(){
        recordAudioThread = null;
        LQMediaPlayer lqMediaPlayer = new LQMediaPlayer();
        lqMediaPlayer.play(MediaPlayer.create(this.getView().getContext(),R.raw.ding),1,new LQHandler.Consumer(){
            @Override
            public void accept(Object o) {

                startCountDown();
                startRecord();
            }
        });
    }

    private void playRecord(List<short[]> shorts){
        startButton.setBackgroundResource(R.drawable.background_red_selector_blue_50dp);
        startButton.setText(R.string.cancel);
        playStatus = 2;
        this.countDownUtil.cancel();
        PlayAudioThread playAudioThread = new PlayAudioThread();
        playAudioThread.playRecord(shorts, new LQHandler.Consumer() {
            @Override
            public void accept(Object o) {
                cancelCountDown();
            }
        });


    }

    private void startRecord(){
        recordAudioThread = new RecordAudioThread(this.getDuring(),new LQHandler.Consumer<List<short[]>>(){
           @Override
           public void accept(List<short[]> shorts) {
               playRecord(shorts);
           }
       });


        recordAudioThread.record();
    }


    /**
     * 比真实长度多5miao
     * @return
     */
    private long getDuring(){
        if(audioPlayPoint == null){
            return 0L;
        }

        return audioPlayPoint.getEndTime()-audioPlayPoint.getStartTime()+5000;
    }


    private void startCountDown() {
        if(audioPlayPoint == null){
            return;
        }
        startButton.setBackgroundResource(R.drawable.background_red_selector_blue_50dp);
        startButton.setText(R.string.play);
        countDownUtil = CountDownUtil.createTime(getDuring());
        countDownUtil.runCount(new CoundDownHandler());
    }

    @Override
    public void reload() {

        try {
            List<AudioPlayPoint> audioPlayPoints = AudioPlayPoint.toAudioPlays(segment.getContent());
            if (audioPlayPoints == null || audioPlayPoints.isEmpty()) {
                return;
            }

            this.audioPlayPoint = new AudioPlayPoint();
            audioPlayPoint.setStartTime(audioPlayPoints.get(0).getStartTime());
            audioPlayPoint.setEndTime(audioPlayPoints.get(audioPlayPoints.size() - 1).getEndTime());

            this.countDownTextView.setText(StringUtil.toMinsAndSeconds(getDuring()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void destory() {

    }


    private class CoundDownHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CountDownUtil.COUNT_DOWN:
                    countDownTextView.setText(msg.getData().getString(BundleUtil.DATA));
                    break;
                case CountDownUtil.OVER:

                    countDownTextView.setText(msg.getData().getString(BundleUtil.DATA));

                    break;
            }
        }
    }
}
