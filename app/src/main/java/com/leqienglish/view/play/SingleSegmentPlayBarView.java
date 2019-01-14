//package com.leqienglish.view.play;
//
//import android.content.Context;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//
//public class SingleSegmentPlayBarView extends PlayBarView {
//    public SingleSegmentPlayBarView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    /**
//     * 按指定索引播放
//     * @param segmentIndex
//     */
//    public void play(int segmentIndex){
//        if(this.musicControl!=null){
//            this.musicControl.play(segmentIndex);
//        }
//    }
//
//    protected void updateSeekbar(int position){
//        seekBar.setProgress( position);
//    }
//
//
//    protected void resetPlayedSegmentPlayEntity(int position) {
//        if(musicControl == null){
//            return;
//        }
//        musicControl.pause();
//        musicControl.seekTo(position);
//        musicControl.play();
//
//
//    }
//
//
//
//
//
//}
