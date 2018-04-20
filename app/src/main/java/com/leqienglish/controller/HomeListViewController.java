package com.leqienglish.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.mybook.R;
import com.leqienglish.entity.english.Content;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LQHandler;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class HomeListViewController extends Controller<View>{
    private ListView listView;
    private HomeListViewAdapter homeListViewAdapter;

    public HomeListViewController(View fragment) {
        super(fragment);
    }


    @Override
    public void init() {
       this.listView= (ListView) this.getView().findViewById(R.id.home_listview);
        LQService.get("/english/content/findAll", Content[].class, null, new LQHandler.Consumer<Content[]>() {
            @Override
            public void applay(Content[] users) {
                addAdapter(Arrays.asList(users));

            }
        });
    }

    private HomeListViewAdapter addAdapter(List<Content> users){
        HomeListViewAdapter homeListViewAdapter = new HomeListViewAdapter(LayoutInflater.from(this.getView().getContext()));
        homeListViewAdapter.setItems(users);
        listView.setAdapter(homeListViewAdapter);
        homeListViewAdapter.notifyDataSetChanged();
        return homeListViewAdapter;
    }

    final class ViewHolder{
        ImageView imageView;
        TextView title;
        TextView subTitle;
    }

    class HomeListViewAdapter extends BaseAdapter{

        private LayoutInflater mInflater;

        public HomeListViewAdapter(LayoutInflater mInflater){
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
          ViewHolder holder = null;
            if(view!=null){
                holder = (ViewHolder) view.getTag();
            }else{
                holder = new ViewHolder();
                view =this.mInflater.inflate(R.layout.home_listview_item,null);
                holder.imageView=view.findViewById(R.id.home_listview_item_imageview);
                holder.title = view.findViewById(R.id.home_listview_item_title);
                holder.subTitle = view.findViewById(R.id.home_listview_item_subtitle);
                view.setTag(holder);
            }

            Content actical = this.getItem(i);
            if(actical == null){
                return view;
            }
            holder.title.setText(actical.getContent());

            return view;
        }
    }
}
