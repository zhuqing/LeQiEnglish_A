package com.leqienglish.view.play.playbar;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.view.play.PlayBarDelegate;
import com.leqienglish.view.play.PlayBarViewInterface;
import com.leqienglish.view.play.PlayStatus;

/**
 * 播放器，只有播放的控制
 */
public class PlayBarView extends LinearLayout implements PlayBarViewInterface {

    private static final LOGGER Log = new LOGGER(PlayBarView.class);



    /**
     * 播放按钮
     */
    private Button playButton;
    /**
     * 进度条
     */
    protected SeekBar seekBar;

    /**
     * 上一个按钮
     */
    private Button previousButton;
    /**
     * 下一个按钮
     */
    private Button nextButton;
    /**
     *
     */
    private TextView currentTimeTextView;
    /**
     * 总播放时间的文本
     */
    private TextView totalTimeTextView;

    /**
     * 当前的播放状态
     */
    private PlayStatus currentStatus = PlayStatus.STOP;

    /**
     * 是否是用户触发的交互
     */
    private Boolean useInterface = true;


    private Integer max;




    private static final int UPDATE_PROGRESS = 0;

    private PlayBarDelegate playBarI;





    public void setMax(int max){
        seekBar.setMax(max);
        this.totalTimeTextView.setText(StringUtil.toMinsAndSeconds(max));
    }

    public PlayBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.play_bar_view, this);
        init();
    }

    /**
     * 初始化播放器组件
     */
    public void init() {

        this.playButton = this.findViewById(R.id.play_bar_view_play);
        this.seekBar = this.findViewById(R.id.play_bar_view_seekbar);
        this.previousButton = this.findViewById(R.id.play_bar_view_previous);
        this.nextButton = this.findViewById(R.id.play_bar_view_next);
        this.currentTimeTextView = this.findViewById(R.id.play_bar_view_current_time);
        this.totalTimeTextView = this.findViewById(R.id.play_bar_view_total_time);


        this.initButtonEventHandler();
    }


    private void initButtonEventHandler() {
        this.previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPlayBarI() != null) {
                    getPlayBarI().playPrevious();
                }
            }
        });

        this.nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPlayBarI() != null) {
                    getPlayBarI().playNext();
                }
            }
        });

        this.playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


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


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!useInterface) {
                    return;
                }


                if(playBarI != null){
                    playBarI.updateProgress(seekBar.getProgress());
                }
            }
        });
    }

    /**
     * 开始播放
     */
    public void play() {
        currentStatus = PlayStatus.PLAY;
        this.playButton.setBackgroundResource(R.drawable.pause_new);
        if(this.playBarI != null){
            this.playBarI.play();
        }
    }




    /**
     * 停止播放
     */
    public void stop() {

        currentStatus = PlayStatus.STOP;

        this.playButton.setBackgroundResource(R.drawable.play_new);
        if(this.playBarI != null){
            this.playBarI.stop();
        }
    }


    public void init(int startProgress,int max) {



        this.max = max;

        useInterface = false;
        this.setMax(max);
        this.seekBar.setProgress(startProgress);
        useInterface = true;
        this.currentTimeTextView.setText(StringUtil.toMinsAndSeconds(startProgress));


        play();


    }

    @Override
    public void updateProgress(int progress, int max) {
        this.updateProgress(progress);
    }


    /**
     * 更新进度条的值
     * @param progress
     */
    public void updateProgress(int progress){
        if(this.seekBar == null){
            return;
        }

        useInterface = false;
        this.seekBar.setProgress(progress);
        this.currentTimeTextView.setText(StringUtil.toMinsAndSeconds(progress));
        useInterface = true;
    }



    public PlayBarDelegate getPlayBarI() {
        return playBarI;
    }

    public void setPlayBarDelegate(PlayBarDelegate playBarI) {
        this.playBarI = playBarI;
    }

}
