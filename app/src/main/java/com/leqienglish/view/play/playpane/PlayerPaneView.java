package com.leqienglish.view.play.playpane;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.leqienglish.R;
import com.leqienglish.playandrecord.PlayAudioThread;
import com.leqienglish.playandrecord.PlayMediaPlayer;
import com.leqienglish.playandrecord.RecordAudioThread;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import java.io.IOException;
import java.util.List;

import xyz.tobebetter.entity.english.play.AudioPlayPoint;

public class PlayerPaneView extends RelativeLayout {
    private static LOGGER logger = new LOGGER(PlayerPaneView.class);
    private Button play;
    private Button record;
    private Button playRecord;

  //  private TextView tip ;

    private String mp3Path;

    private AudioPlayPoint audioPlayPoint;

    private RecordAudioThread recordAudioThread;
    private PlayAudioThread playAudioThread;


    public PlayerPaneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.play_record_bar, this);
        init();
    }

    private void init() {
        this.play = this.findViewById(R.id.play_record_bar_play);
        this.record = this.findViewById(R.id.play_record_bar_record);
        this.playRecord = this.findViewById(R.id.play_record_bar_play_record);
       // this.tip= this.findViewById(R.id.play_record_bar_tip);


        this.play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRecordPlay();
                closeRecord();

                if (PlayMediaPlayer.getInstance().isplaying()) {
                    PlayMediaPlayer.getInstance().destory(PlayMediaPlayer.PAUSE_NOT_CHAGNE);
                    play.setBackgroundResource(R.drawable.leqi_play);
                } else {
                    play.setBackgroundResource(R.drawable.leqi_stop);
                    PlayMediaPlayer.getInstance().play(audioPlayPoint, changePlayButtonBackground());
                }
            }
        });

        this.record.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recordAudioThread!=null && recordAudioThread.isRunning()){
                    closeRecord();
                    createPlayRecordConsumer().accept(recordAudioThread.getByteBufferList());
                    return;
                }
                closeRecordPlay();
                closeRecord();
                closeAudioPlay(PlayMediaPlayer.PAUSE_CHAGNE);
                record.setBackgroundResource(R.drawable.leqi_recording);

                logger.d(audioPlayPoint.getStartTime() + ":" + audioPlayPoint.getEndTime());

                long duration = audioPlayPoint.getEndTime() - audioPlayPoint.getStartTime() + 3000;

                recordAudioThread = new RecordAudioThread(duration,createPlayRecordConsumer() );
                recordAudioThread.record();
               // tip.setText("点击按钮结束录音");

            }
        });

        this.playRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRecordPlay();
                closeRecord();
                closeAudioPlay(PlayMediaPlayer.PAUSE_CHAGNE);
            }
        });
    }

    private LQHandler.Consumer<List<short[]>> createPlayRecordConsumer(){
        return  new LQHandler.Consumer<List<short[]>>() {
            @Override
            public void accept(List<short[]> shorts) {

                record.setBackgroundResource(R.drawable.leqi_record);
                playRecord.setBackgroundResource(R.drawable.leqi_record_play_playing);

                playAudioThread = new PlayAudioThread();
                playAudioThread.playRecord(shorts, new LQHandler.Consumer() {
                    @Override
                    public void accept(Object o) {
                        playRecord.setBackgroundResource(R.drawable.leqi_play_record);
                    }
                });
            }
        };
    }

    private void closeRecordPlay() {
        if (playAudioThread != null && playAudioThread.isRunning()) {
            playAudioThread.destroy();
        }
        playRecord.setBackgroundResource(R.drawable.leqi_play_record);
    }

    private void closeRecord() {
        if (recordAudioThread != null && recordAudioThread.isRunning()) {
            recordAudioThread.destroy();
        }
       // tip.setText("");
        record.setBackgroundResource(R.drawable.leqi_record);

    }

    private void closeAudioPlay(String type) {
        if (PlayMediaPlayer.getInstance().isplaying()) {
            PlayMediaPlayer.getInstance().destory(type);
        }

        play.setBackgroundResource(R.drawable.leqi_play);

    }

    private LQHandler.Consumer<String> changePlayButtonBackground(){
      return  new LQHandler.Consumer<String>() {
            @Override
            public void accept(String o) {
                if (!PlayMediaPlayer.PAUSE_NOT_CHAGNE.equals(o)) {
                    play.setBackgroundResource(R.drawable.leqi_play);
                }

            }
        };
    }


    public void play(AudioPlayPoint audioPlayPoint) {
        this.audioPlayPoint = audioPlayPoint;

        logger.d("play="+audioPlayPoint.getEnText()+"\nstart:"+audioPlayPoint.getStartTime());



        PlayMediaPlayer.getInstance().play(audioPlayPoint, changePlayButtonBackground());
        play.setBackgroundResource(R.drawable.leqi_stop);

    }

    public void destroy() {

        this.closeRecordPlay();
        this.closeRecord();
        this.closeAudioPlay(PlayMediaPlayer.COMPLETE);


    }

    public void stop(){
        this.closeRecordPlay();
        this.closeRecord();
        this.closeAudioPlay(PlayMediaPlayer.PAUSE_CHAGNE);
    }


    public Button getPlay() {
        return play;
    }

    public void setPlay(Button play) {
        this.play = play;
    }

    public Button getRecord() {
        return record;
    }

    public void setRecord(Button record) {
        this.record = record;
    }

    public Button getPlayRecord() {
        return playRecord;
    }

    public void setPlayRecord(Button playRecord) {
        this.playRecord = playRecord;
    }

    public AudioPlayPoint getAudioPlayPoint() {
        return audioPlayPoint;
    }

    public void setAudioPlayPoint(AudioPlayPoint audioPlayPoint) {
        this.audioPlayPoint = audioPlayPoint;
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public void setMp3Path(String mp3Path) throws IOException {
        this.mp3Path = mp3Path;
        PlayMediaPlayer.getInstance().setResource(mp3Path);

    }
}
