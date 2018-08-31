package com.leqienglish.view.article;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.content.ArticleInfoActivity;
import com.leqienglish.activity.content.ShowAllContentActiviey;
import com.leqienglish.data.content.MyRecitingContentDataCache;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.adapter.LeQiBaseAdapter;
import com.leqienglish.view.percent.CirclePrecentView;
import com.leqienglish.view.recommend.RecommendArticle;

import java.io.File;
import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.content.ReciteContentVO;
import xyz.tobebetter.entity.user.User;

import static com.leqienglish.util.BundleUtil.DATA_BL;


public class UserRecitingArticleView extends RelativeLayout {

    private LOGGER logger = new LOGGER(RecommendArticle.class);

    private Button addArticle;

    private TextView showAllRecitingArticle;
    private TextView myRecitingTitle;

    private GridView recitingArticles;

    private User user;

    private GridViewAdapter gridViewAdapter;

    public UserRecitingArticleView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.user_reciting_article, this);
        this.addArticle = this.findViewById(R.id.user_reciting_add_button);
        this.recitingArticles = this.findViewById(R.id.user_reciting_gridview);
        this.myRecitingTitle = this.findViewById(R.id.user_reciting_title);
        this.gridViewAdapter = new GridViewAdapter(LayoutInflater.from(this.recitingArticles.getContext()));
        this.initListener();
    }

    public void load() {
        MyRecitingContentDataCache.getInstance().load(new LQHandler.Consumer<List<ReciteContentVO>>() {
            @Override
            public void accept(List<ReciteContentVO> contents) {
                showData(contents);
            }
        });
    }

    private void initListener(){
        this.addArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setClass(getContext(), ShowAllContentActiviey.class);
                getContext().startActivity(intent);
            }
        });

        this.recitingArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Content content = gridViewAdapter.getItem(position);
                if (content == null) {
                    return;
                }

                Intent intent = new Intent();
               Bundle bundle = BundleUtil.create(BundleUtil.DATA, content);
                bundle.putBoolean(DATA_BL,true);
                intent.putExtras(bundle);
                intent.setClass(getContext(), ArticleInfoActivity.class);
                getContext().startActivity(intent);
            }
        });
    }

    private void showData(List<ReciteContentVO> contents) {
        if (contents == null ) {
            contents = Collections.EMPTY_LIST;
        }
        myRecitingTitle.setText("我的背诵("+contents.size()+")");

        logger.d("showData data Content " + contents.size());

        this.gridViewAdapter.updateListView(contents);

        this.recitingArticles.setAdapter(gridViewAdapter);

        int size = contents.size();
        int length = 170;

        float density = Resources.getSystem().getDisplayMetrics().density;

        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        recitingArticles.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        recitingArticles.setColumnWidth(itemWidth); // 设置列表项宽
        recitingArticles.setHorizontalSpacing(4); // 设置列表项水平间距
        recitingArticles.setStretchMode(GridView.NO_STRETCH);
        recitingArticles.setNumColumns(size); // 设置列数量=列表集合数

    }

    final class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView precentText;
        CirclePrecentView circlePrecentView;

    }

    class GridViewAdapter extends LeQiBaseAdapter<ReciteContentVO> {


        public GridViewAdapter(LayoutInflater mInflater) {
            super(mInflater);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            UserRecitingArticleView.ViewHolder holder = null;
            if (view != null) {
                holder = (UserRecitingArticleView.ViewHolder) view.getTag();
            } else {
                holder = new UserRecitingArticleView.ViewHolder();
                view = this.mInflater.inflate(R.layout.article_item_complete_status, null);
                holder.imageView = view.findViewById(R.id.article_item_complete_image);
                holder.title = view.findViewById(R.id.article_item_complete_title);
                holder.precentText = view.findViewById(R.id.article_item_complete_percent);
                holder.circlePrecentView = view.findViewById(R.id.article_item_complete_circle);

                view.setTag(holder);

            }

            ReciteContentVO actical = this.getItem(i);
            if (actical == null) {


                return view;
            }

            holder.title.setText(actical.getTitle());
            holder.precentText.setText("完成" + actical.getFinishedPercent() + "%");
            holder.circlePrecentView.setPercent(actical.getFinishedPercent() / 100.0F);
            final ViewHolder viewHolder = holder;
            final UserRecitingArticleView.ViewHolder fviewHolder = holder;

            if (actical.getImagePath() != null) {
                LoadFile.loadFile(actical.getImagePath(), new LQHandler.Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        viewHolder.imageView.setImageURI(Uri.fromFile(new File(s)));
                    }
                });
            }


            return view;
        }
    }

}
