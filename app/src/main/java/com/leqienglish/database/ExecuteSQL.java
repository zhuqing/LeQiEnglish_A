package com.leqienglish.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.leqienglish.entity.SQLEntity;

import com.leqienglish.sf.databasetask.DataBaseTask;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.english.Content;

import static android.R.attr.codes;
import static android.R.attr.id;
import static com.leqienglish.database.Constants.CACHE_TABLE;
import static com.leqienglish.database.Constants.CREATETIME;
import static com.leqienglish.database.Constants.ID;
import static com.leqienglish.database.Constants.JSON;

import static com.leqienglish.database.Constants.TYPE;
import static com.leqienglish.database.Constants.URL;

public class ExecuteSQL {
    public final static String CONTENT_TYPE = "content";
    public final static String USER_TYPE = "user";
    private LOGGER log = new LOGGER(ExecuteSQL.class);

    private SqlData sqlData;

    private static ExecuteSQL executeSQL;

    private ExecuteSQL(SqlData sqlData) {
        this.sqlData = sqlData;
    }

    public static void init(SqlData sqlData) {
        if (executeSQL == null) {
            executeSQL = new ExecuteSQL(sqlData);
        }
    }

    public static ExecuteSQL getInstance() {

        return executeSQL;
    }

    /**
     * contents 转换成SQLEntitys
     *
     * @param contents
     * @return
     * @throws JsonProcessingException
     */
    public List<SQLEntity> toSQLEntitys(List<Content> contents) throws JsonProcessingException {
        if (contents == null || contents.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<SQLEntity> datas = new ArrayList<>(contents.size());
        ObjectMapper mapper = new ObjectMapper();
        for (Content content : contents) {
            SQLEntity sqlEnity = new SQLEntity();
            sqlEnity.setUrl("");
            sqlEnity.setType(CONTENT_TYPE);
            sqlEnity.setId(content.getId());
            sqlEnity.setCreateTime(content.getUpdateDate()+"");
            sqlEnity.setJson(mapper.writeValueAsString(content));
            datas.add(sqlEnity);

        }

        return datas;
    }

    public void insertLearnE(final List<SQLEntity> sqlEnities, LQHandler.Consumer consumer) {
        DataBaseTask dataBaseTask =   new DataBaseTask<String>(consumer) {
            @Override
            protected String doInBackground(Object... objects) {
                SQLiteDatabase db = sqlData.getWritableDatabase();
                for (SQLEntity sqlEnity : sqlEnities) {
                    insert(sqlEnity,db);
                }
                return null;
            }
        };
        dataBaseTask.execute();
    }

    /**
     * 插入数据
     *
     * @param sqlEnity
     * @return
     */
    public void insertLearnE(final SQLEntity sqlEnity, LQHandler.Consumer consumer) {
        log.d("start insertLearnE ");
        DataBaseTask dataBaseTask =   new DataBaseTask(consumer) {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = sqlData.getWritableDatabase();
                insert(sqlEnity,db);
                db.close();

                return sqlEnity.getId();

            }


        };
        dataBaseTask.execute();

    }

