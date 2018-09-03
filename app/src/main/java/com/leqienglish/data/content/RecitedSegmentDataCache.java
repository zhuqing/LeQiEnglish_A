package com.leqienglish.data.content;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.word.user.UserAndSegment;

import static com.leqienglish.database.Constants.RECITED_SEGMENT_IN_CONTENT;

public class RecitedSegmentDataCache extends DataCacheAbstract<List<UserAndSegment>> {

    private Content content;

    private LQHandler.Consumer consumer;

    private static Map<String,RecitedSegmentDataCache> cache = new HashMap<>();

    private RecitedSegmentDataCache(Content content){
        this.content = content;
    }

    public static RecitedSegmentDataCache getInstance(Content content){
        if(cache.containsKey(content.getId())){
            return cache.get(content.getId());
        }
        cache.put(content.getId(),new RecitedSegmentDataCache(content));

        return cache.get(content.getId());
    }

    @Override
    public void load(LQHandler.Consumer<List<UserAndSegment>> consumer) {
        super.load(consumer);
        this.consumer = consumer;
    }

    @Override
    protected List<UserAndSegment> getFromCache() {
        List<UserAndSegment> userAndSegments = ExecuteSQL.getDatasByType(RECITED_SEGMENT_IN_CONTENT,getParentId(),UserAndSegment.class);
        if(userAndSegments==null||userAndSegments.isEmpty()){
            return null;
        }

        return userAndSegments;
    }

    @Override
    protected void putCache(List<UserAndSegment> userAndSegments) {
        ExecuteSQL.insertLearnE(userAndSegments,getParentId(),RECITED_SEGMENT_IN_CONTENT);
    }

    private String getParentId(){
        if(UserDataCache.getInstance().getUser()==null||content == null){
            return "";
        }

        return UserDataCache.getInstance().getUser().getId()+"-"+content.getId();
    }

    @Override
    protected List<UserAndSegment> getFromService() {
        if(UserDataCache.getInstance().getUser()==null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("contentId", content.getId());
        param.add("userId", UserDataCache.getInstance().getUser().getId());

        try {
            UserAndSegment[] userAndSegments = this.getRestClient().get("/userAndSegment/findByContentIdAndUserId",param,UserAndSegment[].class);

            if(userAndSegments == null || userAndSegments.length == 0){
                return null;
            }
            return new ArrayList<>(Arrays.asList(userAndSegments));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(List<UserAndSegment> userAndSegments) {
        if(this.getCacheData() == null){
            this.setCacheData(new ArrayList<>());
        }

        this.getCacheData().addAll(userAndSegments);
        this.putCache(userAndSegments);

        if(consumer!=null){
            consumer.accept(this.getCacheData());
        }
    }

    @Override
    public void remove(List<UserAndSegment> userAndSegments) {

    }
}
