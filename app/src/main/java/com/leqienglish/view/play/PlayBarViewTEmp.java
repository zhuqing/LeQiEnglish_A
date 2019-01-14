//package com.leqienglish.view.play;
//
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.leqienglish.R;
//import com.leqienglish.entity.SegmentPlayEntity;
//import com.leqienglish.service.MusicService;
//import com.leqienglish.util.LOGGER;
//import com.leqienglish.util.LQHandler;
//import com.leqienglish.util.string.StringUtil;
//import com.leqienglish.util.toast.ToastUtil;
//
//import java.util.List;
//
//import static android.content.Context.BIND_AUTO_CREATE;
//
//public class PlayBarViewTEmp extends LinearLayout {
//    private static final LOGGER Log = new LOGGER(PlayBarViewTEmp.class);
//
//
//
//    public PlayBarI getPlayBarI() {
//        return playBarI;
//    }
//
//    public void setPlayBarI(PlayBarI playBarI) {
//        this.playBarI = playBarI;
//    }
//
//    /**
//     * 正在播放的SegmentPlayEntity索引
//     */
//    public Integer getPlayIndex() {
//
//        return playIndex;
//    }
//
//    public void setPlayIndex(Integer playIndex) {
//        this.playIndex = playIndex;
//    }
//
//    private enum PlayStatus {
//        PLAY,
//        STOP
//    }
//
//    private MyConnection conn;
//    protected MusicService.MusicBinder musicControl;
//
//
//    /**
//     * 播放按钮
//     */
//    private Button playButton;
//    /**
//     * 进度条
//     */
//    protected SeekBar seekBar;
//
//    /**
//     * 上一个按钮
//     */
//    private Button previousButton;
//    /**
//     * 下一个按钮
//     */
//    private Button nextButton;
//    /**
//     *
//     */
//    private TextView currentTimeTextView;
//    /**
//     * 总播放时间的文本
//     */
//    private TextView totalTimeTextView;
//
//    /**
//     * 当前的播放状态
//     */
//    private PlayStatus currentStatus = PlayStatus.STOP;
//
//    /**
//     * 是否是用户触发的交互
//     */
//    private Boolean useInterface = true;
//
//
//    private Integer playIndex = 0;
//    private Integer max;
//
//
//    /**
//     * 播放的数据
//     */
//    private List<SegmentPlayEntity> segmentPlayEntities;
//
//    private static final int UPDATE_PROGRESS = 0;
//
//    private PlayBarI playBarI;
//
//
//    //使用handler定时更新进度条
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case UPDATE_PROGRESS:
//                    updateProgress();
//                    break;
//            }
//        }
//    };
//
//
//    public void setMax(int max){
//        seekBar.setMax(max);
//        this.totalTimeTextView.setText(StringUtil.toMinsAndSeconds(max));
//    }
//
//    public PlayBarViewTEmp(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        LayoutInflater.from(context).inflate(R.layout.play_bar_view, this);
//        init();
//    }
//
//    /**
//     * 初始化播放器组件
//     */
//    public void init() {
//
//        this.playButton = this.findViewById(R.id.play_bar_view_play);
//        this.seekBar = this.findViewById(R.id.play_bar_view_seekbar);
//        this.previousButton = this.findViewById(R.id.play_bar_view_previous);
//        this.nextButton = this.findViewById(R.id.play_bar_view_next);
//        this.currentTimeTextView = this.findViewById(R.id.play_bar_view_current_time);
//        this.totalTimeTextView = this.findViewById(R.id.play_bar_view_total_time);
//
//        conn = new MyConnection();
//        //使用混合的方法开启服务，
//        Intent intent3 = new Intent(this.getContext(), MusicService.class);
//
//        getContext().startService(intent3);
//        this.initButtonEventHandler();
//    }
//
//
//    private void initButtonEventHandler() {
//        this.previousButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getPlayBarI() != null) {
//                    getPlayBarI().playPrevious();
//                }
//            }
//        });
//
//        this.nextButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getPlayBarI() != null) {
//                    getPlayBarI().playNext();
//                }
//            }
//        });
//
//        this.playButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (musicControl == null) {
//                    return;
//                }
//                if(segmentPlayEntities == null || segmentPlayEntities.isEmpty()){
//                    ToastUtil.showShort(getContext(),"没有数据");
//                    return;
//                }
//
//                switch (currentStatus) {
//                    case PLAY:
//                        stop();
//                        break;
//                    case STOP:
//                        play();
//                        break;
//
//                    default:
//                }
//            }
//        });
//
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (!useInterface) {
//                    return;
//                }
//
//                resetPlayedSegmentPlayEntity(progress);
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//    }
//
//    /**
//     * 开始播放
//     */
//    public void play() {
//        if (musicControl == null || musicControl.isPlaying()) {
//            currentStatus = PlayStatus.PLAY;
//            this.playButton.setBackgroundResource(R.drawable.pause_red);
//            startUpdateProgress();
//            return;
//        }
//        if (this.segmentPlayEntities == null) {
//            ToastUtil.showShort(getContext(), "先选中要播放的文件");
//            return;
//        }
//        currentStatus = PlayStatus.PLAY;
//
//        musicControl.play();
//
//        this.playButton.setBackgroundResource(R.drawable.pause_red);
//        startUpdateProgress();
//    }
//
//    public void play(Integer segmentIndex){
//        if(this.musicControl == null){
//            return ;
//        }
//        this.musicControl.play(segmentIndex);
//    }
//
//    public boolean isPlaying(){
//        if(this.musicControl == null){
//            return false;
//        }
//
//        return this.musicControl.isPlaying();
//    }
//
//    /**
//     * 停止播放
//     */
//    public void stop() {
//        if (musicControl == null || !musicControl.isPlaying()) {
//            return;
//        }
//        currentStatus = PlayStatus.STOP;
//        musicControl.pause();
//        this.playButton.setBackgroundResource(R.drawable.play_red);
//    }
//
//    /**
//     * 初始化数据和时间
//     *
//     * @param startProgress
//     * @param segmentPlayEntities
//     */
//    public void init(int startProgress, List<SegmentPlayEntity> segmentPlayEntities) {
//
//        if (musicControl != null) {
//            musicControl.pause();
//        }
//        this.playIndex = 0;
//        this.max = segmentPlayEntities.get(segmentPlayEntities.size() - 1).getEndTime();
//        this.segmentPlayEntities = segmentPlayEntities;
//        useInterface = false;
//        this.setMax(max);
//        this.seekBar.setProgress(startProgress);
//        useInterface = true;
//        this.currentTimeTextView.setText(StringUtil.toMinsAndSeconds(startProgress));
//
//        if(musicControl!=null){
//            musicControl.setSegmentPlayEntityList(segmentPlayEntities);
//        }
//        play();
//
//
//    }
//
//    public void playBySegmentIndex(int index){
//        if(musicControl == null){
//            return;
//        }
//
//        musicControl.play(index);
//    }
//
//
//    /**
//     * 销毁时解除绑定并关闭播放
//     */
//    public void onDestroy() {
//
//        if (musicControl != null) {
//            musicControl.pause();
//        }
//        //退出应用后与service解除绑定
//        unbind();
//        onStopUpdate();
//    }
//
//    /**
//     * 建立与服务端的绑定
//     */
//    public void startBind(){
//
//        Intent intent3 = new Intent(this.getContext(), MusicService.class);
//
//
//        getContext().bindService(intent3, conn, BIND_AUTO_CREATE);
//    }
//
//    /**
//     * 解除与服务端的绑定
//     */
//    public void unbind(){
//        try{
//            getContext().unbindService(conn);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//
//    /**
//     *  //停止更新进度条的进度
//     */
//    public void onStopUpdate() {
//
//        handler.removeCallbacksAndMessages(null);
//    }
//
//    /**
//     * 根据播放时间更新进度条
//     */
//    private void updateProgress() {
//        useInterface = false;
//        int position = musicControl.getCurrenPostion();
//        playIndex = musicControl.getCurrentPlayIndex();
//
//
//
//        updateSeekbar(position);
//
//        useInterface = true;
//
//        this.currentTimeTextView.setText(StringUtil.toMinsAndSeconds(seekBar.getProgress()));
//        if(this.getPlayBarI()!=null){
//            this.getPlayBarI().currentTimeChange(seekBar.getProgress());
//        }
//        //使用Handler每500毫秒更新一次进度条,r如果播放已经暂停，不在继续更新
//        sendUpdateProgressHandler();
//
//    }
//
//    protected void updateSeekbar(int position){
//
//        SegmentPlayEntity segmentPlayEntity = this.musicControl.getCurrentSegmentPlayEntity();
//        if(segmentPlayEntity == null){
//            return;
//        }
//        seekBar.setProgress(segmentPlayEntity.getStartTime() + position);
//    }
//
//    private void sendUpdateProgressHandler(){
//        if (musicControl != null) {
//            handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 200);
//        }
//    }
//
//
//    /**
//     * 进度条的值修改修改播放
//     * @param position
//     */
//    protected void resetPlayedSegmentPlayEntity(int position) {
//        if(this.segmentPlayEntities == null){
//            return;
//        }
//
//        for (int index = 0; index < this.segmentPlayEntities.size(); index++) {
//            SegmentPlayEntity segmentPlayEntity = segmentPlayEntities.get(index);
//            if ( position >=segmentPlayEntity.getStartTime()  && position <= segmentPlayEntity.getEndTime() ) {
//
//                playIndex = index;
//                break;
//            }
//            if(index==0){
//                if(position <= segmentPlayEntity.getStartTime()){
//                    playIndex = index;
//                    break;
//                }
//            }else{
//                SegmentPlayEntity lastSegmentPlayEntity = segmentPlayEntities.get(index - 1);
//                if(position>= lastSegmentPlayEntity.getEndTime() && position <= segmentPlayEntity.getStartTime()){
//                    playIndex = index;
//                    break;
//                }
//            }
//
//
//        }
//
//        if(playIndex >= segmentPlayEntities.size()){
//            playIndex = segmentPlayEntities.size()-1;
//        }
//
//
//        final SegmentPlayEntity currentPlayEntity = segmentPlayEntities.get(playIndex);
//
//        int newPosition = position - currentPlayEntity.getStartTime();
//        if (newPosition < 0) {
//            newPosition = 0;
//        }
//        musicControl.pause();
//        musicControl.play(playIndex,newPosition);
//
//
//    }
//
//
//    /**
//     * 开始更新进度条
//     */
//    public void startUpdateProgress() {
//        if (musicControl != null) {
//            handler.sendEmptyMessage(UPDATE_PROGRESS);
//        }
//    }
//
//    /**
//     * 与服务端的连接器
//     */
//    private class MyConnection implements ServiceConnection {
//
//        //服务启动完成后会进入到这个方法
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            //获得service中的MyBinder
//            musicControl = (MusicService.MusicBinder) service;
//            musicControl.setPlaySegmentIndexConsumer(new LQHandler.Consumer<Integer>() {
//                @Override
//                public void accept(Integer integer) {
//                    if(playBarI != null){
//                        playBarI.playSegmentIndex(integer);
//                    }
//                }
//            });
//
//
//            musicControl.setPlayCommplete(new Runnable() {
//                @Override
//                public void run() {
//
//                    if (playBarI != null) {
//                        playBarI.playNext();
//                        return;
//                    }
//
//
//                }
//            });
//
//            if(musicControl.getSegmentPlayEntityList()==null){
//                if(segmentPlayEntities!=null){
//                    musicControl.setSegmentPlayEntityList(segmentPlayEntities);
//                }
//            }
//
//
//
//            if(musicControl.isPlaying()){
//                play();
//            }
//            startUpdateProgress();
//            //更新按钮的文字
//            //updatePlay();
//            //设置进度条的最大值
//
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    }
//
//
//
//    public interface PlayBarI {
//        /**
//         * 播放
//         */
//        public void play();
//
//        /**
//         * 播放下一个
//         */
//        public void playNext();
//
//        /**
//         * 播放前一个
//         */
//        public void playPrevious();
//
//        /**
//         * 暂停
//         */
//        public void stop();
//
//        /**
//         *播放下一个段
//         */
//
//        public void playSegmentIndex(Integer segmentIndex);
//
//        /**
//         * 当前播放时间改变时调用
//         * @param currentTime
//         */
//        public void currentTimeChange(long currentTime);
//    }
//
//
//}
