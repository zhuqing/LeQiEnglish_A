package com.leqienglish.controller.segment;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.playandrecord.LQMediaPlayer;
import com.leqienglish.playandrecord.PlayAudioThread;
import com.leqienglish.playandrecord.RecordAudioThread;
import com.leqienglish.pop.actionsheet.ActionSheet;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.SharePlatform;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.util.time.CountDownUtil;

import java.util.List;

import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;

public class RecitingSegmentController extends ControllerAbstract {
    private Button startButton;

    private  RecordAudioThread recordAudioThread;
    private PlayAudioThread playAudioThread;

    private TextView countDownTextView;

    private Segment segment;




    private AudioPlayPoint audioPlayPoint;
    private  CountDownUtil countDownUtil;

    private enum PlayStatusEnum{
        PLAY_RECORD,//播放录音
        RECORD,//录音
        STOP,//停止
        EXECUTEING;//正在执行不处理
    }

    private PlayStatusEnum currentStatus = PlayStatusEnum.STOP;//默认是停止状态


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
                switch (currentStatus){
                    case STOP:
                        currentStatus = PlayStatusEnum.EXECUTEING;
                        playDing();
                        break;
                    case RECORD:
                        currentStatus = PlayStatusEnum.PLAY_RECORD;
                        if(recordAudioThread!=null){
                            recordAudioThread.destroy();
                            playRecord(recordAudioThread.getByteBufferList());
                        }else{

                            currentStatus = PlayStatusEnum.STOP;
                            cancelCountDown();
                        }
                        break;
                    case PLAY_RECORD:
                        currentStatus = PlayStatusEnum.STOP;
                        cancelCountDown();
                        break;
                }



            }
        });
    }


    private void cancelCountDown(){
        if(countDownUtil == null||audioPlayPoint == null){
            return;
        }
        if(playAudioThread != null){
            playAudioThread.destroy();
        }

        if(recordAudioThread!=null){
            recordAudioThread.destroy();
        }

        countDownUtil.cancel();
        this.countDownTextView.setText(StringUtil.toMinsAndSeconds(getDuring()));
        startButton.setBackgroundResource(R.drawable.background_green_selector_blue_50dp);
        startButton.setText(R.string.start);
    }

    //顶顶两声后开始录音
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
        startButton.setText(R.string.playing);

        this.countDownUtil.cancel();
        playAudioThread = new PlayAudioThread();
        playAudioThread.playRecord(shorts, new LQHandler.Consumer() {
            @Override
            public void accept(Object o) {
                cancelCountDown();
                playFinish();
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
        startButton.setBackgroundResource(R.drawable.background_red_selector_blue_50dp);
        startButton.setText(R.string.recording);

        recordAudioThread.record();
        currentStatus = PlayStatusEnum.RECORD;
    }


    private void playFinish(){
        new ActionSheet.DialogBuilder(getView().getContext())
                .addButton("分享给朋友", (v)->{
                    String userName = UserDataCache.getInstance().getUserName();
                    String userId = UserDataCache.getInstance().getUserId();
                    String hasFinished = "刚刚完成了\""+segment.getTitle()+"\"的背诵";
                    String segmentId = segment.getId();
                    StringBuilder stringBuilder = new StringBuilder(LQService.getHttp());
                    stringBuilder.append("html/share/shareContent.html").append("?userId=").append(userId).append("&segmentId=").append(segmentId);
                    SharePlatform.onShare(getView().getContext(),userName+hasFinished,"我"+hasFinished,LQService.getLogoPath(),stringBuilder.toString());
                })
                .addButton("返回",(v)->{
                   Activity activity = (Activity) getView().getContext();
                   activity.finish();
                })
                .addCloseButton("继续背诵",null)
               .create();
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
        if(this.playAudioThread!=null){
            this.playAudioThread.destroy();
        }
        if(this.recordAudioThread!=null){
            this.recordAudioThread.destroy();
        }
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
