package com.leqienglish.controller.user;

import android.content.Intent;
import android.graphics.Paint;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leqienglish.MainActivity;
import com.leqienglish.R;
import com.leqienglish.activity.suggestion.SuggestionActivity;
import com.leqienglish.activity.user.UserRegistActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.md5.MD5;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.util.toast.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import xyz.tobebetter.entity.user.User;

public class LoginController extends ControllerAbstract {
    private EditText userName;
    private EditText password;

    private Button login;
    private TextView regist;

    private Button weiXinButton;
    private Button weiBoButton;
    private Button qqButton;


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

            }
        });

        this.weiBoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        this.qqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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


    @Override
    public void reload() {

    }

    @Override
    public void destory() {

    }
}
