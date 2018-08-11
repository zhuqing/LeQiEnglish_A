package com.leqienglish.controller.user;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.leqienglish.MainActivity;
import com.leqienglish.R;
import com.leqienglish.activity.user.UserRegistActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.MD5;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.recommend.RecommendArticle;

import xyz.tobebetter.entity.user.User;

public class UserRegistController extends ControllerAbstract {

    private LOGGER logger = new LOGGER(UserRegistController.class);
    private EditText userName;
    private EditText password;
    private EditText checkPassword;

    private Button regist;

    private RadioGroup radioGroup;
    private RadioButton manRadioButton;
    private RadioButton womanRadionButton;

    public UserRegistController(View view) {
        super(view);
    }

    @Override
    public void init() {
        userName = this.getView().findViewById(R.id.regist_main_username);
        password = this.getView().findViewById(R.id.regist_main_password);
        checkPassword = this.getView().findViewById(R.id.regist_main_repassword);

        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        checkPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

        this.radioGroup = this.getView().findViewById(R.id.regist_main_sex_rg);
        this.manRadioButton = this.getView().findViewById(R.id.regist_main_man_rb);
        this.womanRadionButton = this.getView().findViewById(R.id.regist_main_woman_rb);

        regist = this.getView().findViewById(R.id.regist_main_regist);
        this.initListner();
    }

    private void initListner(){
        this.regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        this.userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    return;
                }
                checkUserName();
            }
        });

        this.password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                   return ;
                }

                checkPassword();
                if(!checkPassword.getText().toString().isEmpty()){
                    checkPasswordIsMatch(password);
                }
            }
        });

        this.checkPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    return ;
                }

                checkCheckPassword();
                checkPasswordIsMatch(checkPassword);
            }
        });


    }

    private boolean checkCheckPassword(){
        String passwordStr = this.checkPassword.getText().toString();
        if( passwordStr.length()>=6&&passwordStr.length()<=16){
            checkPassword.setBackgroundResource(R.drawable.edittext_background);
            return true;
        }else{
            checkPassword.setBackgroundResource(R.drawable.square_normal_red);
            ToastUtil.showShort(this.getView().getContext(),R.string.message_password_unsuitable);
            return false;
        }
    }

    private boolean checkPasswordIsMatch(EditText currentEditText){
        String checkPasswordStr = this.checkPassword.getText().toString();
        String passWordStr = this.password.getText().toString();

        if(StringUtil.isNullOrEmpty(passWordStr)){
            password.setBackgroundResource(R.drawable.square_normal_red);

            ToastUtil.showShort(this.getView().getContext(),R.string.message_no_password);
            return false;
        }

        if(StringUtil.isNullOrEmpty(checkPasswordStr)){
            ToastUtil.showShort(this.getView().getContext(),R.string.message_no_password);
            checkPassword.setBackgroundResource(R.drawable.square_normal_red);
            return  false;
        }

        if(passWordStr.equals(checkPasswordStr)){
            currentEditText.setBackgroundResource(R.drawable.edittext_background);
            return true;
        }else {
            currentEditText.setBackgroundResource(R.drawable.square_normal_red);
            ToastUtil.showShort(this.getView().getContext(),R.string.message_regist_password_not_match);
            return false;
        }
    }

    private boolean checkUserName(){
        String userNameStr = this.userName.getText().toString();
        if( userNameStr.length()>=6&&userNameStr.length()<=16){
            userName.setBackgroundResource(R.drawable.edittext_background);
            return true;
        }else{
            userName.setBackgroundResource(R.drawable.square_normal_red);
            ToastUtil.showShort(this.getView().getContext(),R.string.message_username_unsuitable);
            return false;
        }
    }

    private boolean checkPassword(){
        String passwordStr = this.password.getText().toString();
        if( passwordStr.length()>=6&&passwordStr.length()<=16){
            password.setBackgroundResource(R.drawable.edittext_background);
            return true;
        }else{
            password.setBackgroundResource(R.drawable.square_normal_red);
            ToastUtil.showShort(this.getView().getContext(),R.string.message_password_unsuitable);
            return false;
        }


    }

    private boolean checkData(){
        if(!this.checkPassword()){
            return false;
        }

        if(!this.checkUserName()){
            return false;
        }

        if(!this.checkCheckPassword()){
            return false;
        }

        return true;
    }

    private void saveUser(){
        logger.d("startsaveUser");

        if(!this.checkData()){
            return;
        }

        String userName = this.userName.getText().toString();
        String password = this.password.getText().toString();
        String checkPassword = this.checkPassword.getText().toString();

        if(StringUtil.isNullOrEmpty(userName)){
            ToastUtil.showShort(this.getView().getContext(),R.string.message_no_username);
            return;
        }

        if(StringUtil.isNullOrEmpty(password)||StringUtil.isNullOrEmpty(checkPassword)){
            ToastUtil.showShort(this.getView().getContext(),R.string.message_no_password);
            return;
        }

        if(!password.equals(checkPassword)){
            ToastUtil.showShort(this.getView().getContext(),R.string.message_regist_password_not_match);
            return;
        }

        int sex = 0;

        if(radioGroup.getCheckedRadioButtonId() == manRadioButton.getId()){
            sex = 1;
        }

        final  int sexx = sex;

        UserDataCache.getInstance().load(new LQHandler.Consumer<User>() {
            @Override
            public void accept(User user) {
                User unsaveUser = null;
                if(user == null || user.getStatus().equals(1)){
                    unsaveUser = createUser(null);

                }else {
                    unsaveUser = createUser(user);
                }

                user.setSex(sexx);

                LQService.post("user/regist", user, User.class, null, new LQHandler.Consumer<User>() {
                    @Override
                    public void accept(User user) {
                        if(user == null){
                            ToastUtil.showShort(getView().getContext(),R.string.message_regist_fail);
                            return;
                        }

                        logger.d("deng lu");
                        ToastUtil.showShort(getView().getContext(),R.string.message_regist_success);
                        UserDataCache.getInstance().add(user);
                        toMainActivity();
                    }
                });
            }
        });
    }

    private void toMainActivity(){
        Intent intent = new Intent();
        intent.setClass(getView().getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getView().getContext().startActivity(intent);
    }

    private User createUser(User user){
        if(user == null){
            user = new User();
        }
        user.setPassword(MD5.md5(password.getText().toString()));

        String userName = this.userName.getText().toString();

        if(userName.contains("@")){
            user.setEmail(userName);
        }else {
            user.setName(userName);
        }

        return user;

    }

    @Override
    public void reload() {

    }

    @Override
    public void destory() {

    }
}
