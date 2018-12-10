package com.leqienglish.controller.article;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.leqienglish.R;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.AppRefreshManager;
import com.leqienglish.data.content.MyRecitingContentDataCache;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.article.ArticleInfoView;
import com.leqienglish.view.article.UserRecitingArticleView;
import com.leqienglish.view.recommend.RecommendArticle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.content.ReciteContentVO;
import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.content.UserAndContent;

public class ArticleInfoController extends ControllerAbstract {

    private User user;
    private Content content;
    private List<Segment> segmentList;

    private boolean hasAdd2MyReciting = false;

    private ArticleInfoView articleInfoView;

    public Button button;

    private boolean isReciting;

    public ArticleInfoController(View view,boolean isReciting) {
        super(view);
        this.isReciting = isReciting;
    }

    @Override
    public void init() {
        this.button = this.getView().findViewById(R.id.add_recite_article_info_button);
        this.articleInfoView = this.getView().findViewById(R.id.add_recite_article_info_view);


        this.initListener();
        reload();
    }

    private void initListener() {
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(hasAdd2MyReciting){
                    removeFromMyReciting();
                }else {
                    add();
                }
            }
        });
    }

    @Override
    public void reload() {


        if(this.isReciting){
            button.setVisibility(View.GONE);
        }else{
            button.setVisibility(View.VISIBLE);
        }
        if (MyRecitingContentDataCache.getInstance().contains(this.content)) {
            button.setBackgroundResource(R.drawable.background_red_corner);
            button.setText(R.string.has_add_article_to_recite);
            hasAdd2MyReciting = true;
        }
    }

    @Override
    public void destory() {

    }

    private void removeFromMyReciting(){
       AlertDialog.Builder builder = new AlertDialog.Builder(this.getView().getContext());
       builder.setTitle(R.string.warnning)
               .setMessage(R.string.is_remove_from_myreciting)
               .setPositiveButton(R.string.cancel,null)
               .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       removeFromMyReciting(content);
                   }
               }).show();
    }

    private void removeFromMyReciting(Content content){

        Map<String, String> param = new HashMap<>();
        param.put("userId",this.getUser().getId());
        param.put("contentId",this.getContent().getId());


        LQService.put("/userAndContent/remove",null, String.class, param, new LQHandler.Consumer<String>() {
            @Override
            public void accept(String s) {
                MyRecitingContentDataCache.getInstance().removeByContentId(content.getId());

                button.setText(R.string.add_article_to_recite);
                button.setBackgroundResource(R.drawable.background_green_selector_blue);
            }
        });

    }

    private void add() {


        if (this.getUser() == null || this.getContent() == null) {
            return;
        }

        if (button.getText().equals(this.getView().getResources().getString(R.string.has_add_article_to_recite))) {
            return;
        }

        UserAndContent userAndContent = new UserAndContent();
        userAndContent.setContentId(this.getContent().getId());
        userAndContent.setUserId(this.getUser().getId());
        userAndContent.setFinishedPercent(0);


        LQService.post("/userAndContent/create", userAndContent, UserAndContent.class, null, new LQHandler.Consumer<UserAndContent>() {
            @Override
            public void accept(UserAndContent userAndContent) {
                if (userAndContent == null) {
                    return;
                }

                button.setText(R.string.has_add_article_to_recite);
                button.setBackgroundResource(R.drawable.background_red_corner);
                loadRecitingContent();
            }
        });


    }

    /**
     *加载正在背诵的数据
     */
    private void loadRecitingContent() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("contentId", content.getId());
        LQService.get("/english/content/findUserRecitingByContentId", ReciteContentVO[].class, map, new LQHandler.Consumer<ReciteContentVO[]>() {
            @Override
            public void accept(ReciteContentVO[] reciteContentVOS) {
                if(reciteContentVOS == null){
                    return;
                }
                MyRecitingContentDataCache.getInstance().add(Arrays.asList(reciteContentVOS));

                AppRefreshManager.getInstance().refresh(UserRecitingArticleView.REFRESH_ID);

                AppRefreshManager.getInstance().clearnAndRefresh(RecommendArticle.REFRESH_ID);
            }
        });
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
        this.articleInfoView.setContent(content);
        this.reload();
    }


    public User getUser() {
        if(user == null){
            user = UserDataCache.getInstance().getUser();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
