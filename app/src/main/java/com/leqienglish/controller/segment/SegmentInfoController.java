package com.leqienglish.controller.segment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.segment.SegmentWordsActivity;
import com.leqienglish.activity.word.WordInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.content.RecitedSegmentDataCache;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.user.UserReciteRecordDataCache;
import com.leqienglish.data.word.WordInfoDataCache;
import com.leqienglish.entity.SegmentPlayEntity;
import com.leqienglish.helper.heart.SegmentHeartHelper;
import com.leqienglish.helper.music.MusicServicePlayHelper;
import com.leqienglish.pop.actionsheet.ActionSheet;
import com.leqienglish.service.music.MusicService;
import com.leqienglish.sf.LQService;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.SharePlatform;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.view.operation.OperationBar;
import com.leqienglish.view.play.PlayBarDelegate;
import com.leqienglish.view.play.floatplay.FloatPlayView;
import com.leqienglish.view.play.playpane.PlayerPaneView;
import com.leqienglish.view.word.WordInfoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;
import xyz.tobebetter.entity.english.word.user.UserAndSegment;
import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.recite.UserReciteRecord;
import xyz.tobebetter.entity.word.Word;

public class SegmentInfoController extends ControllerAbstract {
    private LOGGER LOG = new LOGGER(SegmentInfoController.class);
    private Segment segment;
    private String filePath;

    List<AudioPlayPoint> playEntities = null;
    List<View> views;

    private LinearLayout frameLayout;


    private OperationBar operationBar;

    private PlayerPaneView paneView;

    private View lastView;

    /**
     * 浮动播放控件
     */
    private FloatPlayView floatPlayView;



    private int minutes;

    private long during;

    private SegmentHeartHelper segmentHeartHelper;


    private MusicServicePlayHelper musicServicePlayHelper;


    public SegmentInfoController(View view, Segment segment, String path) {
        super(view);
        this.segment = segment;
        this.filePath = path;


    }

    @Override
    public void init() {
        this.frameLayout = this.getView().findViewById(R.id.play_audio_layout);
        this.paneView = new PlayerPaneView(this.getView().getContext(), null);
        this.operationBar = (OperationBar) this.findViewById(R.id.play_audio_operationBar);
        this.segmentHeartHelper = new SegmentHeartHelper(operationBar,segment);
        this.views = new ArrayList<>();

        loadData();

        initFloatPlayView();
        saveWord(segment, UserDataCache.getInstance().getUser());
        this.initListener();

        segmentHeartHelper.updateHearted(false);

        this.initMusicServicePlayHelper();

    }

    private void initMusicServicePlayHelper(){
        musicServicePlayHelper = new MusicServicePlayHelper(this.getContext());
        musicServicePlayHelper.setPlayBarDelegate(new PlayBarImpl());
        musicServicePlayHelper.setMusicBinderDelegate(new MusicBinderImpl());
        musicServicePlayHelper.setPlayBarView(this.floatPlayView);
        musicServicePlayHelper.setMusicBinderConsumer(new LQHandler.Consumer<MusicService.MusicBinder>() {
            @Override
            public void accept(MusicService.MusicBinder musicBinder) {
                if(segment == null){
                    return;
                }

                musicBinder.setSegmentPlayEntityList(Arrays.asList(SegmentPlayEntity.toSegmentPlayEntity(segment)));
            }
        });
        musicServicePlayHelper.initService();
    }


    @Override
    public void onResume() {
        musicServicePlayHelper.startBind();
//        if(musicServicePlayHelper.getMusicBinder()!=null){
//            musicServicePlayHelper.getMusicBinder().play();
//        }
    }

    /**
     * 初始化浮动播放按钮
     */
    private void initFloatPlayView(){
        floatPlayView = new FloatPlayView(this.getContext(),null);
        this.addPopView(floatPlayView,200,200);
        this.floatPlayView.setPlayBarDelegate(new PlayBarImpl());

    }

