package com.leqienglish.data.user;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;

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

    private UserReciteRecordDataCache() {

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

        List<UserReciteRecord> users = ExecuteSQL.getDatasByType(USER_RECITE_RECORD_TYPE, null, UserReciteRecord.class);
        if(users.isEmpty()){
            return null;
        }
        return users.get(0);
    }

    @Override
    protected void putCache(UserReciteRecord userReciteRecord) {
        User user = UserDataCache.getInstance().getCacheData();
        ExecuteSQL.insertLearnE(Arrays.asList(userReciteRecord),user.getId(),USER_RECITE_RECORD_TYPE);
    }

    @Override
    protected UserReciteRecord getFromService() {
        User user = UserDataCache.getInstance().getCacheData();
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
}
