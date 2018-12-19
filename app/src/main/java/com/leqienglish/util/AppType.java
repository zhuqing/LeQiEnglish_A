package com.leqienglish.util;

/**
 * Created by zhuqing on 2018/4/24.
 */


import android.content.Context;
import android.media.AudioFormat;

public class AppType {
    public static final int frequence = 44100; // 录制频率，单位hz.这里的值注意了，写的不好，可能实例化AudioRecord对象的时候，会出错。我开始写成11025就不行。这取决于硬件设备
    public static final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    public static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    public  static Context mainContext;

    public final static String ID = "id";
    public final static String KEY = "key";
    public final static String JSON = "json";
    public final static String LIST = "list";

    public final static int REGIST = 1;
    public final static int CHECKEMAIL = 2;
    public final static int URL_ERROR = -100;

    public final static int PAGE_SIZE = 15;

    /**
     * firstPage
     */
    public final static int URL_ALLLESSION = 2003;
    public final static int URL_MORE_CONTENT = 2004;
    public final static int URL_NEW_CONTENT = 2005;


    public final static int URL_DOWNLOAD = 101;
    public final static int URL_DOWNLOAD_OVER = 102;
    /**
     * 已下载文件的长度
     */
    public final static String URL_DOWNLOAD_LEGTH = "length";
    public final static String DOWNLOAD_PATH = "downLoadPath";
    /**
     * 下载文件的长度
     */
    public final static int DOWNLOAD_ALLLEGTH = 102;
    public final static int HAS_DOWNLOAD = 103;
    public final static int DOWNLOAD_ERROR = -1;
    public final static int DOWNLOAD_OVER = 104;
    public final static String URL_IMAGE_NAME = "imageName";
    /**
     * 已经下载的JSON的数据
     */
    public final static int CONTENT_GET = 101;
    public final static int DIC_GET = 105;
    /**
     * 翻译json
     */
    public final static int TRANSPLATE_GET = 102;

    public final static int RECORD_OVER = 1;
    public final static int PLAY_RECORD_OVER = 2;

    public final static String LEARNE = "learne";// activity 跳转

    /**
     * 播放音乐时 ， 按时间点改变显示句子
     */
    public final static int CHANGE_TEXT = 1;
    public final static int CHANGE_IMAGR = 2;
    public final static int CHANGE_IMAGR_ALPH_0 = 3;
    public final static int CHANGE_IMAGR_ALPH_1 = 4;

    /**
     * DATABASE 操作
     */
    public final static int SELECT = 4;
    public final static String DATALIST = "list";
    public final static String DATA = "data";

    /**
     *
     */

    public final static int LOCAL_DATA = 0;
    public final static int NET_DATA = 1;

    /***
     * app 更新
     */
    public final static int HAS_UPDATE = 0;
    public final static int UPDATE_DOWNLOAD_OVER = 1;
    /**
     * 我的课程 from10开始
     */

    public final static int CONTENT_CATALOG = 10;
    public final static int CONTENT_BOOK = 11;
    public final static int BOOK_LESSION = 12;
    /**
     * 下载URL
     */
    public final static int DOWN_LOAD_URL = 10000;
    /**
     * 下载名言警句
     */
    public final static int FAMOUS_WORD_JSON = 100001;
    /**
     * readContent
     */
    public final static int SHOW_CONTENT = 10000001;
}