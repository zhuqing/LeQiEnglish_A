package com.leqienglish.controller.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.leqienglish.MainActivity;
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
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.user.User;

public class LoginController extends ControllerAbstract {

    private static final LOGGER LOG = new LOGGER(LoginController.class);

    private EditText userName;
    private EditText password;

    private Button login;
    private TextView regist;

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

        regist.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        this.qqButton = this.getView().findViewById(R.id.login_main_qq);
        this.weiBoButton = this.getView().findViewById(R.id.login_main_weibo);
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


        this.weiXinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform weixin = ShareSDK.getPlatform(Wechat.NAME);

                thirdPartLogin(weixin);
            }
        });

        this.weiBoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform qq = ShareSDK.getPlatform(SinaWeibo.NAME);
                thirdPartLogin(qq);
            }
        });

        this.qqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
              thirdPartLogin(qq);

            }
        });
    }




    private void thirdPartLogin( Platform qq){
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
                User user = UserDataCache.getInstance().findByOtherSysId(platform.getDb().getUserId());

                if(user != null){
                    UserDataCache.getInstance().add(user);
                    toMainActivity();
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
                        toMainActivity();
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
                toMainActivity();
            }
        });
    }

    private void toMainActivity(){
        Intent intent = new Intent();
        intent.setClass(getView().getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getView().getContext().startActivity(intent);
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