    private void loadData(){
        TaskUtil.run(new LQHandler.Supplier<List<AudioPlayPoint>>() {
                         @Override
                         public List<AudioPlayPoint> get() {
                             try {
                                 List<AudioPlayPoint> entitys = AudioPlayPoint.toAudioPlays(segment.getContent());
                                 minutes = getMinute(entitys);
                                 return entitys;
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                             return Collections.EMPTY_LIST;

                         }
                     }, new LQHandler.Consumer<List<AudioPlayPoint>>() {
                         @Override
                         public void accept(List<AudioPlayPoint> audioPlayPoint) {
                             playEntities = audioPlayPoint;
                             loadView(playEntities);
                         }
                     }
        );

        this.loadMp3(segment.getAudioPath(), new LQHandler.Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

            }
        });
    }

    /**
     * 加载Mp3 ，如果文件
     *
     * @param path
     * @param consumer
     */
    private void loadMp3(String path, LQHandler.Consumer<Boolean> consumer) {
        try {
            this.paneView.setMp3Path(FileUtil.getFileAbsolutePath(this.filePath));
            consumer.accept(true);
        } catch (Exception e) {
            //Map3文件加载失败，重新加载文件
            try {
                FileUtil.delete(FileUtil.getFileAbsolutePath(this.filePath));
            } catch (IOException e1) {
                e1.printStackTrace();
                consumer.accept(false);
                return;
            }
            LoadFile.loadFile(this.filePath, new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    try {
                        paneView.setMp3Path(s);
                        consumer.accept(true);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        consumer.accept(false);
                    }

                }
            });
            e.printStackTrace();
        }
    }

    /**
     * 获取时长
     *
     * @param playEntities
     * @return
     */
    private int getMinute(List<AudioPlayPoint> playEntities) {
        AudioPlayPoint start = playEntities.get(0);
        AudioPlayPoint end = playEntities.get(playEntities.size() - 1);
        int length = (int) (end.getEndTime() - start.getStartTime());

        return length / 60 / 1000;
    }

    private void initListener() {
        this.operationBar.setOperationBarI(new OperationBar.OperationBarI() {
            @Override
            public void handler(String id) {
                switch (id){
                    case "return":

                        finished();
                        break;
                    case "words":
                        if(paneView != null){
                            paneView.stop();
                        }

                        floatPlayViewStop();
                        toWordsAndShortWordsView(segment);
                        break;
                    case "hearted":
                        segmentHeartHelper.hearted();
                        break;
                    case "share":
                        shareClickHandler();
                        break;

                    default:


                }
            }
        });



    }




    private void shareClickHandler() {

        if(segment == null){
            return;
        }

        String userName = UserDataCache.getInstance().getUserName();
        String userId = UserDataCache.getInstance().getUserId();
        String hasFinished = "刚刚完成了\"" + segment.getTitle() + "\"的背诵";
        String segmentId = segment.getId();
        StringBuilder stringBuilder = new StringBuilder(LQService.getHttp());
        stringBuilder.append("html/share/shareContent.html").append("?userId=").append(userId).append("&segmentId=").append(segmentId);
        SharePlatform.onShare(getView().getContext(), userName + hasFinished, "我" + hasFinished, LQService.getLogoPath(), stringBuilder.toString());

    }

    private void toWordsAndShortWordsView(Segment segment){
        Intent intent = new Intent();
        intent.putExtras(BundleUtil.create(BundleUtil.DATA, segment));
        intent.setClass(getView().getContext(), SegmentWordsActivity.class);
        getView().getContext().startActivity(intent);
    }


    /**
     * 更新背诵时间
     */
    private void updateTimes() {
        Map<String, String> param = new HashMap<>();
        param.put("id", UserReciteRecordDataCache.getInstance().getCacheData().getId());
        param.put("minutes", this.minutes + "");

        LQService.put("userReciteRecord/updateReciteMinutes", null, UserReciteRecord.class, param, new LQHandler.Consumer<UserReciteRecord>() {
            @Override
            public void accept(UserReciteRecord userReciteRecord) {
                UserReciteRecordDataCache.getInstance().add(userReciteRecord);
            }
        });
    }

    private void addUserAndSegment() {
        User user = UserDataCache.getInstance().getUser();
        if (user == null || this.segment == null) {
            return;
        }

        UserAndSegment userAndSegment = new UserAndSegment();
        userAndSegment.setUserId(user.getId());
        userAndSegment.setSegmentId(segment.getId());
        userAndSegment.setContentId(this.segment.getContentId());

        LQService.post("userAndSegment/create", userAndSegment, UserAndSegment.class, null, new LQHandler.Consumer<UserAndSegment>() {
            @Override
            public void accept(UserAndSegment userAndSegment) {
                Content content = new Content();
                content.setId(segment.getContentId());
                RecitedSegmentDataCache.getInstance(content).add(Arrays.asList(userAndSegment));
            }
        });

    }

    /**
     * 记录该段下的单词
     *
     * @param segment
     * @param user
     */
    private void saveWord(Segment segment, User user) {
        if (segment == null || user == null) {
            return;
        }

        Map<String, String> param = new HashMap<>();
        param.put("userId", user.getId());
        param.put("segmentId", segment.getId());
        LQService.post("/userAndWord/insertAllBySegmentId", null, String.class, param, new LQHandler.Consumer<String>() {
            @Override
            public void accept(String userAndContent) {
                // ToastUtil.showShort(getView().getContext(),userAndContent);
            }
        });

    }

    public void loadView(List<AudioPlayPoint> playEntities) {


        LayoutInflater layoutInflater = LayoutInflater.from(this.getView().getContext());
        for (AudioPlayPoint audioPlayPoint : playEntities) {
            final View view = layoutInflater.inflate(R.layout.play_audio_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.play_audio_text);
            viewHolder.play_audio_playerpane = view.findViewById(R.id.play_audio_playerpane);
            viewHolder.ch = view.findViewById(R.id.play_audio_text_ch);
            viewHolder.view = view;

            viewHolder.title.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        long end = System.currentTimeMillis();

                        /**
                         * 如果按下的时间小于300毫秒，不处理
                         */
                        if (end - during < 300) {
                            return false;
                        }

                        if (!viewHolder.title.hasSelection()) {
                            return false;
                        }
                        String wordStr = viewHolder.title.getText().subSequence(viewHolder.title.getSelectionStart(), viewHolder.title.getSelectionEnd()).toString();
                        wordStr = wordStr.trim();


                        if (wordStr.isEmpty()) {
                            return false;
                        }
                        /// wordInfoPopupWindow = new WordInfoPopupWindow(getView().getContext());
                        paneView.destroy();
                        showWordInfo(wordStr);


                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        during = System.currentTimeMillis();
                    }
                    return false;
                }
            });


            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlay(view);

                }
            });
            viewHolder.ch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlay(view);

                }
            });


            viewHolder.audioPlayPoint = audioPlayPoint;
            viewHolder.title.setText(audioPlayPoint.getEnText());
            if (audioPlayPoint.getChText() != null) {
                viewHolder.ch.setText(audioPlayPoint.getChText());
            }

            view.setTag(viewHolder);
            this.views.add(view);
            this.frameLayout.addView(view);

        }


    }


    /**
     * 相应的界面切换成播放状态
     *
     * @param view
     */
    private void startPlay(View view) {
        if (lastView == view) {
            return;
        }


        if (lastView != null) {
            LOG.i(lastView.getHeight() + "");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(view.getWidth(), lastView.getHeight() - 300 + 5);
            lastView.setLayoutParams(layoutParams);
            ViewHolder viewHolder = (ViewHolder) lastView.getTag();
            lastView.setBackgroundResource(R.drawable.backgrond_top_bottom_grey);
            viewHolder.play_audio_playerpane.setVisibility(View.GONE);
            viewHolder.play_audio_playerpane.removeAllViews();
            paneView.destroy();
        }

        LOG.i(view.getHeight() + "");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(view.getWidth(), view.getHeight() + 300 - 5);
        view.setLayoutParams(layoutParams);
        view.setBackgroundResource(R.drawable.backgrond_top_bottom_white);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.play_audio_playerpane.addView(paneView);
        viewHolder.play_audio_playerpane.setVisibility(View.VISIBLE);
        floatPlayViewStop();
        paneView.play(viewHolder.audioPlayPoint);
        lastView = view;
        frameLayout.requestLayout();
    }

    public void onPause(){
        super.onPause();
        this.floatPlayViewStop();
    }


    private void floatPlayViewStop(){
        this.floatPlayView.stop();
        this.musicServicePlayHelper.getMusicBinder().pause();
    }

    private void showWordInfo(String word) {
        WordInfoView wordInfoView = new WordInfoView(getView().getContext(), null);

        WordInfoDataCache.getInstance(word).load(new LQHandler.Consumer<Word>() {
            @Override
            public void accept(Word word) {
                if (wordInfoView == null) {
                    return;
                }

                wordInfoView.load(word);

                new ActionSheet.DialogBuilder(getView().getContext())
                        .addButton("查看详情", (v) -> {
                            Intent intent = new Intent();
                            intent.putExtras(BundleUtil.create(BundleUtil.DATA, word));
                            intent.setClass(getView().getContext(), WordInfoActivity.class);
                            getView().getContext().startActivity(intent);
                        })
                        .addCloseButton("关闭", null)
                        .setCustomeView(wordInfoView).create();
                wordInfoView.play();
            }
        });
    }

    @Override
    public void reload() {

    }

    @Override
    public void destory() {

        this.paneView.destroy();
        musicServicePlayHelper.unbind();
        if(musicServicePlayHelper.getMusicBinder()!=null){
            musicServicePlayHelper.getMusicBinder().pause();
        }
    }

    public static final class ViewHolder {
        public View view;
        public TextView title;
        public TextView ch;
        public  RelativeLayout play_audio_playerpane;
        public  Button playButton;
        public  AudioPlayPoint audioPlayPoint;

    }

    private class PlayBarImpl implements PlayBarDelegate {

        @Override
        public void play() {
            if(musicServicePlayHelper.getMusicBinder() == null){
                return;
            }

            if(paneView!=null){
                paneView.stop();
            }

            musicServicePlayHelper.getMusicBinder().play();
        }

        @Override
        public void playNext() {

        }

        @Override
        public void playPrevious() {

        }

        @Override
        public void stop() {
            if(musicServicePlayHelper.getMusicBinder() == null){
                return;
            }

            musicServicePlayHelper.getMusicBinder().pause();
        }



        @Override
        public void updateProgress(long newValue) {

        }
    }

    private class MusicBinderImpl implements MusicService.MusicBinderDelegate {

        @Override
        public void currentTimeChange(int index, int currentTime) {
            if(musicServicePlayHelper.getPlayBarView() == null){
                return;
            }
            musicServicePlayHelper.getPlayBarView().updateProgress(currentTime,musicServicePlayHelper.getMusicBinder().getMax());

        }

        @Override
        public void finished() {
            if(musicServicePlayHelper.getPlayBarView() == null){
                return;
            }

            musicServicePlayHelper.getPlayBarView().stop();
            musicServicePlayHelper.getPlayBarView().updateProgress(0,1);
        }

        @Override
        public void currentPlayIndexChange(int playIndex) {

        }
    }



}
