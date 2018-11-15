package com.leqienglish.util;


import android.content.Context;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class SharePlatform {

    public static void onShare(Context context, String title, String content, String imageUrl, String url) {
        OnekeyShare oks = new OnekeyShare();
        // 分享回调
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // 分享内容的连接
        oks.setUrl(url);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams shareParams) {
                if(url != null){
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                }

            }
        });
        // 微信、易信分享内容
        // 微信图片，微信朋友圈必须

        oks.setImageUrl(imageUrl);

        // 启动分享GUI
        oks.show(context);
    }

}