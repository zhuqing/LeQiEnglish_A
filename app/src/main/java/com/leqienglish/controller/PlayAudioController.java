//package com.leqienglish.controller;
//
//import android.net.Uri;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.leqienglish.R;
//import com.leqienglish.entity.PlayEntity;
//
//import com.leqienglish.playandrecord.PlayAudioThread;
//import com.leqienglish.playandrecord.PlayMediaPlayerThread;
//import com.leqienglish.playandrecord.RecordAudioThread;
//import com.leqienglish.pop.CustomTranslateDialog;
//import com.leqienglish.popwindow.WordDetailPopupWindow;
//import com.leqienglish.util.FileUtil;
//import com.leqienglish.util.LOGGER;
//import com.leqienglish.util.LQHandler;
//import com.leqienglish.util.PlayEntityUitl;
//import com.leqienglish.util.TransApiUtil;
//import com.leqienglish.view.LeQiTextView;
//import com.leqienglish.view.adapter.LeQiBaseAdapter;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import io.reactivex.android.plugins.RxAndroidPlugins;
//import xyz.tobebetter.entity.english.Content;
//import xyz.tobebetter.entity.english.Segment;
//import xyz.tobebetter.entity.english.play.AudioPlayPoint;
//
///**
// * Created by zhuqing on 2018/4/21.
// */
//
//public class PlayAudioController extends ControllerAbstract<ListView> {
//    private LOGGER logger = new LOGGER(PlayAudioController.class);
//    private Segment segment;
//    private String filePath;
//    private HomeListViewAdapter homeListViewAdapter;
//    private PlayMediaPlayerThread playMediaPlayerThread;
//
//    public PlayAudioController(ListView view, Segment segment,String path) {
//        super(view);
//        this.segment = segment;
//        this.filePath = path;
//
//    }
//
//    @Override
//    public void init() {
//        try {
//            loadAudioFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        List<AudioPlayPoint> playEntities = null;
//        try {
//            playEntities = AudioPlayPoint.toAudioPlays(segment.getContent());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        homeListViewAdapter = new HomeListViewAdapter(LayoutInflater.from(this.getView().getContext()));
//        homeListViewAdapter.setItems(playEntities);
//        this.getView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(view.getWidth(),view.getHeight()+100);
//                view.setLayoutParams(layoutParams);
//            }
//        });
//        this.getView().setAdapter(homeListViewAdapter);
//    }
//
//    @Override
//    public void reload() {
//
//    }
//
//    private void loadAudioFile() throws IOException {
//        final String filePath = FileUtil.getFileAbsolutePath(this.filePath);
//        File file = new File(filePath);
//        logger.d(filePath+"\nfile exit() "+file.exists());
//        this.playMediaPlayerThread = new PlayMediaPlayerThread(filePath);
//    }
//
//    public PlayMediaPlayerThread create() throws IOException {
//        final String filePath = FileUtil.getFileAbsolutePath(this.filePath);
//        File file = new File(filePath);
//        logger.d(filePath+"\nfile exit() "+file.exists());
//        return new PlayMediaPlayerThread(filePath);
//    }
//
//
//    final class ViewHolder {
//
//        TextView title;
//        Button playButton;
//        Button recodeButton;
//
//    }
//
//    class HomeListViewAdapter extends LeQiBaseAdapter<AudioPlayPoint> {
//
//
//
//        public HomeListViewAdapter(LayoutInflater mInflater) {
//            super(mInflater);
//        }
//
//
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            PlayAudioController.ViewHolder holder = null;
//            if (view != null) {
//                holder = (PlayAudioController.ViewHolder) view.getTag();
//            } else {
//                holder = new PlayAudioController.ViewHolder();
//                view = this.mInflater.inflate(R.layout.play_audio_item, null);
//                holder.title = view.findViewById(R.id.play_audio_text);
//                holder.playButton = view.findViewById(R.id.play_audio_play);
//                holder.recodeButton = view.findViewById(R.id.play_audio_record);
//                view.setTag(holder);
//                holder.playButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        AudioPlayPoint pe = (AudioPlayPoint) view.getTag();
//                        play(pe);
//                    }
//                });
//
//                final LeQiTextView leQiTextView = (LeQiTextView) holder.title;
//                View finalView = view;
//                holder.title.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(finalView.getWidth(), finalView.getHeight()+100);
//                        finalView.setLayoutParams(layoutParams);
//                    }
//                });
//               // RxAndroidPlugins.
//                holder.title.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                            String select = leQiTextView.getSelectText();
//                            logger.v("select\t" + select);
//                            if (select == null || select.isEmpty()) {
//                                return false;
//                            }
//                          //  WordDetailPopupWindow pop = new WordDetailPopupWindow(leQiTextView.getContext(),select);
//                           // pop.showAtLocation(PlayAudioController.this.getView(), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
//
//                        }
//                        return false;
//                    }
//                });
//
//            }
//            AudioPlayPoint playEntity = this.getItem(i);
//            holder.title.setText(playEntity.getEnText());
//            holder.playButton.setTag(playEntity);
//            holder.recodeButton.setTag(playEntity);
//            return view;
//        }
//
//        private void play(final AudioPlayPoint pe) {
//            if (pe == null) {
//                return;
//            }
//            try {
//                playMediaPlayerThread = create();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//            playMediaPlayerThread.setPlayEntity(pe);
//            logger.d(pe.getStartTime()+":"+pe.getEndTime());
//            playMediaPlayerThread.setPlayComplete(new LQHandler.Consumer() {
//                @Override
//                public void accept(Object o) {
//                    logger.v("播放完成 录音");
//
//                    RecordAudioThread thread = new RecordAudioThread(pe.getEndTime()-pe.getStartTime() + 3000, new LQHandler.Consumer<List<short[]>>() {
//                        @Override
//                        public void accept(List<short[]> shorts) {
//
//                            new PlayAudioThread(shorts, new LQHandler.Consumer() {
//                                @Override
//                                public void accept(Object o) {
//                                    logger.v("录音播放完成 ");
//                                    try {
//                                        playMediaPlayerThread = create();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                        return;
//                                    }
//                                    playMediaPlayerThread.setPlayEntity(pe);
//                                    playMediaPlayerThread.setPlayComplete(null);
//                                    playMediaPlayerThread.start();
//                                }
//                            }).execute();
//
//                        }
//                    });
//                    thread.execute();
//                }
//            });
//
//            playMediaPlayerThread.start();
//        }
//    }
//}
