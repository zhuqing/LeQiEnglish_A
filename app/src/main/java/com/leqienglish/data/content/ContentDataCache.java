package com.leqienglish.data.content;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.TaskUtil;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.Content;

import static com.leqienglish.database.Constants.CONTENT_TYPE;

/**
 * Content缓存
 */
public class ContentDataCache extends DataCacheAbstract<Content> {
    private String contentId;

    public ContentDataCache(String contentId){
        this.contentId = contentId;
    }
    @Override
    protected Content getFromCache() {
        if(this.contentId == null){
            return null;
        }
       List<Content> contentList = ExecuteSQL.getDatasByType(CONTENT_TYPE,contentId,Content.class);
        if(contentList == null || contentList.isEmpty()){
            return null;
        }
        return contentList.get(0);
    }

    @Override
    protected void putCache(Content content) {

        if(this.contentId == null){
            return;
        }

        this.clearData();
        ExecuteSQL.insertLearnE(Arrays.asList(content),contentId,CONTENT_TYPE);
    }

    @Override
    protected Content getFromService() {
        if(contentId == null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("id", contentId);

        try {
            Content content = this.getRestClient().get("/english/content/findById",param,Content.class);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected boolean needUpdate() {


        return true;
    }

    public static void update(String contentId){

        TaskUtil.run(new Runnable() {
            @Override
            public void run() {
                ContentDataCache contentDataCache = new ContentDataCache(contentId);
                Content content = contentDataCache.getFromService();
                if(content == null){
                    return;
                }

                contentDataCache.putCache(content);
            }
        });
    }

    @Override
    protected String getUpdateTimeType() {
        return "ContentDataCache_update";
    }

    @Override
    public void add(Content content) {
        if(content == null){
            return ;
        }
        ExecuteSQL.deleteById(contentId);
        MyContentDataCache.getInstance().putCache(Arrays.asList(content));
        ExecuteSQL.insertLearnE(Arrays.asList(content),content.getId(),CONTENT_TYPE);
    }

    @Override
    public void clearData() {
        ExecuteSQL.deleteById(contentId);
    }


}
