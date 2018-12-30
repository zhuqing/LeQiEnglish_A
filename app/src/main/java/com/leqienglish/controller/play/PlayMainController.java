package com.leqienglish.controller.play;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.segment.SegmentPlayActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.content.MyContentDataCache;
import com.leqienglish.data.segment.SegmentDataCache;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.sf.LQService;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.SharePlatform;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.adapter.simpleitem.SimpleItemAdapter;
import com.leqienglish.view.operation.OperationBar;
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
    private OperationBar operationBar;
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


        initPlayBarView(this.playBarView);
        this.initOperationBar(this.operationBar);
        reload();
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


    private void initOperationBar(OperationBar operationBar) {
        this.operationBar.setOperationBarI(new OperationBar.OperationBarI() {
            @Override
            public void heartClickHandler() {

            }

            @Override
            public void contentClickHandler() {


                playBarView.onStopUpdate();
                if (selectedContent == null) {
                    ToastUtil.showShort(getContext(), "没有选中数据");
                    return;
                }

                playBarView.unbind();
                Intent intent = new Intent();

                Bundle bundle = BundleUtil.create(BundleUtil.DATA, selectedContent);
                BundleUtil.create(bundle, BundleUtil.INDEX, playBarView.getPlayIndex());
                intent.putExtras(bundle);
                intent.setClass(getContext(), SegmentPlayActivity.class);
                getContext().startActivity(intent);
            }

            @Override
            public void shareClickHandler() {
                String userName = UserDataCache.getInstance().getUserName();
                String userId = UserDataCache.getInstance().getUserId();
                String hasFinished = "刚刚完成了\"\"的背诵";
                //  String segmentId = segment.getId();
                StringBuilder stringBuilder = new StringBuilder(LQService.getHttp());
                stringBuilder.append("html/share/shareContent.html").append("?userId=").append(userId).append("&segmentId=").append("");
                SharePlatform.onShare(getView().getContext(), userName + hasFinished, "我" + hasFinished, LQService.getLogoPath(), stringBuilder.toString());

            }
        });
    }

    private void initPlayBarView(PlayBarView playBarView) {
        this.playBarView.setPlayBarI(new PlayBarView.PlayBarI() {
            @Override
            public void play() {

            }

            @Override
            public void playNext() {
                selectedIndex += 1;
                if (selectedIndex == contentSimpleItemAdapter.getCount()) {
                    selectedIndex = 0;
                }

                contentSimpleItemAdapter.selecte(selectedIndex);

                PlayMainController.this.play(selectedIndex);
            }

            @Override
            public void playPrevious() {
                selectedIndex -= 1;
                if (selectedIndex == -1) {
                    selectedIndex = contentSimpleItemAdapter.getCount() - 1;
                }
                contentSimpleItemAdapter.selecte(selectedIndex);
                PlayMainController.this.play(selectedIndex);
            }

            @Override
            public void stop() {

            }

            @Override
            public void playSegmentIndex(Integer index) {

            }

            @Override
            public void currentTimeChange(long currentTime) {

            }
        });
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
        playBarView.onDestroy();
    }

    private void play(Integer position) {
        Content content = contentSimpleItemAdapter.getItem(position);
        selectedContent = content;
        loadSegments(content);
    }

    private void initHeart(Content content){
        content.getAwesomeNum();
      //  operationBar.set
    }

    /**
     * 递归加载音频文件
     *
     * @param filePaths
     * @param index
     */
    private void loadFiles(List<SegmentPlayEntity> filePaths, int index) {


        SegmentPlayEntity segmentPlayEntity = filePaths.get(index);


        LoadFile.loadFile(segmentPlayEntity.getFilePath(), new LQHandler.Consumer<String>() {
            @Override
            public void accept(String s) {
                segmentPlayEntity.setFilePath(s);
                if (index == filePaths.size() - 1) {

                    return;
                }
                loadFiles(filePaths, index + 1);

            }
        });


    }

    private void initPlayBarView(List<SegmentPlayEntity> filePaths) {

        //先加载第一个文件，然后异步加载其他文件
        LoadFile.loadFile(filePaths.get(0).getFilePath(), new LQHandler.Consumer<String>() {
            @Override
            public void accept(String s) {
                filePaths.get(0).setFilePath(s);
                playBarView.init(1, filePaths);
                TaskUtil.run(new Runnable() {
                    @Override
                    public void run() {
                        loadFiles(filePaths, 1);
                    }
                });

            }
        });

    }

    private void loadSegments(Content content) {
        SegmentDataCache segmentDataCache = new SegmentDataCache(content);
        segmentDataCache.load(new LQHandler.Consumer<List<Segment>>() {
            @Override
            public void accept(List<Segment> segments) {
                if (segments == null) {
                    return;
                }

                List<SegmentPlayEntity> segmentPlayEntities = createSegmentPlayEntitys(segments);
                initPlayBarView(segmentPlayEntities);
            }
        });
    }

    private List<SegmentPlayEntity> createSegmentPlayEntitys(List<Segment> segments) {
        Collections.sort(segments, new Comparator<Segment>() {
            @Override
            public int compare(Segment o1, Segment o2) {
                return o1.getIndexNo() - o2.getIndexNo();
            }
        });
        List<SegmentPlayEntity> segmentPlayEntities = new ArrayList<>(segments.size());
        SegmentPlayEntity lastPlayEntity = null;
        for (Segment segment : segments) {
            SegmentPlayEntity segmentPlayEntity = SegmentPlayEntity.toSegmentPlayEntity(segment);

            if (lastPlayEntity != null) {
                segmentPlayEntity.setEndTime(lastPlayEntity.getEndTime() + segmentPlayEntity.getEndTime());
                segmentPlayEntity.setStartTime(lastPlayEntity.getEndTime());

            }

            lastPlayEntity = segmentPlayEntity;
            segmentPlayEntities.add(segmentPlayEntity);
        }

        return segmentPlayEntities;
    }


}
