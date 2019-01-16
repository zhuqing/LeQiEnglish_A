package com.leqienglish.controller.segment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.leqienglish.R;
import com.leqienglish.activity.segment.SegmentWordsActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.segment.SegmentDataCache;
import com.leqienglish.data.segment.SegmentPlayEntityGenerator;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.user.UserHeartedDataCache;
import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.service.music.MusicService;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.SharePlatform;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.operation.OperationBar;
import com.leqienglish.view.play.PlayBarDelegate;
import com.leqienglish.view.play.playbar.PlayBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;
import xyz.tobebetter.entity.user.UserHearted;

import static android.content.Context.BIND_AUTO_CREATE;

public class SegmentPlayController extends ControllerAbstract {

    private LOGGER logger = new LOGGER(SegmentPlayController.class);
    private PlayBarView playBarView;

    private LinearLayout playItemRootLayout;
    private OperationBar operationBar;

    private Content content;
    private Integer currentIndex;
    private ScrollView scrollView;
    private SegmentPlayEntity currentSegmentPlayEntity;
    List<AudioPlayPoint> playEntities = null;
    private boolean isClosePlayerWhenRetern = false;
    List<View> views = new ArrayList<>();

    private View lastSelectedView;

    private List<Segment> currentSegments;
    private Integer playIndex = -1;


    protected MusicService.MusicBinder musicControl;
    private SegmentPlayController.MyConnection conn;


    public SegmentPlayController(View view, Integer currentIndex, Content content) {
        super(view);
        this.content = content;
        this.currentIndex = currentIndex;

    }

    @Override
    public void init() {
        this.playBarView = (PlayBarView) this.findViewById(R.id.segment_play_audio_playbar);
        this.playItemRootLayout = (LinearLayout) this.findViewById(R.id.segment_play_audio_playitem_root);
        this.scrollView = (ScrollView) this.findViewById(R.id.segment_play_audio_playitem_scrollview);
        this.operationBar = (OperationBar) this.findViewById(R.id.segment_play_audio_operationbar);
        initOperationBar(operationBar);
        initService();

    }


    private void initOperationBar(OperationBar operationBar) {
        operationBar.setOperationBarI(new OperationBar.OperationBarI() {

            @Override
            public void handler(String id) {
                switch (id){
                    case "return":
                       // playBarView.stop();
                        finished();
                        break;
                    case "words":
                        Segment segment = getCurrentPlayingSegment();
                        if(segment == null){
                            return;
                        }
                        playBarView.stop();
                        toWordsAndShortWordsView(segment);
                        break;
                    case "hearted":
                        hearted();
                        break;
                    case "share":
                        shareClickHandler();
                        break;

                    default:


                }
            }
        });
    }

    private void hearted(){
        Segment segment = this.getCurrentPlayingSegment();
        if(segment == null){
            return;
        }

        Integer awesomeNum = segment.getAwesomeNum() == null ? 0 :segment.getAwesomeNum();


        segment.setAwesomeNum(awesomeNum + 1);

        updateHearted(true);
        UserHeartedDataCache userHeartedDataCache = new UserHeartedDataCache(segment.getId());
        userHeartedDataCache.commit(Consistent.CONTENT_TYPE_SEGMENT);
        //ContentDataCache.update(selectedContent.getId());
    }

    private void updateHearted(boolean userInteract){
        Segment segment = this.getCurrentPlayingSegment();
        if(segment == null){
            return;
        }

        Integer awesomeNum = segment.getAwesomeNum() == null ? 0 :segment.getAwesomeNum();

        if(userInteract){
            this.operationBar.update("hearted",awesomeNum+"",R.drawable.heart_red);
            return;
        }else{
            this.operationBar.update("hearted",awesomeNum+"",R.drawable.heart);
        }

        UserHeartedDataCache userHeartedDataCache = new UserHeartedDataCache(segment.getId());
        userHeartedDataCache.load(new LQHandler.Consumer<UserHearted>() {
            @Override
            public void accept(UserHearted userHearted) {
                if(userHearted != null){
                    operationBar.update("hearted",R.drawable.heart_red);
                }
            }
        });

    }


    private void toWordsAndShortWordsView(Segment segment){
        Intent intent = new Intent();
        intent.putExtras(BundleUtil.create(BundleUtil.DATA, segment));
        intent.setClass(getView().getContext(), SegmentWordsActivity.class);
        getView().getContext().startActivity(intent);
    }

