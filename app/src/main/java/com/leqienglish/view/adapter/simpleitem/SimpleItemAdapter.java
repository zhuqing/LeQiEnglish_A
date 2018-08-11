package com.leqienglish.view.adapter.simpleitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.view.adapter.LeQiBaseAdapter;

import xyz.tobebetter.entity.english.Catalog;

public class SimpleItemAdapter extends LeQiBaseAdapter<Catalog> {

    class ViewHolder{
        TextView textView;
    }

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
            convertView.setTag(holder);
        }

        Catalog catalog = this.getItem(position);
        if(catalog == null){
            return null;
        }

        holder.textView.setText(catalog.getTitle());

        return convertView;

    }
}