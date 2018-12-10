package com.leqienglish.pop;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.util.LQHandler;

import xyz.tobebetter.version.Version;

public class NewVersionDialog extends CustomDialog {
    private TextView message;


    private Version version;

    private LQHandler.Consumer<Version> versionConsumer;

    public NewVersionDialog(Context context, Version version, LQHandler.Consumer<Version> versionConsumer) {
        super(context);
        this.version = version;
        this.versionConsumer = versionConsumer;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.newversion_dialog);


        this.message = this.findViewById(R.id.new_version_message);

        this.initListener();
        initData();
    }

    private void initListener(){

    }

    private void initData(){
        if(version == null){
            return;
        }
        Spanned spanned = Html.fromHtml(version.getMessage());
        message.setText(spanned);
    }
}
