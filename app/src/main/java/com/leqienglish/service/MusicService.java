package com.leqienglish.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

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

    private static final int UPDATE_PROGRESS = 0;


    /**
     * 当前播放的索引
     */
    private Integer currentPlayIndex = -1;
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

        private MusicBinderI musicBinderI;

        //使用handler定时更新进度条
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_PROGRESS:
                        if (player == null) {
                            return;
                        }
                        if (player != null || player.isPlaying()) {
                            handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 200);
                        }

                        if (musicBinderI != null) {
                            musicBinderI.currentTimeChange(currentPlayIndex,player.getCurrentPosition());
                        }
                        break;
                }
            }
        };


        /**
         * 设置播放位置和播放文件的位置
         *
         * @param index
         * @param seekTo
         */
        public void play(Integer index, Integer seekTo) {

            if (index >= segmentPlayEntityList.size() || index < 0) {
                return;
            }

            try {
                if (index != currentPlayIndex) {
                    setSource(segmentPlayEntityList.get(index));
                    currentPlayIndex = index;
                }
                seekTo(seekTo);
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 播放资源index的数据
         *
         * @param index
         */
        public void play(Integer index) {

            if (segmentPlayEntityList == null || segmentPlayEntityList.size() <= index) {
                return;
            }

            try {

                setSource(segmentPlayEntityList.get(index));
                currentPlayIndex = index;
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 设置播放资源
         *
         * @param segmentPlayEntity
         * @throws IOException
         */
        private void setSource(SegmentPlayEntity segmentPlayEntity) throws IOException {
            String path = segmentPlayEntity.getFilePath();
            if (path.contains(FileUtil.APP_NAME)) {
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


        /**
         * 设置播放资源，重置播放器
         *
         * @param path
         * @throws IOException
         */
        private void setSource(String path) throws IOException {
            if (player == null) {
                player = new MediaPlayer();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playNext();
                    }
                });


            }

            Log.i("play\t" + path);
            player.reset();
            player.setDataSource(path);
            player.prepare();

        }

        /**
         * 播放上一个数据
         */
        public void playProvious() {
            if (currentPlayIndex == 0) {
                return;
            }
            currentPlayIndex -= 1;

            if (this.musicBinderI != null) {
                musicBinderI.currentPlayIndexChange(currentPlayIndex);
            }

            this.play(currentPlayIndex);
        }

        /**
         * 播放下一个数据
         */
        public void playNext() {


            if (currentPlayIndex == segmentPlayEntityList.size() - 1) {
                if (this.musicBinderI != null) {
                    musicBinderI.finished();
                }
                return;
            }

            currentPlayIndex += 1;

            if (this.musicBinderI != null) {
                musicBinderI.currentPlayIndexChange(currentPlayIndex);
            }

            this.play(currentPlayIndex);


        }

        //获取当前播放的数据的位置
        public Integer getCurrentPlayIndex() {
            return currentPlayIndex;
        }

        //判断是否处于播放状态
        public boolean isPlaying() {
            if (player == null) {
                return false;
            }
            return player.isPlaying();
        }

        public void pause() {
            if (player == null) {
                return;
            }
            if (player.isPlaying()) {
                player.pause();
                handler.removeMessages(UPDATE_PROGRESS);
            }
        }

        //播放或暂停歌曲
        public void play() {

            if (player == null) {
                return;
            }
            if (!player.isPlaying()) {
                player.start();
                handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 200);
            }
        }

        //返回歌曲的长度，单位为毫秒
        public int getDuration() {
            if (player == null) {
                return 0;
            }
            return player.getDuration();
        }

        //获取当前播放的数据
        public SegmentPlayEntity getCurrentSegmentPlayEntity() {
            if (currentPlayIndex == null) {
                return null;
            }

            if (segmentPlayEntityList == null) {
                return null;
            }

            if (currentPlayIndex >= segmentPlayEntityList.size()) {
                return null;
            }
            return segmentPlayEntityList.get(currentPlayIndex);
        }

        //返回歌曲目前的进度，单位为毫秒
        public int getCurrenPostion() {
            if (player == null) {
                return 0;
            }
            return player.getCurrentPosition();
        }

        //设置歌曲播放的进度，单位为毫秒
        public void seekTo(int mesc) {
            if (player == null) {
                return;
            }
            player.seekTo(mesc);
        }


        public MusicBinderI getMusicBinderI() {
            return musicBinderI;
        }

        public void setMusicBinderI(MusicBinderI musicBinderI) {
            this.musicBinderI = musicBinderI;
        }


        /**
         * 设置播放数据并设置播放的位置
         * 功能太过复杂，设置数据就设置数据 ， 播放就播放
         *
         * @param segmentPlayEntityList
         */
        public void setSegmentPlayEntityList(List<SegmentPlayEntity> segmentPlayEntityList) {
            MusicService.this.segmentPlayEntityList = segmentPlayEntityList;

        }

        public List<SegmentPlayEntity> getSegmentPlayEntityList() {
            return segmentPlayEntityList;
        }
    }

    public interface MusicBinderI {

        /**
         * 播放的当前时间改变
         *
         * @param currentTime
         */
        public void currentTimeChange(int index , int currentTime);

        /**
         * 播放完成
         */
        public void finished();

        /**
         * 当前播放数据的索引改变
         *
         * @param playIndex
         */
        public void currentPlayIndexChange(int playIndex);
    }

}
