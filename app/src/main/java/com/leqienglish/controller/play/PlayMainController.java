package com.leqienglish.controller.play;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.content.MyContentDataCache;
import com.leqienglish.data.segment.SegmentDataCache;
import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.view.adapter.simpleitem.SimpleItemAdapter;
import com.leqienglish.view.play.PlayBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;

public class PlayMainController extends ControllerAbstract {
    private ListView listView;
    private PlayBarView playBarView;
    private Integer selectedIndex;
    private Content selectedContent;
    private SimpleItemAdapter<Content> contentSimpleItemAdapter;
    public PlayMainController(View view) {
        super(view);
    }

    @Override
    public void init() {
        this.listView = (ListView) this.findViewById(R.id.play_main_listview);
        this.playBarView = (PlayBarView) this.findViewById(R.id.play_main_playBardView);
        this.contentSimpleItemAdapter = new SimpleItemAdapter<Content>(LayoutInflater.from(getView().getContext())) {
            @Override
            protected String toString(Content content) {
                return content.getTitle();
            }

            @Override
            protected void setStyle(TextView textView) {
            }
        };

        this.listView.setAdapter(contentSimpleItemAdapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               selectedIndex = position;
               play(selectedIndex);

            }
        });

        this.playBarView.init();
        this.playBarView.setPlayBarI(new PlayBarView.PlayBarI() {
            @Override
            public void play() {

            }

            @Override
            public void playNext() {
                selectedIndex += 1;
                if(selectedIndex  == contentSimpleItemAdapter.getCount()){
                    selectedIndex = 0;
                }

               PlayMainController.this.play(selectedIndex);
            }

            @Override
            public void playPrevious() {
                selectedIndex -= 1;
                if(selectedIndex == -1){
                    selectedIndex = contentSimpleItemAdapter.getCount() -1;
                }
                PlayMainController.this.play(selectedIndex);
            }

            @Override
            public void stop() {

            }
        });
        reload();
    }

    @Override
    public void reload() {
        MyContentDataCache.getInstance().load(new LQHandler.Consumer<List<Content>>() {
            @Override
            public void accept(List<Content> contents) {
                if(contents == null){
                    contents = Collections.emptyList();
                }
                contentSimpleItemAdapter.updateListView(contents);
            }
        });
    }

    @Override
    public void destory() {
        playBarView.onDestroy();
    }

    private void play(Integer position){
        Content content = contentSimpleItemAdapter.getItem(position);
        selectedContent = content;
        loadSegments(content);
    }

    /**
     * 递归加载音频文件
     * @param filePaths
     * @param index
     */
    private void loadFiles(List<SegmentPlayEntity> filePaths , int index){


            SegmentPlayEntity segmentPlayEntity = filePaths.get(index);


            LoadFile.loadFile(segmentPlayEntity.getFilePath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    segmentPlayEntity.setFilePath(s);
                    if(index == filePaths.size()-1){

                        return;
                    }
                    loadFiles(filePaths,index+1);

                }
            });


    }

    private void initPlayBarView( List<SegmentPlayEntity> filePaths ){

        //先加载第一个文件，然后异步加载其他文件
       LoadFile.loadFile(filePaths.get(0).getFilePath(), new LQHandler.Consumer<String>() {
           @Override
           public void accept(String s) {
               filePaths.get(0).setFilePath(s);
               playBarView.init(1,filePaths);
               TaskUtil.run(new Runnable() {
                   @Override
                   public void run() {
                       loadFiles(filePaths,1);
                   }
               });

           }
       });

    }

    private void loadSegments(Content content){
        SegmentDataCache segmentDataCache = new SegmentDataCache(content);
        segmentDataCache.load(new LQHandler.Consumer<List<Segment>>() {
            @Override
            public void accept(List<Segment> segments) {
                if(segments == null){
                    return;
                }

                List<SegmentPlayEntity> segmentPlayEntities = createSegmentPlayEntitys(segments);
                initPlayBarView(segmentPlayEntities);
            }
        });
    }

    private List<SegmentPlayEntity> createSegmentPlayEntitys(List<Segment> segments){
        Collections.sort(segments, new Comparator<Segment>() {
            @Override
            public int compare(Segment o1, Segment o2) {
                return  o1.getIndexNo() -o2.getIndexNo() ;
            }
        });
        List<SegmentPlayEntity> segmentPlayEntities = new ArrayList<>(segments.size());
        SegmentPlayEntity lastPlayEntity =  null;
        for(Segment segment : segments){
            SegmentPlayEntity segmentPlayEntity = SegmentPlayEntity.toSegmentPlayEntity(segment);

            if(lastPlayEntity != null){
                segmentPlayEntity.setEndTime(lastPlayEntity.getEndTime()+segmentPlayEntity.getEndTime());
                segmentPlayEntity.setStartTime(lastPlayEntity.getEndTime());

            }

            lastPlayEntity = segmentPlayEntity;
            segmentPlayEntities.add(segmentPlayEntity);
        }

        return segmentPlayEntities;
    }





}
