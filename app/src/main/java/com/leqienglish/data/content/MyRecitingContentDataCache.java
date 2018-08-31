package com.leqienglish.data.content;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.content.ReciteContentVO;
import xyz.tobebetter.entity.user.User;

import static com.leqienglish.database.Constants.MY_RECITING_ARITCLE_TYPE;


/**
 * 正在背诵的文章的缓存
 */
public class MyRecitingContentDataCache extends DataCacheAbstract<List<ReciteContentVO>> {

    private LOGGER logger = new LOGGER(MyRecitingContentDataCache.class);

    private static MyRecitingContentDataCache myRecitingContentDataCache;

    private LQHandler.Consumer<List<ReciteContentVO>> consumer;



    private MyRecitingContentDataCache(){

    }


    @Override
    public void load(LQHandler.Consumer<List<ReciteContentVO>> consumer) {
        this.consumer = consumer;
        super.load(consumer);
    }

    public  static MyRecitingContentDataCache getInstance(){
        if(myRecitingContentDataCache !=null){
            return myRecitingContentDataCache;
        }
        synchronized (MyRecitingContentDataCache.class){
            if(myRecitingContentDataCache == null){
                myRecitingContentDataCache = new MyRecitingContentDataCache();
            }
        }

        return myRecitingContentDataCache;
    }

    @Override
    protected List<ReciteContentVO> getFromCache() {
        User user = UserDataCache.getInstance().getCacheData();
        if(user == null){
            return null;
        }
        List<ReciteContentVO> contents = ExecuteSQL.getDatasByType(MY_RECITING_ARITCLE_TYPE,user.getId(),ReciteContentVO.class);

        return contents;
    }

    @Override
    protected void putCache(List<ReciteContentVO> constantsList) {
        User user = UserDataCache.getInstance().getCacheData();
        if(user == null){
            return;
        }
        ExecuteSQL.delete(MY_RECITING_ARITCLE_TYPE,user.getId());
        ExecuteSQL.insertLearnE(constantsList,user.getId(),MY_RECITING_ARITCLE_TYPE);
    }

    @Override
    protected List<ReciteContentVO> getFromService() {
        User user = UserDataCache.getInstance().getCacheData();

        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", user.getId());

        logger.d("param="+param);

        try {
            ReciteContentVO[] contents = this.getRestClient().get("/english/content/findUserReciting",param,ReciteContentVO[].class);
            return new ArrayList<>(Arrays.asList(contents));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void add(List<ReciteContentVO> reciteContentVOS) {
        if(this.getCacheData()!=null){
            this.getCacheData().addAll(reciteContentVOS);
        }else{
            this.setCacheData(reciteContentVOS);
        }

        if(consumer != null){
            this.consumer.accept(this.getCacheData());
        }
    }

    /**
     * 已有的添加的背诵
     * @param content
     * @return
     */
    public boolean contains(Content content){
        if(content == null){
            return false;
        }
        if(this.getCacheData() == null){
            return false;
        }
        for(ReciteContentVO reciteContentVO : this.getCacheData()){
            if(reciteContentVO.getId().equals(content.getId())){
                return true;
            }
        }

        return false;
    }

    @Override
    public void remove(List<ReciteContentVO> reciteContentVOS) {

    }


    public void removeByContentId(String contentId) {
        if(this.getCacheData() == null || this.getCacheData().isEmpty()){
            return;
        }

        ReciteContentVO needRemove = null;
        for(ReciteContentVO reciteContentVO : this.getCacheData()){
            if(reciteContentVO.getId().equals(contentId)){
                needRemove = reciteContentVO;
                break;
            }
        }
        if(needRemove == null){
            return;
        }

        this.getCacheData().remove(needRemove);

        ExecuteSQL.deleteById(needRemove.getId());

        if(consumer !=null){
            consumer.accept(this.getCacheData());
        }
    }
}
