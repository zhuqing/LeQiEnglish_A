package com.leqienglish.view.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.sf.RestClient;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.task.FutureTaskUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import xyz.tobebetter.entity.User;
import xyz.tobebetter.entity.english.Content;

public class RecommendArticle  extends RelativeLayout {
    private User user;
    private GridView gridView;
    private TextView showAllTextView;
    private GridViewAdapter gridViewAdapter;
    public RecommendArticle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.recommend_aritcle, this);
        this.gridView = this.findViewById(R.id.recommend_article_gridView);
        this.showAllTextView = this.findViewById(R.id.recommend_article_show_all);
        gridViewAdapter = new GridViewAdapter(LayoutInflater.from(this.gridView.getContext()));
        this.gridView.setAdapter(gridViewAdapter);

    }

    private void init(){
        this.showAllTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void reloadArticle(){
        try {
           List<Content> contentList =   FutureTaskUtil.run(()->{
                RestClient restClient = new RestClient();

                Content[] contents= restClient.get("recommend/recommendArticle",null,Content[].class);

                return Arrays.asList(contents);
            });

           this.gridViewAdapter.setItems(contentList);
           this.gridView.invalidate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.reloadArticle();
    }

    final class ViewHolder{
        ImageView imageView;
        TextView title;

    }

    class GridViewAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public GridViewAdapter(LayoutInflater mInflater){
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
            if(view!=null){
                holder = (RecommendArticle.ViewHolder) view.getTag();
            }else{
                holder = new RecommendArticle.ViewHolder();
                view =this.mInflater.inflate(R.layout.article_item,null);
                holder.imageView=view.findViewById(R.id.article_item_image);
                holder.title = view.findViewById(R.id.article_item_title);

                view.setTag(holder);

            }

            Content actical = this.getItem(i);
            if(actical == null){
                return view;
            }

            holder.title.setText(actical.getTitle());
            final RecommendArticle.ViewHolder fviewHolder = holder;
            try {
                final String filePath = FileUtil.getFileAbsolutePath(actical.getImagePath());



            } catch (IOException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
}
