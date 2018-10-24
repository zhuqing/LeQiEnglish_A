package com.leqienglish.data.word;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.word.ReciteWordConfig;

public class MyReciteWordReConfigDataCache extends DataCacheAbstract<ReciteWordConfig> {
    private static final String DATA_TYPE = "MyWordConfigDataCache";

    private static MyReciteWordReConfigDataCache MyReciteWordReConfigDataCache;

    private MyReciteWordReConfigDataCache() {

    }


    public static MyReciteWordReConfigDataCache getInstance() {
        if (MyReciteWordReConfigDataCache != null) {
            return MyReciteWordReConfigDataCache;
        }
        synchronized (MyWordDataCache.class) {
            if (MyReciteWordReConfigDataCache == null) {
                MyReciteWordReConfigDataCache = new MyReciteWordReConfigDataCache();
            }
        }

        return MyReciteWordReConfigDataCache;
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