    private void insert(SQLEntity sqlEnity, SQLiteDatabase db) {
        ContentValues values = createValues(sqlEnity);
        log.d("start insertLearnE url=" + sqlEnity);
        // log.d("start insertLearnE data="+json);
        SQLEntity data = getJSONDataById(sqlEnity.getId());
        if (data != null) {
            db.update(CACHE_TABLE, values, ID + "=?",
                    new String[]{sqlEnity.getId()});
        } else {
            db.insertOrThrow(CACHE_TABLE, null, values);
        }
    }

    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    public boolean deleteJsonDataById(String id) {
        try {
            SQLiteDatabase db = sqlData.getWritableDatabase();
            db.delete(CACHE_TABLE, "id=" + id, null);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    public boolean deleteJsonDataByMd5(String id) {
        try {
            SQLiteDatabase db = sqlData.getWritableDatabase();
            db.delete(CACHE_TABLE, ID + "='" + id + "'", null);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 创建values
     *
     * @param sqlEnity
     * @return
     */
    private ContentValues createValues(SQLEntity sqlEnity) {
        ContentValues values = new ContentValues();
        values.put(JSON, sqlEnity.getJson());
        values.put(URL, sqlEnity.getUrl());
        values.put(ID, sqlEnity.getId());
        values.put(TYPE, sqlEnity.getType());
        values.put(CREATETIME, sqlEnity.getCreateTime());
        return values;
    }

    private SQLEntity toSqlEntity(Cursor cursor) {
        SQLEntity sqlEnity = new SQLEntity();
        sqlEnity.setJson(cursor.getString(cursor.getColumnIndex(JSON)));
        sqlEnity.setId(cursor.getString(cursor.getColumnIndex(ID)));
        sqlEnity.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
        sqlEnity.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
        sqlEnity.setCreateTime(cursor.getString(cursor.getColumnIndex(CREATETIME)));
        return sqlEnity;
    }

    /**
     * 更新课程
     *
     * @param sqlEnity
     * @return
     */
    public void updateJSONData(final SQLEntity sqlEnity, LQHandler.Consumer consumer) {
        DataBaseTask dataBaseTask = new DataBaseTask(consumer) {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    SQLiteDatabase db = sqlData.getWritableDatabase();
                    ContentValues values = createValues(sqlEnity);
                    db.update(CACHE_TABLE, values, "id=" + sqlEnity.getId(), null);

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            }
        };
        dataBaseTask.execute();
    }

    /**
     * 根据数据类型获取数据
     *
     * @param type
     * @return
     */
    public void getDatasByType(final String type, LQHandler.Consumer<List<SQLEntity>> consumer) {

       DataBaseTask dataBaseTask =  new DataBaseTask<List<SQLEntity>>(consumer) {
            @Override
            protected List<SQLEntity> doInBackground(Object... objects) {
                SQLiteDatabase db = sqlData.getReadableDatabase();
                Cursor cursor = db.query(CACHE_TABLE, null, TYPE + "=?",
                        new String[]{type}, null, null, "CREATETIME desc");
                List<SQLEntity> jsonDatas = new ArrayList<>();
                if (cursor.getCount() <= 0) {
                    cursor.close();
                    return null;
                }
                if (!cursor.moveToFirst()) {
                    log.d("!cursor.moveToFirst()");
                    cursor.close();
                    return null;
                }
                String jsonData = null;
                while (cursor.moveToNext()) {
                    jsonDatas.add(toSqlEntity(cursor));
                }
                log.d("getJSONDataById jsonData=" + jsonData);
                cursor.close();

                return jsonDatas;
            }
        };

        dataBaseTask.execute();

    }

    public void  getNewsetByType(final String type , LQHandler.Consumer<SQLEntity> consumer){
        DataBaseTask dataBaseTask =    new DataBaseTask<SQLEntity>(consumer){
            @Override
            protected SQLEntity doInBackground(Object... objects) {
                SQLiteDatabase db = sqlData.getReadableDatabase();
                Cursor cursor = db.query(CACHE_TABLE, null, TYPE + "=?",
                        new String[]{type}, null, null, "CREATETIME desc","1");

                if (cursor.getCount() <= 0) {
                    cursor.close();
                    return null;
                }
                if (!cursor.moveToFirst()) {
                    log.d("!cursor.moveToFirst()");
                    cursor.close();
                    return null;
                }
                SQLEntity jsonData = null;
                while (cursor.moveToNext()) {
                    jsonData = toSqlEntity(cursor);
                }
                log.d("getJSONDataById jsonData=" + jsonData);
                cursor.close();

                return jsonData;
            }
        };
        dataBaseTask.execute();
    }


    /**
     * 根据Md5获取Json字符串
     *
     * @param id
     * @return
     */
    public SQLEntity getJSONDataById(String id) {
        log.d("getJSONDataById id=" + id);
        if (sqlData == null) {
            log.d("sqlData==null");
            return null;
        }
        SQLiteDatabase db = sqlData.getReadableDatabase();
        // Cursor cursor = db.rawQuery("select * from "+CACHE_TABLE,null);
        Cursor cursor = db.query(CACHE_TABLE, null, ID + "=?",
                new String[]{id}, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        }
        if (!cursor.moveToFirst()) {
            log.d("!cursor.moveToFirst()");
            cursor.close();
            return null;
        }
        SQLEntity jsonData = null;
        while (cursor.moveToNext()) {
            jsonData = toSqlEntity(cursor);
        }
        log.d("getJSONDataById jsonData=" + jsonData);
        cursor.close();

        return jsonData;
    }

}