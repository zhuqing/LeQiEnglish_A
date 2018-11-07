package com.leqienglish.data;

import java.util.HashMap;
import java.util.Map;

public class AppRefreshManager {
    private Map<String,RefreshI> refreshIMap = new HashMap<>();

    private static AppRefreshManager appRefreshManager;

    private AppRefreshManager(){

    }

    public static AppRefreshManager getInstance(){
        if(appRefreshManager != null){
            return appRefreshManager;
        }

        synchronized(AppRefreshManager.class){
            if(appRefreshManager != null){
                return appRefreshManager;
            }

            appRefreshManager = new AppRefreshManager();

            return appRefreshManager;
        }
    }

    /**
     * 注册
     * @param id
     * @param refreshI
     */
    public void regist(String id , RefreshI refreshI){
        refreshIMap.put(id,refreshI);
    }

    /**
     * 移除刷新借口
     * @param id
     */
    public void remove(String id){
        if(refreshIMap.containsKey(id)){
            refreshIMap.remove(id);
        }
    }


    /**
     * 刷新所有缓存
     */
    public void refreshAll(){
        for(RefreshI refreshI : refreshIMap.values()){
            refreshI.refresh(null);
        }
    }

    /**
     * 刷新所有缓存
     */
    public void clearAndRefreshAll(){
        for(RefreshI refreshI : refreshIMap.values()){
            refreshI.clearAndRefresh(null);
        }
    }
    /**
     * 根据Id刷新缓存
     * @param ids
     */
    public void refresh(String... ids){
        for (String id:ids) {
            if (refreshIMap.containsKey(id)) {
                refreshIMap.get(id).refresh(null);
            }
        }
    }

    /**
     * 根据Id刷新缓存
     * @param ids
     */
    public void clearnAndRefresh(String... ids){
        for (String id:ids) {
            if (refreshIMap.containsKey(id)) {
                refreshIMap.get(id).clearAndRefresh(null);
            }
        }
    }
}
