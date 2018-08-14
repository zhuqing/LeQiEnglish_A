package com.leqienglish.controller.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.ArticleInfoActivity;
import com.leqienglish.activity.suggestion.SuggestionActivity;
import com.leqienglish.activity.user.UserLoginActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.adapter.simpleitem.SimpleItemAdapter;

import java.util.ArrayList;
import java.util.List;

import xyz.tobebetter.entity.Entity;
import xyz.tobebetter.entity.english.Catalog;
import xyz.tobebetter.entity.user.User;

public class UserManagerPaneController extends ControllerAbstract {

    private User user;

    private ImageView imageView;
    private TextView userNameTextView;
    private TextView registDateTextView;

    private Button relogin;

    private ListView managerList;

    private List<Catalog> catalogs = new ArrayList<>();
    private SimpleItemAdapter<Catalog> simpleItemAdapter;

    public UserManagerPaneController(View view) {
        super(view);
    }

    @Override
    public void init() {
        imageView = this.getView().findViewById(R.id.user_manager_pane_image);
        userNameTextView = this.getView().findViewById(R.id.user_manager_pane_username);
        registDateTextView = this.getView().findViewById(R.id.user_manager_pane_regist_date);
        managerList = this.getView().findViewById(R.id.user_manager_pane_list);
        this.relogin = this.getView().findViewById(R.id.user_manager_pane_relogin);

        simpleItemAdapter = new SimpleItemAdapter<Catalog>(LayoutInflater.from(this.getView().getContext())){
            @Override
            protected String toString(Catalog entity) {
                if(entity == null){
                    return "";
                }
                return entity.getTitle();
            }
        };

        managerList.setAdapter(simpleItemAdapter);

        this.initListener();
        initManagerList();
    }

    @Override
    public void reload() {

    }

    private void initManagerList(){
        Catalog catalog = new Catalog();
        catalog.setTitle("意见反馈");
        catalog.setId("recommend");
        catalogs.add(catalog);

        simpleItemAdapter.updateListView(catalogs);
    }

    private void initListener(){

        this.managerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Catalog catalog =  simpleItemAdapter.getItem(position);
              if (catalog == null){
                  return;
              }

                Intent intent = new Intent();

                intent.setClass(getView().getContext(), SuggestionActivity.class);
                getView().getContext().startActivity(intent);
            }
        });

        this.relogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getView().getContext(), UserLoginActivity.class);
                getView().getContext().startActivity(intent);
            }
        });
//        UserDataCache.getInstance().load(new LQHandler.Consumer<User>() {
//            @Override
//            public void accept(User user) {
//                if(user == null){
//                    return;
//                }
//            }
//        });
    }

    @Override
    public void destory() {

    }
}
