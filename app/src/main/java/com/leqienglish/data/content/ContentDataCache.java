package com.leqienglish.data.content;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.recite.UserReciteRecord;

import static com.leqienglish.database.Constants.CONTENT_TYPE;

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
    protected boolean shouldUpdate(Content content) {
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("id", content.getId());
        param.add("updateTime",content.getUpdateDate().toString());

        try {
            Boolean shouldUpdate = this.getRestClient().get("/english/content/shouldUpdate",param,Boolean.class);
            return shouldUpdate;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void add(Content content) {

    }

    @Override
    public void remove(Content content) {

    }
}
