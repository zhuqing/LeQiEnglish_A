package com.leqienglish.data.word;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.TaskUtil;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.word.ReciteWordConfig;

public class MyReciteWordConfigDataCache extends DataCacheAbstract<ReciteWordConfig> {
    private static final String DATA_TYPE = "MyWordConfigDataCache";

    private static MyReciteWordConfigDataCache myReciteWordConfigDataCache;

    private MyReciteWordConfigDataCache() {

    }


    public static MyReciteWordConfigDataCache getInstance() {
        if (myReciteWordConfigDataCache != null) {
            return myReciteWordConfigDataCache;
        }
        synchronized (MyWordDataCache.class) {
            if (myReciteWordConfigDataCache == null) {
                myReciteWordConfigDataCache = new MyReciteWordConfigDataCache();
            }
        }

        return myReciteWordConfigDataCache;
    }

    @Override
    protected String getUpdateTimeType() {
        return "MyWordConfigDataCache_update";
    }

    @Override
    protected ReciteWordConfig getFromCache() {


        List<ReciteWordConfig> datas = ExecuteSQL.getDatasByType(DATA_TYPE, UserDataCache.getInstance().getUserId(),ReciteWordConfig.class);
        if(datas == null|| datas.isEmpty()){
            return null;
        }
        return datas.get(0);
    }

    /**
     *
     * @param reciteWordConfig
     */
    public void update(ReciteWordConfig reciteWordConfig){
        TaskUtil.run(()->{
            try {
                ReciteWordConfig reciteWordConfig1 = this.getFromCache();
                if(reciteWordConfig.equals(reciteWordConfig1)){
                    return;
                }
                this.getRestClient().put("reciteWordConfig/update",reciteWordConfig,null,ReciteWordConfig.class);
                this.clearData();
                this.setCacheData(reciteWordConfig);
                this.putCache(reciteWordConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 获取每天背诵的单词的数量
     * @return
     */
    public Integer getReciteNumberPerDay(){
        ReciteWordConfig reciteWordConfig = this.getCacheData();
        if(reciteWordConfig == null || reciteWordConfig.getReciteNumberPerDay() == null){
            return 10;
        }
        return reciteWordConfig.getReciteNumberPerDay();
    }

    @Override
    protected void putCache(ReciteWordConfig reciteWordConfig) {
        if(reciteWordConfig == null){
            return;
        }
        ExecuteSQL.insertLearnE(Arrays.asList(reciteWordConfig),UserDataCache.getInstance().getUserId(),DATA_TYPE);
    }

    @Override
    protected ReciteWordConfig getFromService() {
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", UserDataCache.getInstance().getUserId());

        try {
            ReciteWordConfig reciteWordConfig = this.getRestClient().get("reciteWordConfig/findByUserId",param,ReciteWordConfig.class);
            return reciteWordConfig;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(ReciteWordConfig reciteWordConfig) {

    }

    @Override
    public void clearData() {
        ExecuteSQL.delete(DATA_TYPE,UserDataCache.getInstance().getUserId());
    }
}
