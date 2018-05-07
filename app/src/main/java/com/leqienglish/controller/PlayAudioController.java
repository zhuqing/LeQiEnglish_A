package com.leqienglish.controller;

import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.entity.PlayEntity;
import com.leqienglish.entity.english.Content;
import com.leqienglish.entity.english.TranslateEntity;
import com.leqienglish.playandrecord.PlayAudioThread;
import com.leqienglish.playandrecord.PlayMediaPlayerThread;
import com.leqienglish.playandrecord.RecordAudioThread;
import com.leqienglish.pop.CustomTranslateDialog;
import com.leqienglish.popwindow.WordDetailPopupWindow;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.PlayEntityUitl;
import com.leqienglish.util.TransApiUtil;
import com.leqienglish.view.LeQiTextView;

import java.io.IOException;
import java.util.List;

import io.reactivex.android.plugins.RxAndroidPlugins;

/**
 * Created by zhuqing on 2018/4/21.
 */

public class PlayAudioController extends Controller<GridView> {
    private LOGGER logger = new LOGGER(PlayAudioController.class);
    private Content content;
    private HomeListViewAdapter homeListViewAdapter;
    private PlayMediaPlayerThread playMediaPlayerThread;

    public PlayAudioController(GridView view, Content content) {
        super(view);
        this.content = content;

    }

    @Override
    public void init() {
        try {
            loadAudioFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<PlayEntity> playEntities = PlayEntityUitl.createEntitys(this.content.getContent());
        homeListViewAdapter = new HomeListViewAdapter(LayoutInflater.from(this.getView().getContext()));
        homeListViewAdapter.setItems(playEntities);
        this.getView().setAdapter(homeListViewAdapter);
    }

    private void loadAudioFile() throws IOException {
        final String filePath = FileUtil.getFileAbsolutePath(content.getAudioPath());
        this.playMediaPlayerThread = new PlayMediaPlayerThread(filePath);
    }


    final class ViewHolder {

        TextView title;
        Button playButton;
        Button recodeButton;

    }

    class HomeListViewAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public HomeListViewAdapter(LayoutInflater mInflater) {
            this.mInflater = mInflater;
        }

        private List<PlayEntity> items;

        public List<PlayEntity> getItems() {
            return items;
        }

        public void setItems(List<PlayEntity> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public PlayEntity getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0L;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            PlayAudioController.ViewHolder holder = null;
            if (view != null) {
                holder = (PlayAudioController.ViewHolder) view.getTag();
            } else {
                holder = new PlayAudioController.ViewHolder();
                view = this.mInflater.inflate(R.layout.play_audio_item, null);
                holder.title = view.findViewById(R.id.play_audio_text);
                holder.playButton = view.findViewById(R.id.play_audio_play);
                holder.recodeButton = view.findViewById(R.id.play_audio_record);
                view.setTag(holder);
                holder.playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayEntity pe = (PlayEntity) view.getTag();
                        play(pe);
                    }
                });

                final LeQiTextView leQiTextView = (LeQiTextView) holder.title;
               // RxAndroidPlugins.
                holder.title.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            String select = leQiTextView.getSelectText();
                            logger.v("select\t" + select);
                            if (select == null || select.isEmpty()) {
                                return false;
                            }
                            WordDetailPopupWindow pop = new WordDetailPopupWindow(leQiTextView.getContext(),select);
                            pop.showAtLocation(PlayAudioController.this.getView(), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);

                        }
                        return false;
                    }
                });

            }
            PlayEntity playEntity = this.getItem(i);
            holder.title.setText(playEntity.getText());
            holder.playButton.setTag(playEntity);
            holder.recodeButton.setTag(playEntity);
            return view;
        }

        private void play(final PlayEntity pe) {
            if (pe == null) {
                return;
            }
            playMediaPlayerThread.setPlayEntity(pe);
            playMediaPlayerThread.setPlayComplete(new LQHandler.Consumer() {
                @Override
                public void applay(Object o) {
                    logger.v("播放完成 录音");

                    RecordAudioThread thread = new RecordAudioThread(pe.getDuring() + 3000, new LQHandler.Consumer<List<short[]>>() {
                        @Override
                        public void applay(List<short[]> shorts) {

                            new PlayAudioThread(shorts, new LQHandler.Consumer() {
                                @Override
                                public void applay(Object o) {
                                    logger.v("录音播放完成 ");
                                    playMediaPlayerThread.setPlayComplete(null);
                                    playMediaPlayerThread.start();
                                }
                            }).execute();

                        }
                    });
                    thread.execute();
                }
            });
            playMediaPlayerThread.start();
        }
    }
}
