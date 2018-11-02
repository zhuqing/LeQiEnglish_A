package com.leqienglish.controller.word;

import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.leqienglish.R;
import com.leqienglish.activity.word.ReciteWordsActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.RefreshI;
import com.leqienglish.data.word.MyReciteWordConfigDataCache;
import com.leqienglish.pop.actionsheet.ActionSheet;
import com.leqienglish.util.AppType;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.word.ReciteWordNumberChangeView;

import xyz.tobebetter.entity.word.ReciteWordConfig;

public class MyReciteWordsInfoController extends ControllerAbstract implements RefreshI{
    private Button startReciteButton;
    private Button startWriteButton;

    private TextView changeReciteNumTextView;
    private TextView hasReciteTextView;

    private PullToRefreshScrollView pullToRefreshScrollView;

    public MyReciteWordsInfoController(View view) {
        super(view);
    }

    @Override
    public void init() {
        this.startReciteButton = (Button) this.findViewById(R.id.my_recite_word_info_recite_word);
        this.startWriteButton = (Button) this.findViewById(R.id.my_recite_word_info_write_word);
        this.changeReciteNumTextView = (TextView) this.findViewById(R.id.my_recite_word_info_num_perday);
        this.hasReciteTextView = (TextView) this.findViewById(R.id.my_recite_word_info_number);
        this.pullToRefreshScrollView = (PullToRefreshScrollView) this.findViewById(R.id.my_recite_word_info_refresh_scrollview);

        changeReciteNumTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        this.initListener();
        reload();
    }

    private void initListener(){
        this.pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                clearAndRefresh(null);
            }
        });

        this.startWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA,false));
                intent.setClass(getView().getContext(), ReciteWordsActivity.class);
                getView().getContext().startActivity(intent);
            }
        });

        this.startReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA,true));
                intent.setClass(getView().getContext(), ReciteWordsActivity.class);
                getView().getContext().startActivity(intent);
            }
        });

        this.changeReciteNumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ReciteWordNumberChangeView view = new ReciteWordNumberChangeView(getView().getContext());
                view.setItem(MyReciteWordConfigDataCache.getInstance().getCacheData());

                new ActionSheet.DialogBuilder(getView().getContext()).setCustomeView(view).addButton("修改",(cview)->{

                    ReciteWordConfig newReciteWordConfig = view.getReciteWordConfig();
                    MyReciteWordConfigDataCache.getInstance().update(newReciteWordConfig);
                    reSetData(newReciteWordConfig);

                }).create();
            }
        });


    }



    @Override
    public void reload() {

        MyReciteWordConfigDataCache.getInstance().load((r)->{
            if(pullToRefreshScrollView.isRefreshing()){
                pullToRefreshScrollView.onRefreshComplete();
            }
            if(r == null){
                ToastUtil.showShort(AppType.mainContext,"数据加载失败");
                return;
            }

            reSetData(r);

        });
    }

    private void reSetData(ReciteWordConfig r){
        this.hasReciteTextView.setText("已完成背诵"+r.getHasReciteNumber()+"/"+r.getMyWordsNumber());
        this.changeReciteNumTextView.setText("每日背诵"+r.getReciteNumberPerDay()+"个单词");


    }

    @Override
    public void destory() {

    }

    @Override
    public void clearAndRefresh(LQHandler.Consumer<Boolean> fininshed) {
        MyReciteWordConfigDataCache.getInstance().clearData();
        reload();
    }

    @Override
    public void refresh(LQHandler.Consumer<Boolean> fininshed) {
        reload();
    }
}
