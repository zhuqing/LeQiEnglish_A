package com.leqienglish.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.PlayAudioActivity;
import com.leqienglish.entity.english.Content;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class HomeListViewController extends Controller<View>{
    private ListView listView;
    private HomeListViewAdapter homeListViewAdapter;
    private String imagePath;

    public HomeListViewController(View fragment) {
        super(fragment);
        fragment.getResources().getString(R.string.HOST);
        this.imagePath = fragment.getResources().getString(R.string.HOST)+fragment.getResources().getString(R.string.IMAGE_PATH);
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

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Content content = homeListViewAdapter.getItem(i);
                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA,content));
                intent.setClass(listView.getContext(), PlayAudioActivity.class);
                getView().getContext().startActivity(intent);
            }
        });
    }

    private HomeListViewAdapter addAdapter(List<Content> users){
         homeListViewAdapter = new HomeListViewAdapter(LayoutInflater.from(this.getView().getContext()));
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

            holder.title.setText(actical.getTitle());
            final  ViewHolder fviewHolder = holder;
            LQService.get(HomeListViewController.this.getView().getResources().getString(R.string.IMAGE_PATH)+actical.getId(), byte[].class, null, new LQHandler.Consumer<byte[]>() {
                @Override
                public void applay(byte[] users) {
                    System.err.print(users);
                }
            });
            holder.imageView.setImageURI( Uri.parse(imagePath+actical.getId()));
            return view;
        }
    }
}
