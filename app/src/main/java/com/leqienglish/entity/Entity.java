package com.leqienglish.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;




/**
 * Created by zhuqing on 2017/7/25.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entity   implements Serializable {


    private String id;
    private Long createDate;
    private Long updateDate;
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
