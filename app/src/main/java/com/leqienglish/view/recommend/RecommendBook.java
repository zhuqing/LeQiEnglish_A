package com.leqienglish.view.recommend;

import android.content.Context;
import android.net.Uri;
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
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.task.FutureTaskUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;

public class RecommendBook extends RelativeLayout {
    private User user;

    private GridView gridView;

    private TextView showAll;


    public RecommendBook(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.recommend_book, this);

        this.gridView = this.findViewById(R.id.recommend_book_gridView);
        this.showAll = this.findViewById(R.id.recommend_book_show_all);
    }

    public void init(){
        this.showAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

       // FutureTaskUtil.run()
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
            RecommendBook.ViewHolder holder = null;
            if(view!=null){
                holder = (RecommendBook.ViewHolder) view.getTag();
            }else{
                holder = new RecommendBook.ViewHolder();
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
            final RecommendBook.ViewHolder fviewHolder = holder;
            try {
                final String filePath = FileUtil.getFileAbsolutePath(actical.getImagePath());



            } catch (IOException e) {
                e.printStackTrace();
            }

            return view;
        }
    }

}
