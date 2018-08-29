package com.leqienglish;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.leqienglish.controller.HomeListViewController;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.version.VersionDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.database.SqlData;
import com.leqienglish.fragment.HomeFragment;
import com.leqienglish.fragment.LQFragmentAdapter;
import com.leqienglish.fragment.SecondFrament;
import com.leqienglish.fragment.ThirdFrament;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.file.AndroidFileUtil;

import xyz.tobebetter.version.Version;


public class MainActivity extends AppCompatActivity {
    private LOGGER logger = new LOGGER(MainActivity.class);
   // private TextView mTextMessage;

    private FrameLayout content;

    private HomeListViewController homeListViewController;

    private ViewPager viewPager;

    private BottomNavigationView navigation;

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

         navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search, menu);
//        MenuItem menuItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
//        //设置搜索的事件
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Toast t = Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT);
//                t.setGravity(Gravity.TOP, 0, 0);
//                t.show();
//
//               //MainActivity wordInfoController.search(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//
//        return super.onCreateOptionsMenu(menu);
//    }


    private void initData(){
        AndroidFileUtil.application = this.getApplication();
        ExecuteSQL.init(new SqlData(this.getBaseContext()));
        UserDataCache.getInstance().load(null);
     //   this.getBaseContext().get

        VersionDataCache.getInstance().loadNewest(new LQHandler.Consumer<Version>() {
            @Override
            public void accept(Version version) {
                if(version!=null){
                    openNewVersionDialog(version);
                }
            }
        });
    }

    private void openNewVersionDialog(Version version){
//        NewVersionDialog dialog = new NewVersionDialog(this.getApplicationContext(), version, new LQHandler.Consumer<Version>() {
//            @Override
//            public void accept(Version version) {
//                logger.d("==========upgread");
//            }
//        });
//        dialog.show();

        View myView = LayoutInflater.from(MainActivity.this).inflate(R.layout.newversion_dialog, null);
        TextView textView = myView.findViewById(R.id.new_version_message);
        Spanned spanned = Html.fromHtml(version.getType()+version.getMessage());

        textView.setText(spanned);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.upgreade)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VersionDataCache.getInstance().add(version);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setView(myView)
                .create();

        builder.show();
    }






}
