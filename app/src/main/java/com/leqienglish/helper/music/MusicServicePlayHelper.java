package com.leqienglish.helper.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.leqienglish.service.music.MusicService;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.play.PlayBarDelegate;
import com.leqienglish.view.play.PlayBarViewInterface;

import static android.content.Context.BIND_AUTO_CREATE;

public class MusicServicePlayHelper {
    private MusicService.MusicBinder musicBinder;
    private Context context;

    private MusicService.MusicBinderDelegate musicBinderDelegate;

    private PlayBarDelegate playBarDelegate;

    private PlayBarViewInterface playBarView;

    private MyConnection conn;

    private LQHandler.Consumer<MusicService.MusicBinder> musicBinderConsumer;

    public MusicServicePlayHelper(Context context ){
        this.context = context;
    }

    public void initService(){
        conn = new MusicServicePlayHelper.MyConnection();
        //使用混合的方法开启服务，
        Intent intent3 = new Intent(this.getContext(), MusicService.class);
        getContext().startService(intent3);
        startBind();
    }

    /**
     * 建立与服务端的绑定
     */
    public void startBind(){

        Intent intent3 = new Intent(this.getContext(), MusicService.class);
        getContext().bindService(intent3, conn, BIND_AUTO_CREATE);

        if(musicBinder != null ){
            if(playBarView == null){
                return;
            }
            if(musicBinder.isPlaying()){
                playBarView.play();
            }else{
                playBarView.stop();
            }

        }
    }

    /**
     * 解除与服务端的绑定
     */
    public void unbind(){
        try{
            getContext().unbindService(conn);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MusicService.MusicBinderDelegate getMusicBinderDelegate() {
        return musicBinderDelegate;
    }

    public void setMusicBinderDelegate(MusicService.MusicBinderDelegate musicBinderDelegate) {
        this.musicBinderDelegate = musicBinderDelegate;
    }

    public PlayBarDelegate getPlayBarDelegate() {
        return playBarDelegate;
    }

    public void setPlayBarDelegate(PlayBarDelegate playBarDelegate) {
        this.playBarDelegate = playBarDelegate;
    }

    public PlayBarViewInterface getPlayBarView() {
        return playBarView;
    }

    public void setPlayBarView(PlayBarViewInterface playBarView) {
        this.playBarView = playBarView;
    }

    public MusicService.MusicBinder getMusicBinder() {
        return musicBinder;
    }

    public void setMusicBinder(MusicService.MusicBinder musicBinder) {
        this.musicBinder = musicBinder;
    }

    public LQHandler.Consumer<MusicService.MusicBinder> getMusicBinderConsumer() {
        return musicBinderConsumer;
    }

    public void setMusicBinderConsumer(LQHandler.Consumer<MusicService.MusicBinder> musicBinderConsumer) {
        this.musicBinderConsumer = musicBinderConsumer;
    }


    /**
     * 与服务端的连接器
     */
    private class MyConnection implements ServiceConnection {

        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获得service中的MyBinder
            musicBinder = (MusicService.MusicBinder) service;

            if(musicBinderConsumer != null){
                musicBinderConsumer.accept(musicBinder);
            }

            playBarView.setPlayBarDelegate(playBarDelegate);

            musicBinder.setMusicBinderDelegate(musicBinderDelegate);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
