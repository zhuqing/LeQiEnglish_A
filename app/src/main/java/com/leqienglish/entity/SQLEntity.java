package com.leqienglish.entity;

import com.fasterxml.jackson.core.JsonParser;

/**
 * 数据实体
 * Created by zhuqing on 2018/4/29.
 */

public class SQLEntity {
    /**
     * 获取数据的URL
     */
    private String url;
    /**
     * 唯一ID
     */
    private String id;

    private String parentId;
    /**
     * 数据类型
     */
    private String type;
    /**
     * 数据
     */
    private String json;
    /**
     * 创建时间
     */
    private  String createTime;
    /**
     * 更新时间
     */
    private String updateTime;

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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 父Id
     */
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
