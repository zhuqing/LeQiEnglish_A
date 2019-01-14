package com.leqienglish.activity.content;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.article.ArticleInfoController;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.image.ImageUtil;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;

/**
 * 文章详情，段列表
 */
public class ArticleInfoActivity extends LeQiAppCompatActivity {

    private Content content;
    public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.add_recite_article_info);
        content = (Content) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);
        View view = this.findViewById(R.id.add_recite_article_info_root);

        boolean isReciting =  this.getIntent().getExtras().getBoolean(BundleUtil.DATA_BL,false);



        ArticleInfoController articleInfoController = new ArticleInfoController(view,isReciting);
        articleInfoController.init();
        articleInfoController.setContent(content);
        UserDataCache.getInstance().load(new LQHandler.Consumer<User>() {
            @Override
            public void accept(User user) {
                articleInfoController.setUser(user);
            }
        });

        setBackground(view,content);
        super.onCreate(savedInstanceState);


    }

    @Override
    protected String getActionBarTitle() {
        if(content == null || content.getTitle() == null){
            return "";
        }
        return content.getTitle() ;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void setBackground(View rootView,Content content){
        if(content == null || content.getImagePath() == null){
            return;
        }

        LoadFile.loadFile(content.getImagePath(), new LQHandler.Consumer<String>() {
            @Override
            public void accept(String s) {
                Bitmap sbit = BitmapFactory.decodeFile(s);
                Bitmap bitmap = ImageUtil.fastBlur(sbit,30);
                Drawable drawable =new  BitmapDrawable(bitmap);
                rootView.setBackground(drawable);
            }
        });


    }


}
