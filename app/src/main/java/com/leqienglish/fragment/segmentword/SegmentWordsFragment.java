package com.leqienglish.fragment.segmentword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leqienglish.R;
import com.leqienglish.controller.HomeListViewController;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class SegmentWordsFragment extends Fragment {
    private View homeView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this.homeView == null){
            homeView = inflater.inflate(R.layout.home_view,null);

            new HomeListViewController(homeView).init();
        }

        return this.homeView;
    }
}
