package com.leqienglish.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.leqienglish.MainActivity;
import com.leqienglish.activity.base.App;
import com.leqienglish.util.LOGGER;
import com.leqienglish.view.event.MultipleTouchEventInterface;

/**
 * Created by zhuqing on 2017/8/19.
 */

public abstract class ControllerAbstract<F extends View> {

    private LOGGER logger = new LOGGER(ControllerAbstract.class);

    private PopupWindow mPopup;
    private int mCurrentX;
    private int mCurrentY;

    private F view;


    public ControllerAbstract(F view) {
        this.view = view;
    }


    public F getView() {
        return view;
    }

    protected <T extends View> T findViewById(int id) {
        return this.getView().findViewById(id);
    }

    public abstract void init();

    public abstract void reload();

    public void onResume() {
    }

    public void onPause() {
    }

    public abstract void destory();

    protected Context getContext() {
        if (this.getView() == null) {
            return null;
        }

        return this.getView().getContext();
    }

    protected void finished() {
        Activity activity = (Activity) this.getContext();
        activity.finish();
    }


    /**
     * 返回主页
     */
    public void goBackHome() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(getView().getContext(), MainActivity.class);
        getView().getContext().startActivity(intent);
    }

    public void addPopView(View view, int width, int height) {
        final View cv = new View(this.getContext());


        ViewGroup.LayoutParams allLayoutParams = new ViewGroup.LayoutParams(width, height);
        view.setLayoutParams(allLayoutParams);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);


        cv.setLayoutParams(layoutParams);
        // tv.setPadding(8, 8, 8, 8);

        mPopup = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ViewGroup viewGroup = (ViewGroup) this.getView();
        viewGroup.addView(cv);

        View.OnTouchListener otl = new View.OnTouchListener() {
            private float mDx;
            private float mDy;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mDx = mCurrentX - event.getRawX();
                    mDy = mCurrentY - event.getRawY();

                } else if (action == MotionEvent.ACTION_MOVE) {

                    mCurrentX = (int) (event.getRawX() + mDx);
                    mCurrentY = (int) (event.getRawY() + mDy);
                    mPopup.update(mCurrentX, mCurrentY, width, width);
                    return false;
                }
                return true;
            }
        };
        if (view instanceof MultipleTouchEventInterface) {
            MultipleTouchEventInterface multipleTouchEventInterface = (MultipleTouchEventInterface) view;
            multipleTouchEventInterface.addTouchEventHandler(otl);
        } else {
            view.setOnTouchListener(otl);
        }


        mCurrentX = App.sScreenWidth - width - 30;
        mCurrentY = App.sScreenHeight - height * 2 - 40;

        logger.d("addPopView");
        cv.post(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.d("poast");
                    mPopup.showAtLocation(cv, Gravity.NO_GRAVITY, mCurrentX, mCurrentY);
                    mPopup.update(mCurrentX, mCurrentY, width, width);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
