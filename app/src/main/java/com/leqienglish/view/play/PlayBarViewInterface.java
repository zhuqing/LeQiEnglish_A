package com.leqienglish.view.play;

public interface PlayBarViewInterface {
    public void setPlayBarDelegate(PlayBarDelegate playBarI);
    public void updateProgress(int progress,int max);
    public void play();
    public void stop();
}
