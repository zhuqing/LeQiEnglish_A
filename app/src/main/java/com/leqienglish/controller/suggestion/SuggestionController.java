package com.leqienglish.controller.suggestion;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leqienglish.R;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LQHandler;

import xyz.tobebetter.entity.suggestion.Suggestion;
import xyz.tobebetter.entity.user.User;

public class SuggestionController extends ControllerAbstract {

    private EditText contectEditText;
    private EditText messageEditText;

    private Button commit;

    private boolean hasSave =false;

    public SuggestionController(View view) {
        super(view);
    }

    @Override
    public void init() {
        this.contectEditText = this.getView().findViewById(R.id.suggestion_layout_contect);
        this.messageEditText = this.getView().findViewById(R.id.suggestion_layout_message);
        this.commit = this.getView().findViewById(R.id.suggestion_layout_commit);
        this.commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasSave){
                    return;
                }

                hasSave = true;
                save();
            }
        });
    }

    private void save(){
        Suggestion suggestion = new Suggestion();
        suggestion.setContact(contectEditText.getText().toString());
        suggestion.setMessage(messageEditText.getText().toString());

        if(suggestion.getMessage() == null || suggestion.getMessage().isEmpty()){
            //AlertDialog.Builder()
            return;
        }
        UserDataCache.getInstance().load(new LQHandler.Consumer<User>() {
            @Override
            public void accept(User user) {
                suggestion.setUserId(user.getId());

                LQService.post("/suggestion/create", suggestion, Suggestion.class, null, new LQHandler.Consumer<Suggestion>() {
                    @Override
                    public void accept(Suggestion suggestion) {
                        hasSave = false;
                    }
                });
            }
        });


    }

    @Override
    public void reload() {

    }

    @Override
    public void destory() {

    }
}
