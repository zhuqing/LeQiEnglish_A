package com.leqienglish.data.segment;

import android.os.AsyncTask;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;

import static com.leqienglish.database.Constants.SEGMENT_TYPE;

/**
 * Content下段的缓存
 */
public class SegmentDataCache extends DataCacheAbstract<List<Segment>> {

    private Content content;

    public SegmentDataCache(Content content){
        this.content = content;

    }

    @Override
    protected String getUpdateTimeType() {
       return "SegmentDataCache_update";
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
                return o1.getIndexNo()-o2.getIndexNo();
            }
        });

        return segments;
    }

    @Override
    protected void putCache(List<Segment> segmentList) {
        if(this.getContent() == null){
            return ;
        }
        this.clearData();
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
    protected void try2loadNewest(LQHandler.Consumer<List<Segment>> consumer) {
        super.try2loadNewest(consumer);

        AsyncTask asyncTask = new AsyncTask<Object, Object, Boolean>() {
            @Override
            protected Boolean doInBackground(Object[] objects) {
                MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
                param.add("id", getContent().getId());
                try {
                    Content newContent = getRestClient().get("/english/content/findById",param,Content.class);

                    if(newContent.getUpdateDate() <= getContent().getUpdateDate()){
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();


                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean t) {
                super.onPostExecute(t);
                if(t){
                    loadNewest(consumer);
                }


            }
        };

        asyncTask.execute();

    }

    @Override
    public void add(List<Segment> segmentList) {

    }

    @Override
    public void clearData() {
        ExecuteSQL.delete(SEGMENT_TYPE,this.getContent().getId());
    }



    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
