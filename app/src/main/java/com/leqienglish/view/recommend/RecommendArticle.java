package com.leqienglish.view.recommend;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leqienglish.R;
import com.leqienglish.activity.ArticleInfoActivity;
import com.leqienglish.activity.LoadingActivity;
import com.leqienglish.activity.PlayAudioActivity;
import com.leqienglish.data.content.RecommendContentDataCache;
import com.leqienglish.database.Constants;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.entity.SQLEntity;
import com.leqienglish.sf.LQService;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.sf.RestClient;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.task.FutureTaskUtil;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;

import static com.leqienglish.database.Constants.MY_RECOMMEND_TYPE;

public class RecommendArticle extends RelativeLayout {
    private LOGGER logger = new LOGGER(RecommendArticle.class);

    private GridView gridView;
    private TextView showAllTextView;
    private GridViewAdapter gridViewAdapter;

    public RecommendArticle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.recommend_aritcle, this);
        this.gridView = this.findViewById(R.id.recommend_article_gridView);
        this.showAllTextView = this.findViewById(R.id.recommend_article_show_all);
        gridViewAdapter = new GridViewAdapter(LayoutInflater.from(this.gridView.getContext()));

        init();
    }

    private void init() {
        this.showAllTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });

        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Content content = gridViewAdapter.getItem(position);
                if (content == null) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA, content));
                intent.setClass(getContext(), ArticleInfoActivity.class);
                getContext().startActivity(intent);

            }
        });

    }

    public void load() {


        RecommendContentDataCache.getInstance().load(new LQHandler.Consumer<List<Content>>() {
            @Override
            public void accept(List<Content> contents) {
                showData(contents);
            }
        });


    }


    private void showData(List<Content> contents) {

        logger.d("showData data Content " + contents.size());

        this.gridViewAdapter.setItems(contents);

        this.gridView.setAdapter(gridViewAdapter);

        int size = contents.size();
        int length = 170;

        float density = Resources.getSystem().getDisplayMetrics().density;

        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(4); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数


    }



    final class ViewHolder {
        ImageView imageView;
        TextView title;

    }

    class GridViewAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public GridViewAdapter(LayoutInflater mInflater) {
            this.mInflater = mInflater;
        }

        private List<Content> items;

        public List<Content> getItems() {
            return items;
        }

        public void setItems(List<Content> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Content getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0L;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            RecommendArticle.ViewHolder holder = null;
            if (view != null) {
                holder = (RecommendArticle.ViewHolder) view.getTag();
            } else {
                holder = new RecommendArticle.ViewHolder();
                view = this.mInflater.inflate(R.layout.article_item, null);
                holder.imageView = view.findViewById(R.id.article_item_image);
                holder.title = view.findViewById(R.id.article_item_title);

                view.setTag(holder);

            }

            Content actical = this.getItem(i);
            if (actical == null) {
                return view;
            }

            holder.title.setText(actical.getTitle());
            final RecommendArticle.ViewHolder viewHolder = holder;
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
