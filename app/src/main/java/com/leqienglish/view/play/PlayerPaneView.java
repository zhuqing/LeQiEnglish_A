package com.leqienglish.view.play;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.leqienglish.R;
import com.leqienglish.playandrecord.PlayAudioThread;
import com.leqienglish.playandrecord.PlayMediaPlayer;
import com.leqienglish.playandrecord.PlayMediaPlayerThread;
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


        this.play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayMediaPlayer.getInstance().isIsplaying()) {
                    PlayMediaPlayer.getInstance().destory();
                    play.setBackgroundResource(R.drawable.leqi_play);
                } else {
                    play.setBackgroundResource(R.drawable.leqi_stop);
                    PlayMediaPlayer.getInstance().play(audioPlayPoint, new LQHandler.Consumer() {
                        @Override
                        public void accept(Object o) {
                            play.setBackgroundResource(R.drawable.leqi_play);
                        }
                    });
                }
            }
        });

        this.record.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playAudioThread!=null && playAudioThread.isRunning()){
                    playAudioThread.destroy();
                }
                PlayMediaPlayer.getInstance().destory();
                record.setBackgroundResource(R.drawable.leqi_recording);
                if (recordAudioThread != null && recordAudioThread.isRunning()) {
                    recordAudioThread.destroy();
                }
                // RecordAudioThread.getInstance().destroy();
                logger.d(audioPlayPoint.getStartTime()+":"+audioPlayPoint.getEndTime());

                recordAudioThread = new RecordAudioThread();
                recordAudioThread.record(audioPlayPoint.getEndTime() - audioPlayPoint.getStartTime()+3000,
                        new LQHandler.Consumer<List<short[]>>() {
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
                        });

            }
        });

        this.playRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    public void play(AudioPlayPoint audioPlayPoint) {
        this.audioPlayPoint = audioPlayPoint;

        this.play.setBackgroundResource(R.drawable.leqi_stop);
        PlayMediaPlayer.getInstance().play(audioPlayPoint, new LQHandler.Consumer() {
            @Override
            public void accept(Object o) {
                play.setBackgroundResource(R.drawable.leqi_play);
            }
        });

    }

    public void destroy() {
        this.play.setBackgroundResource(R.drawable.leqi_play);
        this.playRecord.setBackgroundResource(R.drawable.leqi_play_record);
        this.record.setBackgroundResource(R.drawable.leqi_record);

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

    public void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
        try {
            PlayMediaPlayer.getInstance().setResource(mp3Path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
