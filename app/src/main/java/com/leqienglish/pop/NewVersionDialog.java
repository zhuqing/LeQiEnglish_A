package com.leqienglish.pop;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.util.LQHandler;

import xyz.tobebetter.version.Version;

public class NewVersionDialog extends CustomDialog {
    private TextView message;
    private Button cancel;
    private Button upgreade;

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

        this.cancel = this.findViewById(R.id.new_version_cancel);
        this.upgreade = this.findViewById(R.id.new_version_ok);
        this.message = this.findViewById(R.id.new_version_message);

        this.initListener();
        initData();
    }

    private void initListener(){
        this.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewVersionDialog.this.dismiss();
            }
        });

        this.upgreade.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
                if(versionConsumer !=null) {
                    versionConsumer.accept(version);
                }
            }
        });
    }

    private void initData(){
        if(version == null){
            return;
        }
        Spanned spanned = Html.fromHtml(version.getMessage());
        message.setText(spanned);
    }
}
