package com.leqienglish.view.adapter.simpleitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.view.adapter.LeQiBaseAdapter;

import java.util.ArrayList;
import java.util.List;

import xyz.tobebetter.entity.Entity;

public abstract class SimpleItemAdapter<T extends Entity> extends LeQiBaseAdapter<T> {

    class ViewHolder{
        TextView textView;
    }



    public List<T> selectedList = new ArrayList<>();

    protected abstract String toString(T t);

    protected abstract void setStyle(TextView textView);



    public SimpleItemAdapter(LayoutInflater mInflater) {
        super(mInflater);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleItemAdapter.ViewHolder holder = null;
        if (convertView != null) {
            holder = (SimpleItemAdapter.ViewHolder) convertView.getTag();
        } else {
            holder = new SimpleItemAdapter.ViewHolder();
            convertView = this.mInflater.inflate(R.layout.simple_item, null);
            holder.textView = convertView.findViewById(R.id.simple_item_textview);
            this.setStyle(holder.textView);
            convertView.setTag(holder);
        }

        T t = this.getItem(position);
        if(t == null){
            return null;
        }

        holder.textView.setText(this.toString(t));




        return convertView;

    }
}
