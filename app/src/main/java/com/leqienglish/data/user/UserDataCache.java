package com.leqienglish.data.user;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.user.User;

import static com.leqienglish.database.Constants.CURRENT_USER_TYPE;

/**
 * 用户数据的缓存
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
            return this.getCacheData();
        }

        return this.findOrCreateUser();
    }

    public String getUserName(){
        User user = this.getUser();
        if(user == null || user.getName()== null){
            return "User";
        }
        return user.getName();
    }

    /**
     * 获取用户的Id
     *
     * @return
     */
    public String getUserId() {
        User user = this.getUser();
        if (user == null) {
            return "";
        }

        return user.getId();
    }

    @Override
    protected User getFromCache() {
        return this.findOrCreateUser();
    }

    @Override
    protected void putCache(User user) {
        this.clearData();

        ExecuteSQL.insertLearnE(Arrays.asList(user), null, CURRENT_USER_TYPE);
    }

    @Override
    protected boolean needUpdate() {
        return false;
    }

    @Override
    protected String getUpdateTimeType() {
        return "UserDataCache_update";
    }

    @Override
    protected User getFromService() {
        if (this.getCacheData() == null) {
            return null;
        }
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("id", this.getCacheData().getId());


        try {
            User user = this.getRestClient().get("/user/findById", param, User.class);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    private User save(User user) {
        try {
            user = this.getRestClient().post("/user/create", this.getCacheData(), null, User.class);
            this.add(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findByOtherSysId(String otherSysId) {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("otherSysId", otherSysId);

        try {
            User user = this.getRestClient().get("/user/findUserByOtherSysId", param, User.class);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void regist(final User user, LQHandler.Consumer<User> consumer) {
        AsyncTask asyncTask = new AsyncTask<Object, Object, User>() {
            @Override
            protected User doInBackground(Object[] objects) {

                try {


                    return getRestClient().post("/user/regist", user, null, User.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(User t) {
                super.onPostExecute(t);
                 add(t);
                if (consumer != null) {
                    consumer.accept(t);
                }

            }

        };

        asyncTask.execute();


    }

    @Override
    public void add(User user) {

        this.putCache(user);
        this.setCacheData(user);
    }

    @Override
    public void clearData() {
        this.setCacheData(null);
        ExecuteSQL.delete(CURRENT_USER_TYPE);
    }


    public boolean login(String name, String password) {
        return false;
    }

    /**
     * 检验当前有没有用户登陆,没有时创建，注册时才保存到服务器
     */
    private User findOrCreateUser() {

        List<User> users = ExecuteSQL.getDatasByType(CURRENT_USER_TYPE, User.class);

        if (users != null && !users.isEmpty()) {
            return users.get(0);
        }

        try {
            User user = createTempUser();
            this.putCache(user);
            //save(user);
            return user;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;


    }

    //创建临时用户
    private User createTempUser() throws JsonProcessingException {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName("Friend");
        user.setCreateDate(System.currentTimeMillis());
        user.setSex(0);
        user.setStatus(Consistent.UN_SAVED_STATUS);
        return user;
    }
}
