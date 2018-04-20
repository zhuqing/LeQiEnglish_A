package com.leqienglish.util;

import com.leqienglish.entity.PlayEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuqing on 2018/4/20.
 */

public class PlayEntityUitl {
    public List<PlayEntity> createEntitys(String str) {
    String[] strArr = str.split("\n");
    List<PlayEntity> entitys = new ArrayList<>();
    for (String item : strArr) {
        String[] itemArr = item.split(":");
        PlayEntity entity = new PlayEntity();
        entity.setText(itemArr[1].trim());
        entity.setStart(Long.valueOf(itemArr[0]));
        entitys.add(entity);
    }

    for (int i = 1; i < entitys.size(); i++) {
        PlayEntity beforeEntity = entitys.get(i - 1);
        PlayEntity currentEntity = entitys.get(i);
        beforeEntity.setEnd(currentEntity.getStart());
        beforeEntity.setDuring(beforeEntity.getEnd() - beforeEntity.getStart());
    }

    return entitys;
}

}
