package com.leqienglish.data.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leqienglish.R;
import com.leqienglish.data.DataCacheAbstract;

import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.sf.RestClient;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.user.User;

import static com.leqienglish.database.Constants.CURRENT_USER_TYPE;

/**
 * 加载用户，或者默认创建一个临时用户。
 */
public class UserDataCache extends DataCacheAbstract<User> {

    private static UserDataCache userDataCache;

    private UserDataCache() {

    }


    public static UserDataCache getInstance() {
        if (userDataCache != null) {
            return userDataCache;
        }
        synchronized (UserDataCache.class) {
            if (userDataCache == null) {
                userDataCache = new UserDataCache();
            }
        }

        return userDataCache;
    }

    public User getUser() {
        if (this.getCacheData() != null) {
            return this.findOrCreateUser();
        }

        return this.getCacheData();
    }

    @Override
    protected User getFromCache() {
        return this.findOrCreateUser();
    }

    @Override
    protected void putCache(User user) {
        ExecuteSQL.delete(CURRENT_USER_TYPE);
        ExecuteSQL.insertLearnE(Arrays.asList(user), null, CURRENT_USER_TYPE);
    }

    @Override
    protected User getFromService() {
        if(this.getCacheData() == null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", this.getCacheData().getId());


        try {
            User user = this.getRestClient().get("/user/findById", param, User.class);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    private User save(User user){
        try {
             user = this.getRestClient().post("/user/create", this.getCacheData(), null, User.class);
            this.add(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void add(User user) {
        this.setCacheData(user);
        this.putCache(user);
    }

    @Override
    public void remove(User user) {

    }

    public boolean login(String name, String password) {
        return false;
    }

    /**
     * 检验当前有没有用户登陆
     */
    private User findOrCreateUser() {

        List<User> users = ExecuteSQL.getDatasByType(CURRENT_USER_TYPE, User.class);

        if (users != null && !users.isEmpty()) {
            return users.get(0);
        }

        try {
            User user = addTempUser();
            this.putCache(user);
            save(user);
            return user;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;


    }

    private User addTempUser() throws JsonProcessingException {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName("Mine");
        user.setSex(0);
        user.setStatus(Consistent.UN_SAVED_STATUS);
        return user;
    }
}
