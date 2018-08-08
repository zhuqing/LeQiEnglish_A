package com.leqienglish.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leqienglish.R;
import com.leqienglish.controller.user.UserManagerPaneController;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class ThirdFrament extends Fragment {
    private View homeView;

    private UserManagerPaneController userManagerPaneController;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this.homeView == null){
            homeView = inflater.inflate(R.layout.user_manager_pane,null);
            userManagerPaneController = new UserManagerPaneController(homeView);
            userManagerPaneController.init();

        }


        return this.homeView;
    }

}
