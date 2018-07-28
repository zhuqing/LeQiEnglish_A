package com.leqienglish.view.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.leqienglish.R;
import com.leqienglish.view.article.ArticleInfoView;

import java.util.List;

import xyz.tobebetter.entity.english.Segment;

public abstract class LeQiBaseAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;

    public LeQiBaseAdapter(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0L;
    }


}
