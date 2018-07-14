package com.leqienglish.controller;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.R;
import com.leqienglish.activity.LoadingActivity;
import com.leqienglish.activity.PlayAudioActivity;
import com.leqienglish.database.ExecuteSQL;

import com.leqienglish.entity.SQLEntity;

import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;

import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import xyz.tobebetter.entity.english.Content;

import static java.util.Arrays.*;

/**private
 * Created by zhuqing on 2017/8/19.
 */

public class HomeListViewController extends Controller<View>{
    private GridView gridView;
    private HomeListViewAdapter homeListViewAdapter;



    public HomeListViewController(View fragment) {
        super(fragment);
       // fragment.getResources().getString(R.string.HOST);
      //  this.imagePath = fragment.getResources().getString(R.string.HOST)+fragment.getResources().getString(R.string.IMAGE_PATH);
    }


    @Override
    public void init() {

       this.gridView = (GridView) this.getView().findViewById(R.id.home_listview);
        ExecuteSQL.getInstance().getDatasByType(ExecuteSQL.CONTENT_TYPE, new LQHandler.Consumer<List< SQLEntity>>(){
            @Override
            public void accept(List<SQLEntity> sqlEnities) {
                if(sqlEnities == null || sqlEnities.isEmpty()){
                   // findData(null);
                    return;
                }

                List<Content> contents = new ArrayList<Content>(sqlEnities.size());
                for(SQLEntity sqlEnity:sqlEnities){
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        contents.add(mapper.readValue(sqlEnity.getJson(), Content.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                addAdapter(contents);

            }
        } );

        ExecuteSQL.getInstance().getNewsetByType(ExecuteSQL.CONTENT_TYPE, new LQHandler.Consumer<SQLEntity>() {
            @Override
            public void accept(SQLEntity sqlEnity) {
                findData(sqlEnity);
            }
        });



        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Content content = homeListViewAdapter.getItem(i);
                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA,content));
                try {
                    final String filePath = FileUtil.getFileAbsolutePath(content.getAudioPath());
                    File file = new File(filePath);
                   // if(file.exists()){
                      //  intent.setClass(gridView.getContext(), PlayAudioActivity.class);
                   // }else{
                        intent.setClass(gridView.getContext(), LoadingActivity.class);
                   // }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getView().getContext().startActivity(intent);
            }
        });
    }

    private void findData(SQLEntity sqlEntity){
        if(sqlEntity == null){
            LQService.get("/english/content/findAll", Content[].class, null, new LQHandler.Consumer<Content[]>() {
                @Override
                public void accept(Content[] users) {
                    if(users == null ){
                        return;
                    }
                    try {
                        ExecuteSQL.getInstance().insertLearnE(ExecuteSQL.getInstance().toSQLEntitys(asList(users)),null);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    addAdapter(asList(users));
                }
            });
        }else{
            LQService.get("/english/content/findNew/"+sqlEntity.getCreateTime(), Content[].class, null, new LQHandler.Consumer<Content[]>() {
                @Override
                public void accept(Content[] users) {
                    try {
                        ExecuteSQL.getInstance().insertLearnE(ExecuteSQL.getInstance().toSQLEntitys(asList(users)),null);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    addAdapter(asList(users));
                }
            });
        }

    }


    /**
     * 加载图片
     * @param actical
     * @param consumer
     * @throws IOException
     */
    private void loadImageFile(Content actical,LQHandler.Consumer<String> consumer) throws IOException {
        final String filePath = FileUtil.getFileAbsolutePath(actical.getImagePath());
        File file = new File(filePath);
        if(file.exists()){
            consumer.accept(filePath);
        }else{
            LQService.download(HomeListViewController.this.getView().getResources().getString(R.string.IMAGE_PATH)+actical.getId(),filePath,MediaType.IMAGE_JPEG,null, consumer);
        }
    }



    private HomeListViewAdapter addAdapter(List<Content> users){
         homeListViewAdapter = new HomeListViewAdapter(LayoutInflater.from(this.getView().getContext()));
        homeListViewAdapter.setItems(users);
        gridView.setAdapter(homeListViewAdapter);
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
            try {
                final String filePath = FileUtil.getFileAbsolutePath(actical.getImagePath());



            } catch (IOException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
}
