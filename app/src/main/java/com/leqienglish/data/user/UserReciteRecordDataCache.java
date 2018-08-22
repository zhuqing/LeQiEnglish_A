package com.leqienglish.data.user;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.recite.UserReciteRecord;

import static com.leqienglish.database.Constants.USER_RECITE_RECORD_TYPE;


/**
 * 用户的背诵记录
 */
public class UserReciteRecordDataCache extends DataCacheAbstract<UserReciteRecord> {

    private static UserReciteRecordDataCache instance;

    private LQHandler.Consumer<UserReciteRecord> userReciteRecordConsumer;

    private UserReciteRecordDataCache() {

    }

    @Override
    public void load(LQHandler.Consumer<UserReciteRecord> consumer){
        this.userReciteRecordConsumer = consumer;
        super.load(consumer);
    }

    public static UserReciteRecordDataCache getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (UserReciteRecordDataCache.class) {
            if (instance == null) {
                instance = new UserReciteRecordDataCache();
            }
        }

        return instance;
    }

    @Override
    protected UserReciteRecord getFromCache() {
        User user = UserDataCache.getInstance().getCacheData();
        if(user == null){
            return null;
        }
        List<UserReciteRecord> users = ExecuteSQL.getDatasByType(USER_RECITE_RECORD_TYPE,user.getId(), UserReciteRecord.class);
        if(users == null || users.isEmpty()){
            return null;
        }
        return users.get(0);
    }

    @Override
    protected void putCache(UserReciteRecord userReciteRecord) {
        User user = UserDataCache.getInstance().getCacheData();
        if(user == null ){
            return;
        }
        ExecuteSQL.insertLearnE(Arrays.asList(userReciteRecord),user.getId(),USER_RECITE_RECORD_TYPE);
    }

    @Override
    protected UserReciteRecord getFromService() {
        User user = UserDataCache.getInstance().getCacheData();
        if(user == null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", user.getId());

        try {
            UserReciteRecord userReciteRecord = this.getRestClient().get("/userReciteRecord/findByUserId",param,UserReciteRecord.class);
           return userReciteRecord;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(UserReciteRecord userReciteRecord) {
        if(this.userReciteRecordConsumer!=null){
            this.userReciteRecordConsumer.accept(userReciteRecord);
        }

        this.setCacheData(userReciteRecord);
        this.putCache(userReciteRecord);

    }

    @Override
    public void remove(UserReciteRecord userReciteRecord) {

    }
}
