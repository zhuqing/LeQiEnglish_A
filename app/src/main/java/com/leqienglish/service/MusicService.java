package com.leqienglish.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.file.FileUtil;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {

    private static final LOGGER Log = new LOGGER(MusicService.class);
    private MediaPlayer player;

    /**
     * 当前播放的索引
     */
    private Integer currentPlayIndex;
    /**
     * 最大时间播放
     */
    private Integer maxDuration;

    /**
     * 要播放的数据
     */
    private List<SegmentPlayEntity> segmentPlayEntityList;

    public MusicService() {

    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    //该方法包含关于歌曲的操作
    public class MusicBinder extends Binder {

        private Runnable playCommplete;

        private LQHandler.Consumer<Integer> playSegmentIndexConsumer;



        public void setPlayCommplete(Runnable playCommplete){
            this.playCommplete = playCommplete;
        }

        public List<SegmentPlayEntity>  getSegmentPlayEntityList(){
            return segmentPlayEntityList;
        }

        public void setSegmentPlayEntityList(List<SegmentPlayEntity> segmentPlayEntityList){
            setSegmentPlayEntityList(0,segmentPlayEntityList);

        }

        public void setSegmentPlayEntityList(Integer playIndex , List<SegmentPlayEntity> segmentPlayEntityList){
            currentPlayIndex = playIndex;
            MusicService.this.segmentPlayEntityList = segmentPlayEntityList;
            MusicService.this.segmentPlayEntityList = segmentPlayEntityList;
            if(segmentPlayEntityList  == null || segmentPlayEntityList.isEmpty()){
                return;
            }


            try {
                play(segmentPlayEntityList.get(currentPlayIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void setSource(String path) throws IOException {
            if(player == null){
                player = new MediaPlayer();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playNext();
                    }
                });
            }

            Log.i("play\t"+path);
            player.reset();
            player.setDataSource(path);
            player.prepare();


        }

        public void play(Integer index,Integer seekTo){

            if(segmentPlayEntityList.size() <= index){
                return;
            }

            try {
                if(index != currentPlayIndex){
                    play(segmentPlayEntityList.get(index));
                    currentPlayIndex = index;
                }
                seekTo(seekTo);
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void play(Integer index){

            if(segmentPlayEntityList.size() <= index){
                return;
            }

            try {
                if(index != currentPlayIndex){
                    play(segmentPlayEntityList.get(index));
                    currentPlayIndex = index;
                }

                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void play(SegmentPlayEntity segmentPlayEntity) throws IOException {
            String path = segmentPlayEntity.getFilePath();
            if(path.contains(FileUtil.APP_NAME)){
                setSource(path);
                return;
            }

            LoadFile.loadFile(path, new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    try {
                        setSource(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void playNext(){
            if(currentPlayIndex == segmentPlayEntityList.size()-1){
                if(playCommplete != null){
                    playCommplete.run();
                }
                return;
            }

            currentPlayIndex += 1;

            if(this.playSegmentIndexConsumer !=null){
                this.playSegmentIndexConsumer.accept(currentPlayIndex);
            }
            try {
                play(segmentPlayEntityList.get(currentPlayIndex));
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public Integer getCurrentPlayIndex(){
            return currentPlayIndex;
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
            if(player == null){
                return 0;
            }
            return player.getDuration();
        }

        public SegmentPlayEntity getCurrentSegmentPlayEntity(){
            if(currentPlayIndex == null){
                return null;
            }

            if(segmentPlayEntityList == null){
                return  null;
            }

            if(currentPlayIndex >= segmentPlayEntityList.size()){
                return null;
            }
            return segmentPlayEntityList.get(currentPlayIndex);
        }

        //返回歌曲目前的进度，单位为毫秒
        public int getCurrenPostion(){
            if(player == null){
                return 0;
            }
            return player.getCurrentPosition();
        }

        //设置歌曲播放的进度，单位为毫秒
        public  void seekTo(int mesc){
            if(player == null){
                return;
            }
            player.seekTo(mesc);
        }

        public void setPlaySegmentIndexConsumer(LQHandler.Consumer<Integer> playSegmentIndexConsumer) {
            this.playSegmentIndexConsumer = playSegmentIndexConsumer;
        }
    }

}
