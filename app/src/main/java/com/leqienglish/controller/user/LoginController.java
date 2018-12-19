package com.leqienglish.controller.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.leqienglish.R;
import com.leqienglish.activity.user.UserRegistActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.md5.MD5;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.util.toast.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.user.User;

public class LoginController extends ControllerAbstract {

    private static final LOGGER LOG = new LOGGER(LoginController.class);

    private EditText userName;
    private EditText password;

    private Button login;
    private Button regist;
    private Button cancelButton;

    private Button weiXinButton;
    private Button weiBoButton;
    private Button qqButton;

    private Activity activity;



    public LoginController(View view) {
        super(view);
    }

    @Override
    public void init() {

        userName = this.getView().findViewById(R.id.login_main_username);
        password = this.getView().findViewById(R.id.login_main_password);

        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

        login = this.getView().findViewById(R.id.login_main_login);

        regist = this.getView().findViewById(R.id.login_main_regist);
        this.cancelButton = this.getView().findViewById(R.id.login_main_cancel);

        regist.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        this.qqButton = this.getView().findViewById(R.id.login_main_qq);
//        this.weiBoButton = this.getView().findViewById(R.id.login_main_weibo);
        this.weiXinButton = this.getView().findViewById(R.id.login_main_weixin);

        this.initListener();
    }

    private void initListener(){

        this.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        this.regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setClass(getView().getContext(), UserRegistActivity.class);
                getView().getContext().startActivity(intent);
            }
        });

        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackHome();
            }
        });


        this.weiXinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // SharePlatform.onShare(getView().getContext(),"乐其英语，乐在其中","fenxiang","http://www.leqienglish.com/res/static/images/logo.png","http://www.leqienglish.com");
                Platform weixin = ShareSDK.getPlatform(Wechat.NAME);
                thirdPartLogin(weixin);
            }
        });

//        this.weiBoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Platform qq = ShareSDK.getPlatform(SinaWeibo.NAME);
////                thirdPartLogin(qq);
//            }
//        });

        this.qqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
              thirdPartLogin(qq);

            }
        });
    }




    private void thirdPartLogin( Platform qq){
       qq.SSOSetting(false);
        if(qq.isAuthValid()){
            qq.removeAccount(true);
        }
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                if(platform == null ){
                    ToastUtil.showShort(getView().getContext(),"授权失败");
                    return;
                }

                User user1 = new User();

                user1.setImagePath(platform.getDb().getUserIcon());
                user1.setName(platform.getDb().getUserName());
                user1.setOtherSysId(platform.getDb().getUserId());

                User user = UserDataCache.getInstance().findByOtherSysId(platform.getDb().getUserId());

                if(user != null){
                    UserDataCache.getInstance().add(user);
                    goBackHome();
                    return;
                }else{
                    user = new User();
                    user.setImagePath(platform.getDb().getUserIcon());
                    user.setName(platform.getDb().getUserName());
                    user.setOtherSysId(platform.getDb().getUserId());
                    //81FEA2389A0359773B9234228F0426EC
                    if("m".equals(platform.getDb().getUserGender())){
                        user.setSex(Consistent.MAN);
                    }else{
                        user.setSex(Consistent.WOMEN);
                    }
                    UserDataCache.getInstance().regist(user,(u)->{
                        goBackHome();
                    });
                }



                //  LOG.d( hashMap.toString());
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });

        qq.authorize();
    }

    private void startLogin(){
        String userName = this.userName.getText().toString();
        String password = this.password.getText().toString();

        if(StringUtil.isNullOrEmpty(userName)){
            ToastUtil.showShort(this.getView().getContext(),R.string.message_no_username);
            return;
        }

        if(StringUtil.isNullOrEmpty(password)){
            ToastUtil.showShort(this.getView().getContext(),R.string.message_no_password);
            return;
        }

        password = MD5.md5(password);

        Map<String,String> param = new HashMap<>();
        param.put("userName",userName);
        param.put("password",password);

        LQService.get("user/login", User.class, param, new LQHandler.Consumer<User>() {
            @Override
            public void accept(User user) {
                if(user == null){
                    ToastUtil.showShort(getView().getContext(),"error");
                    return;
                }
                ToastUtil.showShort(getView().getContext(),"success");
                goBackHome();
            }
        });
    }




    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public void reload() {

    }

    @Override
    public void destory() {

    }
}
