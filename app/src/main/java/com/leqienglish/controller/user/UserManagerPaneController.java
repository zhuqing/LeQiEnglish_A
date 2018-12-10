package com.leqienglish.controller.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.reciting.RecitingArticleListActivity;
import com.leqienglish.activity.suggestion.SuggestionActivity;
import com.leqienglish.activity.user.UserLoginActivity;
import com.leqienglish.activity.word.MyReciteWordsInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.RefreshI;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.user.UserReciteRecordDataCache;
import com.leqienglish.data.version.VersionDataCache;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.date.DateUtil;
import com.leqienglish.view.adapter.simpleitem.SimpleItemAdapter;

import java.util.ArrayList;
import java.util.List;

import xyz.tobebetter.entity.english.Catalog;
import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.version.Version;

public class UserManagerPaneController extends ControllerAbstract implements RefreshI {

    private User user;

    private ImageView userHeaderImageView;
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
        userHeaderImageView = this.getView().findViewById(R.id.user_manager_pane_image);
        userNameTextView = this.getView().findViewById(R.id.user_manager_pane_username);
        registDateTextView = this.getView().findViewById(R.id.user_manager_pane_regist_date);
        managerList = this.getView().findViewById(R.id.user_manager_pane_list);
        this.relogin = this.getView().findViewById(R.id.user_manager_pane_relogin);

        simpleItemAdapter = new SimpleItemAdapter<Catalog>(LayoutInflater.from(this.getView().getContext())) {
            @Override
            protected String toString(Catalog entity) {
                if (entity == null) {
                    return "";
                }
                return entity.getTitle();
            }

            @Override
            protected void setStyle(TextView textView) {

            }
        };

        managerList.setAdapter(simpleItemAdapter);

        this.initListener();
        initManagerList();
        this.refresh(null);
    }

    @Override
    public void reload() {

    }

    private void initManagerList() {

        catalogs.add(createCatalog("01", "意见反馈"));
        catalogs.add(createCatalog("02", "文章背诵"));
        catalogs.add(createCatalog("03", "单词背诵"));
        catalogs.add(createCatalog("04", "当前版本\t1.00.1"));

        simpleItemAdapter.updateListView(catalogs);
    }

    private Catalog createCatalog(String id, String title) {
        Catalog catalog = new Catalog();
        catalog.setTitle(title);
        catalog.setId(id);

        return catalog;
    }

    private void initListener() {

        this.managerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Catalog catalog = simpleItemAdapter.getItem(position);
                if (catalog == null) {
                    return;
                }
                Intent intent = new Intent();
                switch (catalog.getId()) {
                    case "01":

                        intent.setClass(getView().getContext(), SuggestionActivity.class);
                        getView().getContext().startActivity(intent);
                        break;
                    case "02":
                        intent.setClass(getView().getContext(), RecitingArticleListActivity.class);
                        getView().getContext().startActivity(intent);
                        break;

                    case "03":
                        intent.setClass(getView().getContext(), MyReciteWordsInfoActivity.class);
                        getView().getContext().startActivity(intent);
                        break;
                    case "04":
                        checkVersion();
                        break;
                }


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

    }

    private void checkVersion() {

        VersionDataCache.getInstance().loadNewest(new LQHandler.Consumer<Version>() {
            @Override
            public void accept(Version version) {
                if (version != null) {
                    openNewVersionDialog(version,getView().getContext());
                } else {
                    hasNewestDialog();
                }
            }
        });

    }

    @Override
    public void destory() {

    }

    private void hasNewestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getView().getContext());

        builder.setTitle("已经是最新版本了")
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        builder.show();
    }

    private void openNewVersionDialog(Version version) {

        VersionDataCache.getInstance().openNewVersionDialog(version,getView().getContext());
    }

    //重新加载用户数据
    @Override
    public void clearAndRefresh(LQHandler.Consumer<Boolean> fininshed) {
        UserReciteRecordDataCache.getInstance().clearData();
        refresh(fininshed);
    }


    private void openNewVersionDialog(Version version, Context context){

        View myView = LayoutInflater.from(context).inflate(R.layout.newversion_dialog, null);
        TextView textView = myView.findViewById(R.id.new_version_message);
        Spanned spanned = Html.fromHtml(version.getMessage());

        textView.setText(spanned);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.upgreade)
                .setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VersionDataCache.getInstance().installNewApk(context,version);

                    }
                }).setNeutralButton(R.string.install_next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .setNegativeButton(R.string.install_ignore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VersionDataCache.getInstance().add(version);
                    }
                }).setView(myView)
                .create();

        builder.show();
    }

    //刷新用户数据
    @Override
    public void refresh(LQHandler.Consumer<Boolean> fininshed) {
        User user = UserDataCache.getInstance().getUser();

        this.userNameTextView.setText(user.getName());
        if(user.getCreateDate()!=null)
           this.registDateTextView.setText(DateUtil.toDateFmt(Long.valueOf(user.getCreateDate()), DateUtil.YYYYMMDD_CHN2));


        LoadFile.loadFile(user.getImagePath(), (path) -> {
            if (path == null) {
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            userHeaderImageView.setImageBitmap(bitmap);
        });
    }
}
