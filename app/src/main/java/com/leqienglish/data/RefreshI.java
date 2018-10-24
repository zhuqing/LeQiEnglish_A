package com.leqienglish.data;


import com.leqienglish.util.LQHandler;

public interface RefreshI {
    /**
     * 清除缓存中的数据并刷新
     * @param fininshed
     */
    public void clearAndRefresh(LQHandler.Consumer<Boolean> fininshed);

    /**
     * 刷新数据
     * @param fininshed
     */
    public void refresh(LQHandler.Consumer<Boolean> fininshed);
}
