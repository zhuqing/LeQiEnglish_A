package com.leqienglish.view.play;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.leqienglish.R;
import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.service.MusicService;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.file.FileUtil;
import com.leqienglish.util.toast.ToastUtil;

import java.io.IOException;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class PlayBarView extends LinearLayout {
    private static final LOGGER Log = new LOGGER(PlayBarView.class);

    public Runnable getPlayFinish() {
        return playFinish;
    }

    public void setPlayFinish(Runnable playFinish) {
        this.playFinish = playFinish;
    }

    public PlayBarI getPlayBarI() {
        return playBarI;
    }

    public void setPlayBarI(PlayBarI playBarI) {
        this.playBarI = playBarI;
    }

    private enum PlayStatus {
        PLAY,
        STOP
    }

    private MyConnection conn;
    private MusicService.MusicBinder musicControl;


    private Button playButton;
    private SeekBar seekBar;
    private Button previousButton;
    private Button nextButton;

    private PlayStatus currentStatus = PlayStatus.STOP;

    private Boolean useInterface = true;

    private Runnable playFinish;


    private Integer playIndex = 0;
    private Integer max;

    private List<SegmentPlayEntity> segmentPlayEntities;

    private static final int UPDATE_PROGRESS = 0;

    private PlayBarI playBarI;


    //使用handler定时更新进度条
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    updateProgress();
                    break;
            }
        }
    };


    public PlayBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.play_bar_view, this);
    }

    /**
     * 初始化播放器组件
     */
    public void init() {

        this.playButton = this.findViewById(R.id.play_bar_view_play);
        this.seekBar = this.findViewById(R.id.play_bar_view_seekbar);
        this.previousButton = this.findViewById(R.id.play_bar_view_previous);
        this.nextButton = this.findViewById(R.id.play_bar_view_next);

        Intent intent3 = new Intent(this.getContext(), MusicService.class);
        conn = new MyConnection();
        //使用混合的方法开启服务，
        getContext().startService(intent3);
        getContext().bindService(intent3, conn, BIND_AUTO_CREATE);






        this.initButtonEventHandler();
    }


    private void initButtonEventHandler(){
        this.previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getPlayBarI()!=null){
                    getPlayBarI().playPrevious();
                }
            }
        });

        this.nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getPlayBarI()!=null){
                    getPlayBarI().playNext();
                }
            }
        });

        this.playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (musicControl == null) {
                    return;
                }

                switch (currentStatus) {
                    case PLAY:
                        stop();
                        break;
                    case STOP:
                        play();
                        break;

                    default:
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!useInterface) {
                    return;
                }

                resetPlayedSegmentPlayEntity(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void play() {
        if (musicControl == null || musicControl.isPlaying()) {
            return;
        }
        if(this.segmentPlayEntities == null){
            ToastUtil.showShort(getContext(),"先选中要播放的文件");
            return;
        }
        currentStatus = PlayStatus.PLAY;
        if (playIndex == 0) {
            playNext();

        } else {
            musicControl.play();
        }
        this.playButton.setBackgroundResource(R.drawable.pause64);
        startUpdateProgress();
    }

    public void stop() {
        if (musicControl == null || !musicControl.isPlaying()) {
            return;
        }
        currentStatus = PlayStatus.STOP;
        musicControl.pause();
        this.playButton.setBackgroundResource(R.drawable.play64);
    }

    public void init(int startProgress, List<SegmentPlayEntity> segmentPlayEntities) {

        if(musicControl!=null){
            musicControl.pause();
        }
        this.playIndex = 0;
        this.max = segmentPlayEntities.get(segmentPlayEntities.size() - 1).getEndTime();
        this.segmentPlayEntities = segmentPlayEntities;
        useInterface = false;
        this.seekBar.setMax(max);
        this.seekBar.setProgress(startProgress);
        useInterface = true;

        play();


    }

    public void playNext() {
        if (musicControl == null) {
            return;
        }

        try {
            play(segmentPlayEntities.get(playIndex), new Runnable() {
                @Override
                public void run() {
                    musicControl.play();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private class MyConnection implements ServiceConnection {

        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicBinder) service;
            musicControl.setPlayCommplete(new Runnable() {
                @Override
                public void run() {

                    if (playIndex == segmentPlayEntities.size()) {
                        if (playFinish != null) {
                            playFinish.run();
                        }
                        return;
                    }
                    playNext();
                    musicControl.play();


                }
            });
            //更新按钮的文字
            //updatePlay();
            //设置进度条的最大值


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    public void onDestroy() {

        if(musicControl!=null){
            musicControl.pause();
        }
        //退出应用后与service解除绑定
        getContext().unbindService(conn);
        onStop();
    }


    public void onStop() {

        //停止更新进度条的进度
        handler.removeCallbacksAndMessages(null);
    }

    //更新进度条
    private void updateProgress() {
        useInterface = false;
        int postion = musicControl.getCurrenPostion();
        SegmentPlayEntity segmentPlayEntity = this.segmentPlayEntities.get(playIndex-1);
        seekBar.setProgress(segmentPlayEntity.getStartTime() + postion);
        useInterface = true;
        //使用Handler每500毫秒更新一次进度条,r如果播放已经暂停，不在继续更新
        if (musicControl != null && musicControl.isPlaying()) {
            handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
        }

    }


    private void resetPlayedSegmentPlayEntity(int position) {

        for (int index = 0; index < this.segmentPlayEntities.size(); index++) {
            SegmentPlayEntity segmentPlayEntity = segmentPlayEntities.get(index);
            if (segmentPlayEntity.getStartTime() <= position && segmentPlayEntity.getEndTime() >= position) {

                playIndex = index;
                break;
            }
        }



        final SegmentPlayEntity currentPlayEntity = segmentPlayEntities.get(playIndex);

        final int newPosition = position - currentPlayEntity.getStartTime();
        musicControl.pause();
        try {
            play(currentPlayEntity, new Runnable() {
                @Override
                public void run() {
                    musicControl.seekTo(newPosition);
                    musicControl.play();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void play(SegmentPlayEntity currentPlayEntity,Runnable perpared) throws IOException {

        if(perpared == null){
            return;
        }

        if (currentPlayEntity.getFilePath().contains(FileUtil.APP_NAME)) {
            musicControl.setSource(currentPlayEntity.getFilePath());
            playIndex += 1;
            perpared.run();

        } else {
            LoadFile.loadFile(currentPlayEntity.getFilePath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    currentPlayEntity.setFilePath(s);
                    try {
                        musicControl.setSource(currentPlayEntity.getFilePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playIndex += 1;
                   perpared.run();
                }
            });
        }

    }


    public void startUpdateProgress() {
        if (musicControl != null) {
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        }
    }



    public interface  PlayBarI{
        public void play();
        public void playNext();
        public void playPrevious();
        public void stop();
    }


}
