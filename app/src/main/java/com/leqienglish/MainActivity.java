package com.leqienglish;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.leqienglish.controller.HomeListViewController;
import com.leqienglish.fragment.HomeFragment;
import com.leqienglish.fragment.LQFragmentAdapter;
import com.leqienglish.fragment.SecondFrament;
import com.leqienglish.fragment.ThirdFrament;
import com.leqienglish.mybook.R;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);
        this.content = (FrameLayout) this.findViewById(R.id.content);

        this.initViewPage();

      //  mTextMessage = (TextView) findViewById(R.id.message);
         navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
