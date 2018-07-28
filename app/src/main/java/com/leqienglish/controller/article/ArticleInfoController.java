package com.leqienglish.controller.article;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;

import com.leqienglish.R;

import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.article.ArticleInfoView;

import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.content.UserAndContent;

public class ArticleInfoController extends ControllerAbstract {

    private User user;
    private Content content;
    private List<Segment> segmentList;

    private ArticleInfoView articleInfoView;

    public Button button ;

    public ArticleInfoController(View view) {
        super(view);
    }

    @Override
    public void init() {
      this.button =  this.getView().findViewById(R.id.add_recite_article_info_button);
      this.articleInfoView = this.getView().findViewById(R.id.add_recite_article_info_view);

      this.button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              add();
          }
      });

    }

    @Override
    public void reload() {

    }

    private void add(){
        if(this.getUser() == null|| this.getContent() == null){
            return;
        }

        if(button.getText().equals(this.getView().getResources().getString(R.string.has_add_article_to_recite))){
            return;
        }

        UserAndContent userAndContent = new UserAndContent();
        userAndContent.setContentId(this.getContent().getId());
        userAndContent.setUserId(this.getUser().getId());
        userAndContent.setFinishedPercent(0);


        LQService.post("/userAndContent/create",userAndContent,UserAndContent.class,null, new LQHandler.Consumer<UserAndContent>() {
            @Override
            public void accept(UserAndContent userAndContent) {
                if(userAndContent == null){
                    return;
                }

                button.setText(R.string.has_add_article_to_recite);

            }
        });

    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
        this.articleInfoView.setContent(content);
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