    private Segment getCurrentPlayingSegment(){
        if(currentSegments == null || currentSegments.isEmpty()){
            return null;
        }

        if(currentIndex < 0 || currentIndex >= currentSegments.size()){
            return null;
        }

       return currentSegments.get(this.currentIndex);
    }


    private void shareClickHandler() {

        String userName = UserDataCache.getInstance().getUserName();
        String userId = UserDataCache.getInstance().getUserId();

        Segment segment = getCurrentPlayingSegment();
        if(segment == null){
            return;
        }


        String hasFinished = "正在听演讲\""+segment.getTitle()+"\"";


        StringBuilder stringBuilder = new StringBuilder(LQService.getHttp());
        stringBuilder.append("html/share/shareContent.html").append("?userId=").append(userId).append("&segmentId=").append("");
        SharePlatform.onShare(getView().getContext(), userName + hasFinished, userName + hasFinished, LQService.getLogoPath(), stringBuilder.toString());

    }


    private void initService(){
        conn = new SegmentPlayController.MyConnection();
        //使用混合的方法开启服务，
        Intent intent3 = new Intent(this.getContext(), MusicService.class);
        getContext().startService(intent3);
    }



    private void changePlayIdex(Integer newPlayIndex){


        if(this.playItemRootLayout.getChildCount() <= newPlayIndex){
            return;
        }


        if(lastSelectedView != null){
            changeTextColor(lastSelectedView,R.color.black);
        }
        this.playIndex = newPlayIndex;

        lastSelectedView = this.playItemRootLayout.getChildAt(playIndex);
        scrollView.scrollTo(0,Float.valueOf(lastSelectedView.getY()).intValue());
        changeTextColor(lastSelectedView,R.color.dark_orange);
    }

    private void changeTextColor(View lastSelectedView , int colorId){
        SegmentInfoController.ViewHolder viewHolder = (SegmentInfoController.ViewHolder) lastSelectedView.getTag();
        viewHolder.title.setTextColor(getContext().getResources().getColor(colorId));

    }

    public void onResume() {
        startBind();
        if(musicControl!=null){
            musicControl.play();
        }
    }



    /**
     * 建立与服务端的绑定
     */
    public void startBind(){

        Intent intent3 = new Intent(this.getContext(), MusicService.class);
        getContext().bindService(intent3, conn, BIND_AUTO_CREATE);
    }

