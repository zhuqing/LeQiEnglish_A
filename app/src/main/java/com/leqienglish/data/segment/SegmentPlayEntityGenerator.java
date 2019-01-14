package com.leqienglish.data.segment;


import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.TaskUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;

/**
 * 通过Content生成SegmentPlayEntityList
 */
public class SegmentPlayEntityGenerator implements LQHandler.Consumer<LQHandler.Consumer<List<SegmentPlayEntity>>> {

   private Content content;

   private LQHandler.Consumer<List<SegmentPlayEntity>> listConsumer;

   public SegmentPlayEntityGenerator(Content content){
       this.content = content;
   }



    private void initPlayBarView(List<com.leqienglish.entity.SegmentPlayEntity> segmentPlayEntities) {


        TaskUtil.run(new Runnable() {
            @Override
            public void run() {
                loadFiles(segmentPlayEntities, 1);
            }
        });

        //先加载第一个文件，然后异步加载其他文件
        LoadFile.loadFile(segmentPlayEntities.get(0).getFilePath(), new LQHandler.Consumer<String>() {
            @Override
            public void accept(String s) {
                segmentPlayEntities.get(0).setFilePath(s);
                listConsumer.accept(segmentPlayEntities);


            }
        });

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

    private void loadSegments(Content content) {
        SegmentDataCache segmentDataCache = new SegmentDataCache(content);
        segmentDataCache.load(new LQHandler.Consumer<List<Segment>>() {
            @Override
            public void accept(List<Segment> segments) {
                if (segments == null) {
                    return;
                }

                List<com.leqienglish.entity.SegmentPlayEntity> segmentPlayEntities = createSegmentPlayEntitys(segments);
                initPlayBarView(segmentPlayEntities);
            }
        });
    }

    private List<com.leqienglish.entity.SegmentPlayEntity> createSegmentPlayEntitys(List<Segment> segments) {
        //按照indexNo排序
        Collections.sort(segments, new Comparator<Segment>() {
            @Override
            public int compare(Segment o1, Segment o2) {
                return o1.getIndexNo() - o2.getIndexNo();
            }
        });

        List<com.leqienglish.entity.SegmentPlayEntity> segmentPlayEntities = new ArrayList<>(segments.size());
        com.leqienglish.entity.SegmentPlayEntity lastPlayEntity = null;
        for (Segment segment : segments) {
            com.leqienglish.entity.SegmentPlayEntity segmentPlayEntity = com.leqienglish.entity.SegmentPlayEntity.toSegmentPlayEntity(segment);

            if (lastPlayEntity != null) {
                segmentPlayEntity.setEndTime(lastPlayEntity.getEndTime() + segmentPlayEntity.getEndTime());
                segmentPlayEntity.setStartTime(lastPlayEntity.getEndTime());

            }

            lastPlayEntity = segmentPlayEntity;
            segmentPlayEntities.add(segmentPlayEntity);
        }

        return segmentPlayEntities;
    }

    @Override
    public void accept(LQHandler.Consumer<List<SegmentPlayEntity>> listConsumer) {
       this.listConsumer = listConsumer;
        loadSegments(content);
    }
}
