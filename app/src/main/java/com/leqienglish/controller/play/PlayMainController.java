package com.leqienglish.controller.play;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.segment.SegmentPlayActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.content.ContentDataCache;
import com.leqienglish.data.content.MyContentDataCache;
import com.leqienglish.data.segment.SegmentPlayEntityGenerator;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.user.UserHeartedDataCache;
import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.service.music.MusicService;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.SharePlatform;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.adapter.simpleitem.SimpleItemAdapter;
import com.leqienglish.view.operation.OperationBar;
import com.leqienglish.view.play.PlayBarDelegate;
import com.leqienglish.view.play.playbar.PlayBarView;

import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.UserHearted;

import static android.content.Context.BIND_AUTO_CREATE;

public class PlayMainController extends ControllerAbstract {
    private ListView listView;
    private PlayBarView playBarView;
    private OperationBar operationBar;
    private Integer selectedIndex = -1;
    private Content selectedContent;
    private SimpleItemAdapter<Content> contentSimpleItemAdapter;
    protected MusicService.MusicBinder musicControl;
    private MyConnection conn;
    public PlayMainController(View view) {
        super(view);
    }

    @Override
    public void init() {
        this.listView = (ListView) this.findViewById(R.id.play_main_listview);
        this.playBarView = (PlayBarView) this.findViewById(R.id.play_main_playBardView);
        this.operationBar = (OperationBar) this.findViewById(R.id.play_main_operationbar);

        this.contentSimpleItemAdapter = new SimpleItemAdapter<Content>(LayoutInflater.from(getView().getContext())) {
            @Override
            protected String toString(Content content) {
                return content.getTitle();
            }

            @Override
            protected void setStyle(TextView textView) {
            }

            @Override
            protected void hasSelected(int position) {
                View view = listView.getChildAt(position);

                if (view == null) {
                    return;
                }

                ViewHolder viewHolder = (ViewHolder) view.getTag();
                viewHolder.textView.setTextSize(16);
                viewHolder.textView.setTextColor(0xFFed742e);
                viewHolder.textView.setTypeface(Typeface.DEFAULT_BOLD);
            }

            @Override
            protected void hasDisSelected(int position) {
                View view = listView.getChildAt(position);

                if (view == null) {
                    return;
                }

                ViewHolder viewHolder = (ViewHolder) view.getTag();
                viewHolder.textView.setTextSize(16);
                viewHolder.textView.setTextColor(Color.BLACK);
                viewHolder.textView.setTypeface(Typeface.DEFAULT);

            }
        };

        this.listView.setAdapter(contentSimpleItemAdapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedIndex = position;
                play(selectedIndex);
                contentSimpleItemAdapter.selecte(position);

            }
        });



        this.initOperationBar(this.operationBar);
        initService();
        reload();
    }

    private void initService(){
        conn = new MyConnection();
        //使用混合的方法开启服务，
        Intent intent3 = new Intent(this.getContext(), MusicService.class);
        getContext().startService(intent3);

        startBind();
    }





    public void onResume() {
        startBind();
    }

    public void onPause() {
       this.unbind();
    }

    /**
     * 建立与服务端的绑定
     */
    public void startBind(){

        Intent intent3 = new Intent(this.getContext(), MusicService.class);
        getContext().bindService(intent3, conn, BIND_AUTO_CREATE);
        if(musicControl != null ){
            if(musicControl.isPlaying()){
                playBarView.play();
            }else{
                playBarView.stop();
            }

        }
    }

    private void hearted(){
        if(this.selectedContent == null || this.selectedContent.getAwesomeNum() == null){
            return;
        }

        this.selectedContent.setAwesomeNum(selectedContent.getAwesomeNum()+1);

        updateHearted(true);
        UserHeartedDataCache userHeartedDataCache = new UserHeartedDataCache(selectedContent.getId());
        userHeartedDataCache.commit(Consistent.CONTENT_TYPE_CONTENT);
        ContentDataCache.update(selectedContent.getId());
    }

    private void updateHearted(boolean userInteract){
        if(this.selectedContent == null || this.selectedContent.getAwesomeNum() == null){
            return;
        }

        long awesomeNum = this.selectedContent.getAwesomeNum();



        if(userInteract){
            this.operationBar.update("hearted",awesomeNum+"",R.drawable.heart_red);
            return;
        }else{
            this.operationBar.update("hearted",awesomeNum+"",R.drawable.heart);
        }

        UserHeartedDataCache userHeartedDataCache = new UserHeartedDataCache(selectedContent.getId());
        userHeartedDataCache.load(new LQHandler.Consumer<UserHearted>() {
            @Override
            public void accept(UserHearted userHearted) {
                if(userHearted != null){
                    operationBar.update("hearted",R.drawable.heart_red);
                }
            }
        });

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



    private void initOperationBar(OperationBar operationBar) {
        this.operationBar.setOperationBarI(new OperationBar.OperationBarI() {

            @Override
            public void handler(String id) {
                switch (id){
                    case "return":
                        playBarView.stop();
                        PlayMainController.this.finished();
                        break;
                    case "content":
                        contentClickHandler();
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


    private void contentClickHandler() {


        if (selectedContent == null) {
            ToastUtil.showShort(getContext(), "没有选中数据");
            return;
        }

        int currentPlayIndex = 0;

        if(musicControl  != null){
            currentPlayIndex = musicControl.getCurrentPlayIndex();
        }


        Intent intent = new Intent();

        Bundle bundle = BundleUtil.create(BundleUtil.DATA, selectedContent);
        BundleUtil.create(bundle, BundleUtil.INDEX,currentPlayIndex);
        intent.putExtras(bundle);
        intent.setClass(getContext(), SegmentPlayActivity.class);
        getContext().startActivity(intent);
    }


    private void shareClickHandler() {
        if(selectedContent == null){
            return;
        }
        String userName = UserDataCache.getInstance().getUserName();
        String userId = UserDataCache.getInstance().getUserId();
        String hasFinished = "刚刚完成了\"\"的背诵";
        //  String segmentId = segment.getId();
        StringBuilder stringBuilder = new StringBuilder(LQService.getHttp());
        stringBuilder.append("/html/details.html").append("?userId=").append(userId).append("&id=").append(selectedContent.getId());
        SharePlatform.onShare(getView().getContext(), userName + hasFinished, "我" + hasFinished, LQService.getLogoPath(), stringBuilder.toString());

    }



    @Override
    public void reload() {
        MyContentDataCache.getInstance().load(new LQHandler.Consumer<List<Content>>() {
            @Override
            public void accept(List<Content> contents) {
                if (contents == null) {
                    contents = Collections.emptyList();
                }
                contentSimpleItemAdapter.updateListView(contents);
            }
        });
    }

    @Override
    public void destory() {
        if(musicControl != null ){
            if(musicControl.isPlaying()){
                musicControl.pause();
                playBarView.stop();
            }

        }
    }

    private void play(Integer position) {
        contentSimpleItemAdapter.selecte(selectedIndex);
        Content content = contentSimpleItemAdapter.getItem(position);
        selectedContent = content;

        SegmentPlayEntityGenerator segmentPlayEntityGenerator = new SegmentPlayEntityGenerator(content);
        segmentPlayEntityGenerator.accept(new LQHandler.Consumer<List<SegmentPlayEntity>>() {
            @Override
            public void accept(List<SegmentPlayEntity> segmentPlayEntities) {
                if(segmentPlayEntities == null || segmentPlayEntities.isEmpty()){
                    return;
                }
                if(musicControl !=null){
                    musicControl.setSegmentPlayEntityList(segmentPlayEntities);
                    musicControl.play(0);
                }

                playBarView.init(0, segmentPlayEntities.get(segmentPlayEntities.size()-1).getEndTime());

            }
        });

        //loadSegments(content);
        updateHearted(false);
    }



    class PlayMainMusicControlImpl implements MusicService.MusicBinderDelegate {
        private PlayBarView playBarView;

        public PlayMainMusicControlImpl(PlayBarView playBarView){
            this.playBarView = playBarView;
        }

        @Override
        public void currentTimeChange(int index , int currentTime) {

            if(this.playBarView == null){
                return;
            }

            int startTime = 0;
            if(index >0 && index < musicControl.getSegmentPlayEntityList().size()){
                startTime = musicControl.getSegmentPlayEntityList().get(index -1).getEndTime();
            }

            this.playBarView.updateProgress(startTime+currentTime);
        }

        @Override
        public void finished() {
            //播放下一条数据
            selectedIndex += 1;
            if (selectedIndex == contentSimpleItemAdapter.getCount()) {
                selectedIndex = 0;
            }

            contentSimpleItemAdapter.selecte(selectedIndex);

            PlayMainController.this.play(selectedIndex);
        }

        @Override
        public void currentPlayIndexChange(int playIndex) {

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
            if(selectedIndex<0|| selectedContent == null){
                selectedIndex = 0;
                PlayMainController.this.play(selectedIndex);
            }
            musicBinder.play();
        }

        @Override
        public void playNext() {
            selectedIndex += 1;
            if (selectedIndex == contentSimpleItemAdapter.getCount()) {
                selectedIndex = 0;
            }



            PlayMainController.this.play(selectedIndex);
        }

        @Override
        public void playPrevious() {
            selectedIndex -= 1;
            if (selectedIndex == -1) {
                selectedIndex = contentSimpleItemAdapter.getCount() - 1;
            }

            PlayMainController.this.play(selectedIndex);
        }

        @Override
        public void stop() {
            if(musicBinder == null){
                return;
            }
            musicBinder.pause();
        }



        @Override
        public void updateProgress(long newValue) {
           for(int i = 0 ; i < musicBinder.getSegmentPlayEntityList().size() ; i++){
                SegmentPlayEntity segmentPlayEntity = musicBinder.getSegmentPlayEntityList().get(i);
                if(newValue >= segmentPlayEntity.getStartTime()&& newValue <= segmentPlayEntity.getEndTime()){
                    musicBinder.play(i,Integer.valueOf((newValue - segmentPlayEntity.getStartTime())+""));
                    return;
               }
           }
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

            playBarView.setPlayBarDelegate(new PlayMainPlayBarImpl(musicControl));

            musicControl.setMusicBinderDelegate(new PlayMainMusicControlImpl(playBarView));

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


}
