package com.leqienglish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.leqienglish.activity.play.PlayMainActivity;
import com.leqienglish.controller.HomeListViewController;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.version.VersionDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.database.SqlData;
import com.leqienglish.fragment.HomeFragment;
import com.leqienglish.fragment.LQFragmentAdapter;
import com.leqienglish.fragment.SecondFrament;
import com.leqienglish.fragment.ThirdFrament;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.AppType;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.util.dialog.DialogUtil;
import com.leqienglish.util.file.AndroidFileUtil;
import com.leqienglish.util.toast.ToastUtil;
import com.mob.MobSDK;

import xyz.tobebetter.version.Version;


public class MainActivity extends AppCompatActivity {
    private LOGGER logger = new LOGGER(MainActivity.class);
   // private TextView mTextMessage;

    private FrameLayout content;

    private HomeListViewController homeListViewController;

    private ViewPager viewPager;

    private BottomNavigationView navigation;

    private Integer rebackCount = 0;

    private FloatingActionButton floatingActionButton;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                 //   mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                   // mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    private void initViewPage(){
        this.viewPager = (ViewPager) this.findViewById(R.id.viewPage);
        this.viewPager.setAdapter(new LQFragmentAdapter(this.getSupportFragmentManager(),new HomeFragment(),new SecondFrament(),new ThirdFrament()));
        this.viewPager.setCurrentItem(0);

        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navigation.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        navigation.setSelectedItemId(R.id.navigation_dashboard);
                        break;
                    case 2:
                        navigation.setSelectedItemId(R.id.navigation_notifications);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_main);
        this.content = (FrameLayout) this.findViewById(R.id.content);

        this.initViewPage();

        this.floatingActionButton = this.findViewById(R.id.main_floatingbutton);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        AppType.mainContext = this.getBaseContext();
        MobSDK.init(this);
        this.initEventHandler();
    }

    private void initEventHandler(){
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
              //  intent.putExtras(BundleUtil.create(BundleUtil.DATA, content));
                intent.setClass(getApplicationContext(), PlayMainActivity.class);
                getApplicationContext().startActivity(intent);
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            rebackCount++;
           if(rebackCount == 1){
               ToastUtil.showShort(this,"再按一次将退出乐其英语");
               TaskUtil.runlater((t)->rebackCount = 0,4000L);
           }else{
               this.finishAffinity();
               return super.onKeyDown(keyCode, event);
           }
        }

        return false;

    }



    private void initData(){
        AndroidFileUtil.application = this.getApplication();
        ExecuteSQL.init(new SqlData(this.getBaseContext()));
        UserDataCache.getInstance().load(null);
     //   this.getBaseContext().get

        if(!LQService.isConnect){
            showNoNetWork();
            return;
        }
        VersionDataCache.getInstance().loadNewest(new LQHandler.Consumer<Version>() {
            @Override
            public void accept(Version version) {
                if(version!=null){
                    openNewVersionDialog(version,getApplicationContext());
                }
            }
        });
    }

    private void showNoNetWork(){
        DialogUtil.show(R.string.notconnectService,this);

    }


    private void openNewVersionDialog(Version version, Context context){

        View myView = LayoutInflater.from(MainActivity.this).inflate(R.layout.newversion_dialog, null);
        TextView textView = myView.findViewById(R.id.new_version_message);
        Spanned spanned = Html.fromHtml(version.getMessage());

        textView.setText(spanned);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.upgreade)
        .setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VersionDataCache.getInstance().installNewApk(getApplicationContext(),version);

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






}
