package com.leqienglish.view.adapter.content;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.adapter.LeQiBaseAdapter;
import com.leqienglish.view.recommend.RecommendArticle;

import java.io.File;

import xyz.tobebetter.entity.english.Content;

public class ContentItemGridViewAdapter extends LeQiBaseAdapter<Content> {

    public ContentItemGridViewAdapter(LayoutInflater mInflater) {
        super(mInflater);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            holder = new ViewHolder();
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
        final ViewHolder viewHolder = holder;
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


    final class ViewHolder {
        ImageView imageView;
        TextView title;

    }
}
