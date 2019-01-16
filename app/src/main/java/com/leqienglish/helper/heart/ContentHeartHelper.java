package com.leqienglish.helper.heart;

import com.leqienglish.R;
import com.leqienglish.data.content.ContentDataCache;
import com.leqienglish.data.user.UserHeartedDataCache;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.operation.OperationBar;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.UserHearted;

public class ContentHeartHelper implements HeartHelper {
    private OperationBar operationBar;
    private Content content;

    public ContentHeartHelper(OperationBar operationBar,Content content){
        this.content = content;
        this.operationBar = operationBar;
    }
    @Override
    public void hearted() {
        if(this.content == null || this.content.getAwesomeNum() == null){
            return;
        }

        this.content.setAwesomeNum(content.getAwesomeNum()+1);

        updateHearted(true);
        UserHeartedDataCache userHeartedDataCache = new UserHeartedDataCache(content.getId());
        userHeartedDataCache.commit(Consistent.CONTENT_TYPE_CONTENT);
        ContentDataCache.update(content.getId());
    }

    @Override
    public void updateHearted(boolean userInteract) {
        if(content == null || content.getAwesomeNum() == null){
            return;
        }

        long awesomeNum = this.content.getAwesomeNum();



        if(userInteract){
            this.operationBar.update("hearted",awesomeNum+"", R.drawable.heart_red);
            return;
        }else{
            this.operationBar.update("hearted",awesomeNum+"",R.drawable.heart);
        }

        UserHeartedDataCache userHeartedDataCache = new UserHeartedDataCache(content.getId());
        userHeartedDataCache.load(new LQHandler.Consumer<UserHearted>() {
            @Override
            public void accept(UserHearted userHearted) {
                if(userHearted != null){
                    operationBar.update("hearted",R.drawable.heart_red);
                }
            }
        });
    }
}
