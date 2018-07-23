package com.leqienglish.view.article;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.data.content.MyRecitingContentDataCache;
import com.leqienglish.data.user.UserReciteRecordDataCache;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.percent.CirclePrecentView;
import com.leqienglish.view.recommend.RecommendArticle;

import java.io.IOException;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.recite.UserReciteRecord;


public class UserRecitingArticle extends RelativeLayout {

    private Button addArticle;

    private TextView showAllRecitingArticle;

    private GridView recitingArticles;

    private User user;

    public UserRecitingArticle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.user_reciting_article, this);
        this.addArticle = this.findViewById(R.id.user_reciting_add_button);
    }

    public void load(){
        MyRecitingContentDataCache.getInstance().load(new LQHandler.Consumer<List<Content>>() {
            @Override
            public void accept(List<Content> contents) {

            }
        });
    }

    private void showData(List<Content> contentList){

    }

    final class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView precentText;
        CirclePrecentView circlePrecentView;

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
            UserRecitingArticle.ViewHolder holder = null;
            if (view != null) {
                holder = (UserRecitingArticle.ViewHolder) view.getTag();
            } else {
                holder = new UserRecitingArticle.ViewHolder();
                view = this.mInflater.inflate(R.layout.article_item_complete_status, null);
                holder.imageView = view.findViewById(R.id.article_item_complete_image);
                holder.title = view.findViewById(R.id.article_item_complete_title);
                holder.precentText = view.findViewById(R.id.article_item_complete_percent);
                holder.circlePrecentView = view.findViewById(R.id.article_item_complete_circle);

                view.setTag(holder);

            }

            Content actical = this.getItem(i);
            if (actical == null) {
                return view;
            }

            holder.title.setText(actical.getTitle());
            final UserRecitingArticle.ViewHolder fviewHolder = holder;
            try {
                if (actical.getImagePath() != null) {
                    final String filePath = FileUtil.getFileAbsolutePath(actical.getImagePath());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return view;
        }
    }

}
