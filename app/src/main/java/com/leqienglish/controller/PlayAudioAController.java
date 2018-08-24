package com.leqienglish.controller;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.segment.RecitingSegmentActivity;
import com.leqienglish.activity.word.ReciteWordsActivity;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.user.UserReciteRecordDataCache;
import com.leqienglish.pop.WordInfoDialog;
import com.leqienglish.popwindow.WordInfoPopupWindow;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.LeQiTextView;
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

    private WordInfoDialog wordInfoDialog;

    private WordInfoPopupWindow wordInfoPopupWindow;

    public PlayAudioAController(View view, Segment segment, String path) {
        super(view);
        this.segment = segment;
        this.filePath = path;
        wordInfoDialog = new WordInfoDialog(view.getContext());
    }

    @Override
    public void init() {
        this.frameLayout = this.getView().findViewById(R.id.play_audio_layout);
        this.paneView = new PlayerPaneView(this.getView().getContext(), null);
        this.startReciteButton = (Button) this.findViewById(R.id.play_audio_start_recite);

        this.wordInfoPopupWindow = new WordInfoPopupWindow(this.getView().getContext());
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
                paneView.destroy();
                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA, segment));
                intent.setClass(getView().getContext(), RecitingSegmentActivity.class);
                getView().getContext().startActivity(intent);
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

            viewHolder.view = view;

            viewHolder.title.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        if(!viewHolder.title.hasSelection()){
                            return false;
                        }
                        String wordStr = viewHolder.title.getText().subSequence(viewHolder.title.getSelectionStart(),viewHolder.title.getSelectionEnd()).toString();

                        if(wordInfoPopupWindow.isShowing()){
                            wordInfoPopupWindow.dismiss();
                        }

                        wordInfoPopupWindow.load(wordStr);
                       // wordInfoPopupWindow.showAsDropDown(PlayAudioAController.this.getView());
                        wordInfoPopupWindow.showAtLocation(PlayAudioAController.this.getView(), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
//
                    }
                    return false;
                }


            });

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
