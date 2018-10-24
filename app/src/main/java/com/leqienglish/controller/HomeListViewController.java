package com.leqienglish.controller;

import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.leqienglish.R;
import com.leqienglish.data.AppRefreshManager;
import com.leqienglish.data.RefreshI;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.util.dialog.DialogUtil;
import com.leqienglish.util.network.NetWorkUtil;
import com.leqienglish.view.UserBoardView;
import com.leqienglish.view.article.UserRecitingArticleView;
import com.leqienglish.view.recommend.RecommendArticle;
import com.leqienglish.view.recommend.RecommendBook;

import xyz.tobebetter.entity.user.User;

/**
 * private
 * Created by zhuqing on 2017/8/19.
 */

public class HomeListViewController extends ControllerAbstract<View> implements RefreshI {

    private static  final LOGGER logger = new LOGGER(HomeListViewController.class);

    private UserBoardView userBoardView;

    private UserRecitingArticleView userRecitingArticleView;

    private RecommendArticle recommendArticle;

    private RecommendBook recommendBook;

    private TextView showAllContent;

    private PullToRefreshScrollView mPullToRefreshLayout;


    public HomeListViewController(View fragment) {
        super(fragment);
        AppRefreshManager.getInstance().regist("HomeListViewController",this);
        // fragment.getResources().getString(R.string.HOST);
        //  this.imagePath = fragment.getResources().getString(R.string.HOST)+fragment.getResources().getString(R.string.IMAGE_PATH);
    }


    @Override
    public void init() {

        this.userBoardView = this.getView().findViewById(R.id.home_view_user_board);
        this.userRecitingArticleView = this.getView().findViewById(R.id.home_view_user_reciting);

        this.recommendArticle = this.getView().findViewById(R.id.home_view_recommend_article);
        mPullToRefreshLayout = this.getView().findViewById(R.id.home_view_pull_refresh_scrollview);
        mPullToRefreshLayout.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> pullToRefreshBase) {
                logger.i("+=========================");
                clearAndRefresh((h)->{
                    mPullToRefreshLayout.onRefreshComplete();
                });
            }
        });

        loadUser();

    }

    @Override
    public void reload() {

    }


    public void loadUser(){
        UserDataCache.getInstance().load(new LQHandler.Consumer<User>() {
            @Override
            public void accept(User user) {
                refresh(null);

            }
        });

    }
    @Override
    public void destory() {

    }

    @Override
    public void clearAndRefresh(LQHandler.Consumer<Boolean> fininshed) {
        if(!NetWorkUtil.isConnect(this.getView().getContext())){
            DialogUtil.show(R.string.title_no_network,this.getView().getContext());
            TaskUtil.runlater(fininshed,1000L);
            return;
        }
        AppRefreshManager.getInstance().clearnAndRefresh(RecommendArticle.REFRESH_ID,UserBoardView.REFRESH_ID,UserRecitingArticleView.REFRESH_ID);

        if(fininshed == null){
            return;
        }


        TaskUtil.runlater(fininshed,2000L);
    }

    @Override
    public void refresh(LQHandler.Consumer<Boolean> fininshed) {
        AppRefreshManager.getInstance().refresh(RecommendArticle.REFRESH_ID,UserBoardView.REFRESH_ID,UserRecitingArticleView.REFRESH_ID);

        if(fininshed == null){
            return;
        }
        TaskUtil.runlater(fininshed,2000L);


    }
}
