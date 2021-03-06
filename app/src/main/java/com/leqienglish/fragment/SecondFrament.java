package com.leqienglish.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leqienglish.R;
import com.leqienglish.controller.WordListViewController;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class SecondFrament extends Fragment {
    private View homeView;
    private WordListViewController wordListViewController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.homeView == null) {
            homeView = inflater.inflate(R.layout.second, null);

            wordListViewController = new WordListViewController(homeView);
            wordListViewController.init();
            wordListViewController.reload();

        }

        return this.homeView;
    }

}
