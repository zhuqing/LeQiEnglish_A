package com.leqienglish.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.playandrecord.PlayMediaPlayerThread;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.view.play.PlayerPaneView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;

public class PlayAudioAController extends ControllerAbstract {
    private LOGGER logger = new LOGGER(PlayAudioAController.class);
    private Segment segment;
    private String filePath;

    List<AudioPlayPoint> playEntities = null;
    List<View> views;

    private LinearLayout frameLayout;
    private TextView titleView;

    private PlayerPaneView paneView;

    private View lastView;

    public PlayAudioAController(View view, Segment segment, String path) {
        super(view);
        this.segment = segment;
        this.filePath = path;
    }

    @Override
    public void init() {
        this.frameLayout = this.getView().findViewById(R.id.play_audio_layout);
        this.paneView = new PlayerPaneView(this.getView().getContext(), null);

        // this.titleView = this.getView().findViewById(R.id.play_audio_title);
        this.views = new ArrayList<>();
        // titleView.setText(segment.getTitle());

        try {
            this.paneView.setMp3Path(FileUtil.getFileAbsolutePath(this.filePath));
            this.playEntities = AudioPlayPoint.toAudioPlays(segment.getContent());
            loadView(playEntities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadView(List<AudioPlayPoint> playEntities) {


        LayoutInflater layoutInflater = LayoutInflater.from(this.getView().getContext());
        for (AudioPlayPoint audioPlayPoint : playEntities) {
            final View view = layoutInflater.inflate(R.layout.play_audio_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.play_audio_text);
            viewHolder.play_audio_playerpane = view.findViewById(R.id.play_audio_playerpane);
            logger.d("title");
            viewHolder.view = view;


            viewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (lastView == view) {
                        return;
                    }

                    if (lastView != null) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(view.getWidth(), lastView.getHeight() - 300);
                        lastView.setLayoutParams(layoutParams);
                        ViewHolder viewHolder = (ViewHolder) lastView.getTag();
                        viewHolder.play_audio_playerpane.removeAllViews();
                        paneView.destroy();
                    }


                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(view.getWidth(), view.getHeight() + 300);
                    view.setLayoutParams(layoutParams);

                    ViewHolder viewHolder = (ViewHolder) view.getTag();
                    viewHolder.play_audio_playerpane.addView(paneView);
                    paneView.play(viewHolder.audioPlayPoint);
                    lastView = view;

                }
            });

            viewHolder.audioPlayPoint = audioPlayPoint;
            viewHolder.title.setText(audioPlayPoint.getEnText());
            view.setTag(viewHolder);
            this.views.add(view);
            this.frameLayout.addView(view);

        }


    }

    @Override
    public void reload() {

    }

    final class ViewHolder {
        View view;
        TextView title;
        RelativeLayout play_audio_playerpane;
        Button playButton;
        AudioPlayPoint audioPlayPoint;

    }
}
