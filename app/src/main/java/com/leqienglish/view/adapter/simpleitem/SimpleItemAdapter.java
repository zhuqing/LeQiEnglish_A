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

    public class ViewHolder{
       public  TextView textView;
    }


    private SelectedModel selectedModel = SelectedModel.SINGLE;

    public List<T> selectedList = new ArrayList<>();
    private List<Integer> selectedIndexs = new ArrayList<>();

    protected abstract String toString(T t);

    protected  void setStyle(TextView textView){};

    protected   void hasSelected(int position){};
    protected   void hasDisSelected(int position){};


    public void disSelecte(Integer position){

    }

    public void selecte(Integer position){
        switch (selectedModel){
            case SINGLE:
                if(!this.selectedIndexs.isEmpty()){
                    hasDisSelected(this.selectedIndexs.get(0));
                }

                this.selectedList.clear();
                this.selectedIndexs.clear();
                break;

                case MULTIPLE:
                    break;
        }

        this.selectedIndexs.add(position);
        this.selectedList.add(this.getItem(position));

        hasSelected(position);
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
