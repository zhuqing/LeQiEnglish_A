package com.leqienglish.helper.heart;

import com.leqienglish.R;
import com.leqienglish.data.user.UserHeartedDataCache;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.operation.OperationBar;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.user.UserHearted;

/**
 * Segment 点赞工具类
 */
public class SegmentHeartHelper implements HeartHelper {

    private OperationBar operationBar;
    private Segment segment;

    public SegmentHeartHelper(OperationBar operationBar,Segment segment){
        this.operationBar = operationBar;
        this.segment = segment;
    }

    public void hearted(){

        if(segment == null){
            return;
        }

        Integer awesomeNum = segment.getAwesomeNum() == null ? 0 :segment.getAwesomeNum();


        segment.setAwesomeNum(awesomeNum + 1);

        updateHearted(true);
        UserHeartedDataCache userHeartedDataCache = new UserHeartedDataCache(segment.getId());
        userHeartedDataCache.commit(Consistent.CONTENT_TYPE_SEGMENT);
        //ContentDataCache.update(selectedContent.getId());
    }

    public void updateHearted(boolean userInteract){

        if(segment == null){
            return;
        }

        Integer awesomeNum = segment.getAwesomeNum() == null ? 0 :segment.getAwesomeNum();

        if(userInteract){
            this.operationBar.update("hearted",awesomeNum+"", R.drawable.heart_red);
            return;
        }else{
            this.operationBar.update("hearted",awesomeNum+"",R.drawable.heart);
        }

        UserHeartedDataCache userHeartedDataCache = new UserHeartedDataCache(segment.getId());
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
