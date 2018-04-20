package com.leqienglish.entity;

/**
 * 播放记录
 * Created by zhuqing on 2018/4/20.
 */
public class PlayEntity {
    private String text;
    private long during;
    private long start;
    private long end;

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the during
     */
    public long getDuring() {
        return during;
    }

    /**
     * @param during the during to set
     */
    public void setDuring(long during) {
        this.during = during;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(long start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(long end) {
        this.end = end;
    }

}
