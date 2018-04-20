package com.leqienglish.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class LQFragmentAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList;

    public LQFragmentAdapter(FragmentManager fm,Fragment... fragments) {
        super(fm);
        fragmentList = Arrays.asList(fragments);
    }

    @Override
    public Fragment getItem(int position) {

        return this.fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return this.fragmentList.size();
    }
}
