package com.leqienglish.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.user.UserReciteRecordDataCache;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.play.PlayerPaneView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;
import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.recite.UserReciteRecord;

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

    private Button startReciteButton;

    private int minutes;

    public PlayAudioAController(View view, Segment segment, String path) {
        super(view);
        this.segment = segment;
        this.filePath = path;
    }

    @Override
    public void init() {
        this.frameLayout = this.getView().findViewById(R.id.play_audio_layout);
        this.paneView = new PlayerPaneView(this.getView().getContext(), null);
        this.startReciteButton = (Button) this.findViewById(R.id.play_audio_start_recite);

        this.views = new ArrayList<>();

        try {
            this.paneView.setMp3Path(FileUtil.getFileAbsolutePath(this.filePath));
            this.playEntities = AudioPlayPoint.toAudioPlays(segment.getContent());
            loadView(playEntities);
            this.minutes = this.getMinute(playEntities);
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveWord(segment, UserDataCache.getInstance().getUser());
        this.initListener();
    }

    private int getMinute(List<AudioPlayPoint> playEntities){
        AudioPlayPoint start = playEntities.get(0);

        AudioPlayPoint end = playEntities.get(playEntities.size()-1);

        int length = (int) (end.getEndTime()-start.getStartTime());

        return length/60/1000;
    }

    private void initListener(){
        startReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTimes();
            }
        });
    }

    /**
     * 更新背诵时间
     */
    private void updateTimes(){
        Map<String,String> param = new HashMap<>();
        param.put("id", UserReciteRecordDataCache.getInstance().getCacheData().getId());
        param.put("minutes",this.minutes+"");

        LQService.put("userReciteRecord/updateReciteMinutes", null, UserReciteRecord.class, param, new LQHandler.Consumer<UserReciteRecord>() {
            @Override
            public void accept(UserReciteRecord userReciteRecord) {
                UserReciteRecordDataCache.getInstance().add(userReciteRecord);
            }
        });
    }

    /**
     * 记录该段下的单词
     * @param segment
     * @param user
     */
    private void saveWord(Segment segment , User user){
        if(segment == null|| user == null){
            return;
        }


        Map<String,String> param = new HashMap<>();
        param.put("userId",user.getId());
        param.put("segmentId",segment.getId());
        LQService.post("/userAndWord/insertAllBySegmentId", null, String.class, param, new LQHandler.Consumer<String>() {
            @Override
            public void accept(String userAndContent) {
                ToastUtil.showShort(getView().getContext(),userAndContent);
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
                        lastView.setBackgroundResource(R.drawable.backgrond_top_bottom_grey);
                        viewHolder.play_audio_playerpane.removeAllViews();
                        paneView.destroy();
                    }


                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(view.getWidth(), view.getHeight() + 300);
                    view.setLayoutParams(layoutParams);
                    view.setBackgroundResource(R.drawable.backgrond_top_bottom_white);
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
    @Override
    public void destory() {

    }
    final class ViewHolder {
        View view;
        TextView title;
        RelativeLayout play_audio_playerpane;
        Button playButton;
        AudioPlayPoint audioPlayPoint;

    }
}
