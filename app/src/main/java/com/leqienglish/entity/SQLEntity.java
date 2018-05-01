package com.leqienglish.entity;

/**
 * Created by zhuqing on 2018/4/29.
 */

public class SQLEntity {
    private String url;
    private String id;
    private String type;
    private String json;
    private  String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "SQLEnity{url="+this.url+",id="+this.id+",json="+this.json+",type="+this.type+"}";
    }
}
