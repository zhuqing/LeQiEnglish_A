package com.leqienglish.data.user;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.TaskUtil;

import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.UserHearted;

public class UserHeartedDataCache extends DataCacheAbstract<UserHearted> {

    private String id;

    public final static String DATA_TYPE = "UserHeartedDataCache";

    public UserHeartedDataCache(String id){
        this.id = id;
    }

    @Override
    protected String getUpdateTimeType() {
        return null;
    }

    @Override
    protected UserHearted getFromCache() {
        User user = UserDataCache.getInstance().getCacheData();
        if(user == null){
            return null;
        }
        List<UserHearted> users = ExecuteSQL.getDatasByType(DATA_TYPE,getParentId(), UserHearted.class);
        if(users == null || users.isEmpty()){
            return null;
        }
        return users.get(0);
    }

    private String getParentId(){
       return UserDataCache.getInstance().getUserId()+"_"+id;
    }

    @Override
    protected void putCache(UserHearted userHearted) {
        String parentId = getParentId();
        this.clearData();
        ExecuteSQL.insertLearnE(Arrays.asList(userHearted),parentId,DATA_TYPE);
    }

    @Override
    protected UserHearted getFromService() {
        String userId = UserDataCache.getInstance().getUserId();
        MultiValueMap<String,String> param = this.getMutilValueMap();
        param.add("userId",userId);
        param.add("targetId",this.id);
        try {
            UserHearted[] userHearteds = getRestClient().get("/userHearted/findByUserIdAndTargetId",param,UserHearted[].class);

            if(userHearteds == null || userHearteds.length == 0){
                return null;
            }

            return userHearteds[0];

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void commit(int type){
        String userId = UserDataCache.getInstance().getUserId();

        TaskUtil.run(new Runnable() {
            @Override
            public void run() {
                UserHearted userHearted = new UserHearted();
                userHearted.setTargetId(id);
                userHearted.setType(type);
                userHearted.setUserId(userId);

                MultiValueMap<String,String> param = getMutilValueMap();
                param.add("userId",userId);
                param.add("id",id);

                try {
                   getRestClient().put(getPath(type),null,param,null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public String getPath(int type){
                switch (type){
                    case Consistent.CONTENT_TYPE_CONTENT:
                      return   "/english/content/awesome";
                    case Consistent.CONTENT_TYPE_SEGMENT:
                        return   "/segment/awesome";

                }

                return "";
            }
        });


    }

    @Override
    public void add(UserHearted userHearted) {

    }

    @Override
    public void clearData() {
        super.clearData();
        String parentId = getParentId();
        setCacheData(null);
        ExecuteSQL.delete(DATA_TYPE,parentId);
    }
}
