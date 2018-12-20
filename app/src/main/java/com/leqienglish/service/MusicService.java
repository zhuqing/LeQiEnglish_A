package com.leqienglish.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.leqienglish.util.LOGGER;

import java.io.IOException;

public class MusicService extends Service {

    private static final LOGGER Log = new LOGGER(MusicService.class);
    private MediaPlayer player;


    public MusicService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    //该方法包含关于歌曲的操作
    public class MusicBinder extends Binder {

        private Runnable playCommplete;

        public void setPlayCommplete(Runnable playCommplete){
            this.playCommplete = playCommplete;
        }

        public void setSource(String path) throws IOException {
            if(player == null){
                player = new MediaPlayer();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(playCommplete != null){
                            playCommplete.run();
                        }
                    }
                });
            }

            Log.i("play\t"+path);
            player.reset();
            player.setDataSource(path);
            player.prepare();
        }

        //判断是否处于播放状态
        public boolean isPlaying(){
            if(player == null){
                return false;
            }
            return player.isPlaying();
        }

        public void pause(){
            if(player == null){
                return ;
            }
            if (player.isPlaying()) {
                player.pause();
            }
        }

        //播放或暂停歌曲
        public void play() {
            if(player == null){
                return ;
            }
            if (!player.isPlaying()) {
                player.start();
            }
        }

        //返回歌曲的长度，单位为毫秒
        public int getDuration(){
            return player.getDuration();
        }

        //返回歌曲目前的进度，单位为毫秒
        public int getCurrenPostion(){
            return player.getCurrentPosition();
        }

        //设置歌曲播放的进度，单位为毫秒
        public void seekTo(int mesc){
            player.seekTo(mesc);
        }
    }

}
