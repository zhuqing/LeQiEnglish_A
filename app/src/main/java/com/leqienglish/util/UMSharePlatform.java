//package com.leqienglish.util;
//
//
//import android.app.Activity;
//import android.util.Log;
//
//import com.umeng.socialize.UMAuthListener;
//import com.umeng.socialize.UMShareAPI;
//import com.umeng.socialize.bean.SHARE_MEDIA;
//
//import java.util.Map;
//
//public class UMSharePlatform {
//    /**
//     * 第三方登录
//     *第三方登录
//     * @param activity
//     * @param media
//     * @param callback
//     */
//    public static void loginThirdParty(Activity activity, SHARE_MEDIA media, final LoginSuccessCallback callback) {
//        UMShareAPI.get(activity).getPlatformInfo(activity, media, new UMAuthListener() {
//            @Override
//            public void onStart(SHARE_MEDIA share_media) {
//                Log.e("lee", "onStart授权开始: ");
//            }
//
//            @Override
//            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                callback.getLoginData(map.get("uid"));
//                //在该回调的map中可以拿到第三方返回的好多信息：比如昵称，头像，性别等等，由于我这里只需要uid所以就只取了uid。
//                Log.e("lee", map.toString());
//                Log.e("lee", "onComplete授权成功: ");
//            }
//
//            @Override
//            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
//                Log.e("lee", "onError授权异常: ");
//            }
//
//            @Override
//            public void onCancel(SHARE_MEDIA share_media, int i) {
//                Log.e("lee", "onCancel授权取消: ");
//            }
//        });
//    }
//
//    /**
//     * 回调接口
//     */
//    public interface LoginSuccessCallback {
//        /**
//         * @param uid 第三方平台返回的唯一标识
//         */
//        void getLoginData(String uid);
//    }
//}