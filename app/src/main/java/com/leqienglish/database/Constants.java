package com.leqienglish.database;
import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
    public static final int DATAVersion = 3;
    public static final String DATABASE_NAME = "learne.db";
    public static final String ID = "id";
    public static final String PARENT_ID = "parentId";
    public static final String JSON = "json";
    public static final String URL = "url";
    public static final String TYPE = "type";
    public static final String CREATE_TIME = "createTime";
    public static final String UPDATE_TIME = "updateTime";
    public static final String CACHE_TABLE = "cacheTable";

    /**
     * 我要背诵的单词
     */
    public static final String MY_WORDS_TYPE="MY_WORDS";
    /**
     *我正在背诵的单词
     */
    public static final String MY_RECITING_ARITCLE_TYPE="MY_RECITING_ARITCLE_TYPE";

    /**
     * 我的推荐
     */
    public static final String MY_RECOMMEND_TYPE = "MY_RECOMMEND_TYPE";

    /**
     * 文章下的段
     */
    public static final String MY_SEGMENT_TYPE = "MY_SEGMENT_TYPE";


    /**
     * 用户类型
     */
    public static final String USER_TYPE = "USER_TYPE";

    public static final String WORD_TYPE="WORD";
}