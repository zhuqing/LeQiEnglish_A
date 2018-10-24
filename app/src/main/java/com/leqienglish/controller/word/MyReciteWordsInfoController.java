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
import com.leqienglish.data.word.MyReciteWordReConfigDataCache;
import com.leqienglish.util.LQHandler;

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
                intent.setClass(getView().getContext(), ReciteWordsActivity.class);
                getView().getContext().startActivity(intent);
            }
        });

        this.startReciteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getView().getContext(), ReciteWordsActivity.class);
                getView().getContext().startActivity(intent);
            }
        });

        this.changeReciteNumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }



    @Override
    public void reload() {

        MyReciteWordReConfigDataCache.getInstance().load((r)->{
            if(r == null){
                return;
            }
            this.hasReciteTextView.setText("已完成背诵"+r.getHasReciteNumber()+"/"+r.getMyWordsNumber());

            if(!pullToRefreshScrollView.isRefreshing()){
                pullToRefreshScrollView.onRefreshComplete();
            }


        });
    }

    @Override
    public void destory() {

    }

    @Override
    public void clearAndRefresh(LQHandler.Consumer<Boolean> fininshed) {
        MyReciteWordReConfigDataCache.getInstance().clearData();
        reload();
    }

    @Override
    public void refresh(LQHandler.Consumer<Boolean> fininshed) {
        reload();
    }
}
