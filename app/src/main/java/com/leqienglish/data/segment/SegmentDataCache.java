package com.leqienglish.data.segment;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;

import static com.leqienglish.database.Constants.SEGMENT_TYPE;

public class SegmentDataCache extends DataCacheAbstract<List<Segment>> {

    private Content content;

    public SegmentDataCache(Content content){
        this.content = content;

    }
    @Override
    protected List<Segment> getFromCache() {
        if(this.getContent() == null){
            return null;
        }

       List<Segment> segments =  ExecuteSQL.getDatasByType(SEGMENT_TYPE,this.getContent().getId(),Segment.class);

        if(segments == null || segments.isEmpty()){
            return null;
        }

        Collections.sort(segments,new Comparator<Segment>() {
            @Override
            public int compare(Segment o1, Segment o2) {
                return o2.getIndexNo()-o1.getIndexNo();
            }
        });

        return segments;
    }

    @Override
    protected void putCache(List<Segment> segmentList) {
        if(this.getContent() == null){
            return ;
        }
        ExecuteSQL.insertLearnE(segmentList,this.getContent().getId(),SEGMENT_TYPE);

    }

    @Override
    protected List<Segment> getFromService() {
        if(this.getContent() == null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("contentId", this.getContent().getId());
        try {
            Segment[] segments = this.getRestClient().get("/segment/findByContentId",param,Segment[].class);
            if(segments == null){
                return null;
            }

            return Arrays.asList(segments);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void add(List<Segment> segmentList) {

    }

    @Override
    public void remove(List<Segment> segmentList) {

    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
