package com.leqienglish.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.entity.SQLEntity;
import com.leqienglish.sf.databasetask.DataBaseTask;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.Entity;

import static com.leqienglish.database.Constants.CACHE_TABLE;
import static com.leqienglish.database.Constants.CREATE_TIME;
import static com.leqienglish.database.Constants.ID;
import static com.leqienglish.database.Constants.JSON;
import static com.leqienglish.database.Constants.PARENT_ID;
import static com.leqienglish.database.Constants.TYPE;
import static com.leqienglish.database.Constants.UPDATE_TIME;
import static com.leqienglish.database.Constants.URL;

public class ExecuteSQL {

    private static final LOGGER log = new LOGGER(ExecuteSQL.class);

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

    public static <T> List<T> toEntity(List<SQLEntity> sqlEntities, Class<T> claz) throws IOException {
        if (sqlEntities == null || sqlEntities.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<T> contents = new ArrayList<>(sqlEntities.size());
        ObjectMapper mapper = new ObjectMapper();
        for (SQLEntity sqlEntity : sqlEntities) {
            contents.add(mapper.readValue(sqlEntity.getJson(), claz));
        }

        return contents;
    }

    /**
     * contents 转换成SQLEntitys
     *
     * @param contents
     * @return
     * @throws JsonProcessingException
     */
    public static <T extends Entity> List<SQLEntity> toSQLEntitys(String type, String parentId, List<T> contents) throws JsonProcessingException {
        if (contents == null || contents.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<SQLEntity> datas = new ArrayList<>(contents.size());
        ObjectMapper mapper = new ObjectMapper();
        for (T content : contents) {
            if(content == null){
                continue;
            }
            SQLEntity sqlEnity = new SQLEntity();
            sqlEnity.setUrl("");
            sqlEnity.setParentId(parentId);
            sqlEnity.setType(type);
            sqlEnity.setId(content.getId());
            sqlEnity.setCreateTime(content.getUpdateDate() + "");
            sqlEnity.setUpdateTime(System.currentTimeMillis() + "");
            sqlEnity.setJson(mapper.writeValueAsString(content));
            datas.add(sqlEnity);

        }

        return datas;
    }

    public static <T> List<T> sqlEntity2Content(List<SQLEntity> sqlEntities, Class<T> claz) {
        if (sqlEntities == null || sqlEntities.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<T> datas = new ArrayList<>(sqlEntities.size());
        ObjectMapper mapper = new ObjectMapper();
        for (SQLEntity sqlEntity : sqlEntities) {
            try {
                datas.add(mapper.readValue(sqlEntity.getJson(), claz));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return datas;
    }


    public void insertLearnE(final List<SQLEntity> sqlEnities, LQHandler.Consumer consumer) {
        DataBaseTask dataBaseTask = new DataBaseTask<String>(consumer) {
            @Override
            protected String run(Object... objects) {
                SQLiteDatabase db = sqlData.getWritableDatabase();
                for (SQLEntity sqlEnity : sqlEnities) {
                    insert(sqlEnity, db);
                }
                return null;
            }
        };
        dataBaseTask.execute();
    }


    public static <T extends Entity> void insertLearnE(final List<T> data, String parentId, String type) {
        try {
            List<SQLEntity> sqlEntities = toSQLEntitys(type, parentId, data);
            SQLiteDatabase db = executeSQL.sqlData.getWritableDatabase();
            for (SQLEntity sqlEnity : sqlEntities) {
                executeSQL.insert(sqlEnity, db);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }


    }

    /**
     * 插入数据
     *
     * @param sqlEnity
     * @return
     */
    public void insertLearnE(final SQLEntity sqlEnity, LQHandler.Consumer consumer) {
        log.d("start insertLearnE ");
        DataBaseTask dataBaseTask = new DataBaseTask(consumer) {
            @Override
            protected Object run(Object[] objects) {
                SQLiteDatabase db = sqlData.getWritableDatabase();
                insert(sqlEnity, db);
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
     * @param type
     * @return
     */
    public static boolean delete(String type) {
        try {
            SQLiteDatabase db = executeSQL.sqlData.getWritableDatabase();
            db.delete(CACHE_TABLE, Constants.TYPE+"=" + type, null);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 删除数据
     *
     * @param type
     * @return
     */
    public static boolean delete(String type, String parentId) {
        try {
            SQLiteDatabase db = executeSQL.sqlData.getWritableDatabase();
            db.delete(CACHE_TABLE, Constants.TYPE + "=? AND " + Constants.PARENT_ID + " =?", new String[]{type, parentId});

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
        values.put(PARENT_ID, sqlEnity.getParentId());
        values.put(TYPE, sqlEnity.getType());
        values.put(CREATE_TIME, sqlEnity.getCreateTime());
        values.put(UPDATE_TIME, sqlEnity.getUpdateTime());
        return values;
    }

    private SQLEntity toSqlEntity(Cursor cursor) {
        SQLEntity sqlEnity = new SQLEntity();
        sqlEnity.setJson(cursor.getString(cursor.getColumnIndex(JSON)));
        sqlEnity.setId(cursor.getString(cursor.getColumnIndex(ID)));
        sqlEnity.setParentId(cursor.getString(cursor.getColumnIndex(PARENT_ID)));
        sqlEnity.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
        sqlEnity.setUrl(cursor.getString(cursor.getColumnIndex(URL)));
        sqlEnity.setCreateTime(cursor.getString(cursor.getColumnIndex(CREATE_TIME)));
        sqlEnity.setUpdateTime(cursor.getString(cursor.getColumnIndex(UPDATE_TIME)));
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
            protected Object run(Object[] objects) {
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

        DataBaseTask dataBaseTask = new DataBaseTask<List<SQLEntity>>(consumer) {
            @Override
            protected List<SQLEntity> run(Object... objects) {
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


    /**
     * 根据数据类型获取数据
     *
     * @param type
     * @return
     */
    public static <T> List<T> getDatasByType(String type, Class<T> claz) {

       return  getDatasByType(type,null,claz);

    }

    private static Cursor createCursor(String type ,String parentId){
        SQLiteDatabase db = executeSQL.sqlData.getReadableDatabase();
        if(parentId == null){
            return db.query(CACHE_TABLE, null, TYPE + "=? " ,
                    new String[]{type}, null, null, "CREATETIME desc");
        }else{
            return db.query(CACHE_TABLE, null, TYPE + "=? AND " + PARENT_ID + "=?",
                    new String[]{type, parentId}, null, null, "CREATETIME desc");
        }
    }

    /**
     * 根据数据类型获取数据
     *
     * @param type
     * @return
     */
    public static <T> List<T> getDatasByType(String type, String parentId, Class<T> claz) {



        SQLiteDatabase db = executeSQL.sqlData.getReadableDatabase();
        Cursor cursor = createCursor(type,parentId);

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
        List<SQLEntity> jsonDatas = new ArrayList<>();
        while (cursor.moveToNext()) {
            jsonDatas.add(executeSQL.toSqlEntity(cursor));
        }
       // log.d("getJSONDataById jsonData=" + jsonData);
        cursor.close();

        try {
            return ExecuteSQL.toEntity(jsonDatas, claz);
        } catch (IOException e) {
            e.printStackTrace();
            log.e(e.getMessage());
        }

        return null;


    }

    /**
     * 根据数据类型获取数据
     *
     * @param type
     * @return
     */
    public void getDatasByTypeAndParentId(final String parentId, final String type, LQHandler.Consumer<List<SQLEntity>> consumer) {

        DataBaseTask dataBaseTask = new DataBaseTask<List<SQLEntity>>(consumer) {
            @Override
            protected List<SQLEntity> run(Object... objects) {
                SQLiteDatabase db = sqlData.getReadableDatabase();
                Cursor cursor = db.query(CACHE_TABLE, null, TYPE + "=? AND " + PARENT_ID + "=?",
                        new String[]{type, parentId}, null, null, "CREATETIME desc");
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

    public void getNewsetByType(final String type, LQHandler.Consumer<SQLEntity> consumer) {
        DataBaseTask dataBaseTask = new DataBaseTask<SQLEntity>(consumer) {
            @Override
            protected SQLEntity run(Object... objects) {
                SQLiteDatabase db = sqlData.getReadableDatabase();
                Cursor cursor = db.query(CACHE_TABLE, null, TYPE + "=?",
                        new String[]{type}, null, null, "CREATETIME desc", "1");

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