    /**
     * 解除与服务端的绑定
     */
    public void unbind(){
        try{
            getContext().unbindService(conn);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void reload() {
        if (content == null || content.getId() == null) {
            return;
        }
        SegmentDataCache segmentDataCache = new SegmentDataCache(content);

        segmentDataCache.load(new LQHandler.Consumer<List<Segment>>() {
            @Override
            public void accept(List<Segment> segments) {
                currentSegments = segments;
                loadData(currentIndex, segments);
            }
        });
    }

    private void loadData(Integer currentIndex, List<Segment> segments) {
        if (currentIndex < 0 || currentIndex >= segments.size()) {
            return;
        }
        this.currentIndex = currentIndex;
        Segment currentSegment = getCurrentPlayingSegment();
        if(currentSegment == null){
            return;
        }


        this.currentSegmentPlayEntity = SegmentPlayEntity.toSegmentPlayEntity(currentSegment);

        playBarView.setMax(this.currentSegmentPlayEntity.getEndTime());
        updateHearted(false);

        TaskUtil.run(new LQHandler.Supplier<List<View>>() {
                         @Override
                         public List<View> get() {
                             try {
                                 List<AudioPlayPoint> entitys = AudioPlayPoint.toAudioPlays(currentSegment.getContent());
                                 playEntities = entitys;

                                 return loadView(entitys);
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                             return Collections.EMPTY_LIST;

                         }
                     }, new LQHandler.Consumer<List<View>>() {
                         @Override
                         public void accept(List<View> views) {
                             playItemRootLayout.removeAllViews();
                             for(View view : views){
                                 playItemRootLayout.addView(view);

                             }
                         }
                     }
        );

    }


    public List<View> loadView(List<AudioPlayPoint> playEntities) {

        List<View> views = new ArrayList<View>();
        LayoutInflater layoutInflater = LayoutInflater.from(this.getView().getContext());
        for (AudioPlayPoint audioPlayPoint : playEntities) {
            final View view = layoutInflater.inflate(R.layout.play_audio_item, null);

            SegmentInfoController.ViewHolder viewHolder = new SegmentInfoController.ViewHolder();
            viewHolder.title = view.findViewById(R.id.play_audio_text);
            viewHolder.play_audio_playerpane = view.findViewById(R.id.play_audio_playerpane);
            viewHolder.ch = view.findViewById(R.id.play_audio_text_ch);
            viewHolder.view = view;

            viewHolder.audioPlayPoint = audioPlayPoint;
            viewHolder.title.setText(audioPlayPoint.getEnText());
            if (audioPlayPoint.getChText() != null) {
                viewHolder.ch.setText(audioPlayPoint.getChText());
            }

            view.setTag(viewHolder);

            views.add(view);

        }

        return views;

    }

    @Override
    public void destory() {
        if(this.isClosePlayerWhenRetern){
            if(musicControl != null){
                musicControl.pause();
            }
        }

        unbind();
    }

    public boolean isClosePlayerWhenRetern() {
        return isClosePlayerWhenRetern;
    }

    public void setClosePlayerWhenRetern(boolean closePlayerWhenRetern) {
        isClosePlayerWhenRetern = closePlayerWhenRetern;
    }

    class PlayMainMusicControlImpl implements MusicService.MusicBinderDelegate {
        private PlayBarView playBarView;

        public PlayMainMusicControlImpl(PlayBarView playBarView){
            this.playBarView = playBarView;
        }

        @Override
        public void currentTimeChange(int index, int currentTime) {
            if(this.playBarView == null){
                return;
            }

            this.playBarView.updateProgress(currentTime);

            if(playEntities == null){
                return;
            }
            for(int i = 0 ; i < playEntities.size() ; i++){
                AudioPlayPoint audioPlayPoint = playEntities.get(i);
                if(currentTime >= audioPlayPoint.getStartTime() && currentTime <= audioPlayPoint.getEndTime()){
                    changePlayIdex(i);
                    break;
                }
            }

        }

        @Override
        public void finished() {
            //播放下一条数据
            ToastUtil.showShort(getContext(),"播放完成");
        }

        @Override
        public void currentPlayIndexChange(int playIndex) {

            currentIndex = playIndex;
            loadData(currentIndex, currentSegments);


        }
    }


    /**
     * 实现playBarView的接口
     */
    class PlayMainPlayBarImpl implements PlayBarDelegate {
        private MusicService.MusicBinder musicBinder;

        public PlayMainPlayBarImpl(MusicService.MusicBinder musicBinder){
            this.musicBinder = musicBinder;
        }

        @Override
        public void play() {
            if(musicBinder == null){
                return;
            }
            musicBinder.play();
        }

        @Override
        public void playNext() {

            if(musicControl == null){
                return;
            }

            if(currentIndex == musicControl.getSegmentPlayEntityList().size()-1){
                ToastUtil.showShort(getContext(),"已经是第一个了");
                return;
            }

            musicControl.playNext();
        }

        @Override
        public void playPrevious() {

            if(currentIndex == 0){
                ToastUtil.showShort(getContext(),"已经是第一个了");
                return;
            }
            if(musicControl == null){
                return;
            }

            musicControl.playProvious();
        }


        @Override
        public void stop() {
            if(musicControl == null){
                return;
            }

            musicControl.pause();
        }



        @Override
        public void updateProgress(long newValue) {

          musicBinder.play(musicBinder.getCurrentPlayIndex(),Integer.valueOf(newValue+""));
        }
    }

    /**
     * 与服务端的连接器
     */
    private class MyConnection implements ServiceConnection {

        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicBinder) service;
            if(musicControl == null){
                return;
            }

            if(musicControl.isPlaying()){
                playBarView.play();
            }else{
                musicControl.play(currentIndex);
                playBarView.play();
            }

            if(musicControl.getSegmentPlayEntityList() == null){
                startPlayAudio();
            }



            playBarView.setPlayBarDelegate(new SegmentPlayController.PlayMainPlayBarImpl(musicControl));

            musicControl.setMusicBinderDelegate(new SegmentPlayController.PlayMainMusicControlImpl(playBarView));


        }

        private void startPlayAudio(){
            SegmentPlayEntityGenerator segmentPlayEntityGenerator = new SegmentPlayEntityGenerator(content);
            segmentPlayEntityGenerator.accept(new LQHandler.Consumer<List<SegmentPlayEntity>>() {
                @Override
                public void accept(List<SegmentPlayEntity> segmentPlayEntities) {
                    musicControl.setSegmentPlayEntityList(segmentPlayEntities);
                    musicControl.play(currentIndex);
                    if(musicControl.isPlaying()){
                        playBarView.play();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}
