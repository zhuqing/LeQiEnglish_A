package com.leqienglish.controller.segment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.leqienglish.R;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.controller.PlayAudioAController;
import com.leqienglish.data.segment.SegmentDataCache;
import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.play.PlayBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;

public class SegmentPlayController extends ControllerAbstract {

    private LOGGER logger = new LOGGER(SegmentPlayController.class);
    private PlayBarView playBarView;

    private LinearLayout playItemRootLayout;

    private Content content;
    private Integer currentIndex;
    private ScrollView scrollView;
    private SegmentPlayEntity currentSegmentPlayEntity;
    List<AudioPlayPoint> playEntities = null;
    List<View> views = new ArrayList<>();

    private View lastSelectedView;

    private List<Segment> currentSegments;
    private Integer playIndex = -1;


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

        initPlayBarView(playBarView);
    }

    private void initPlayBarView(PlayBarView playBarView) {
        playBarView.setPlayBarI(new PlayBarView.PlayBarI() {
            @Override
            public void play() {

            }

            @Override
            public void playNext() {
                if(currentIndex == currentSegments.size()-1){
                    ToastUtil.showShort(getContext(),"已经是最后一个了");
                    return;
                }
                currentIndex += 1;
                playIndex = -1;
                playEntities = null;
                playItemRootLayout.removeAllViews();
                loadData(currentIndex,currentSegments);
                playBarView.play(currentIndex);
            }

            @Override
            public void playPrevious() {

                if(currentIndex == 0){
                    ToastUtil.showShort(getContext(),"已经是第一个了");
                    return;
                }
                currentIndex -= 1;
                playEntities = null;
                playItemRootLayout.removeAllViews();
                playIndex = -1;
                loadData(currentIndex,currentSegments);
                playBarView.play(currentIndex);
            }

            @Override
            public void stop() {

            }

            @Override
            public void playSegmentIndex(Integer index) {
                if(index == currentSegments.size()){
                    ToastUtil.showShort(getContext(),"已经是最后一个了");
                    return;
                }
                currentIndex  = index;

                playEntities = null;
                playItemRootLayout.removeAllViews();
                loadData(currentIndex,currentSegments);
            }

            @Override
            public void currentTimeChange(long currentTime) {
                if(playEntities == null){
                    return;
                }
                for(int i = 0 ; i < playEntities.size() ; i++){
                    AudioPlayPoint audioPlayPoint = playEntities.get(i);
                    if(currentTime >= audioPlayPoint.getStartTime() && currentTime <= audioPlayPoint.getEndTime()){
                      //  logger.d(i+"\t"+currentTime+"\tstartTime = "+audioPlayPoint.getStartTime() +"\tendTime"+audioPlayPoint.getEndTime());
                        changePlayIdex(i);
                        break;
                    }
                }
            }
        });
    }

    private void changePlayIdex(Integer newPlayIndex){
        if(newPlayIndex == this.playIndex){
            return;
        }

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
        PlayAudioAController.ViewHolder viewHolder = (PlayAudioAController.ViewHolder) lastSelectedView.getTag();
        viewHolder.title.setTextColor(getContext().getResources().getColor(colorId));

    }

    public void onResume() {
        if (this.playBarView != null) {
            this.playBarView.startBind();
        }
    }
    public void onPause(){
        if (this.playBarView != null) {
            this.playBarView.unbind();
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

        Segment currentSegment = segments.get(currentIndex);
        this.currentSegmentPlayEntity = SegmentPlayEntity.toSegmentPlayEntity(currentSegment);

        playBarView.setMax(this.currentSegmentPlayEntity.getEndTime());

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

            PlayAudioAController.ViewHolder viewHolder = new PlayAudioAController.ViewHolder();
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

    }
}
