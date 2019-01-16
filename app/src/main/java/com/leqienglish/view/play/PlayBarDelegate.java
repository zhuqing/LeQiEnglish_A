package com.leqienglish.view.play;

public interface PlayBarDelegate {
    /**
     * 播放
     */
    public void play();

    /**
     * 播放下一个
     */
    public void playNext();

    /**
     * 播放前一个
     */
    public void playPrevious();

    /**
     * 暂停
     */
    public void stop();




    /**
     * 当前播放时间改变时调用

     */
    public void updateProgress(long newValue);
}
