package com.github.ltsopensource.admin.access.domain;

import java.util.Date;

/**
 * 账户表
 * @author: Owen Jia(owen-jia@outlook.com)
 * @time: 2019/3/19 13:35
 */
public class Account {

    /**
     * 自增长字段
     */
    Integer id;
    /**
     * 账户
     */
    String username;
    /**
     * 密码
     */
    String password;
    /**
     * 邮箱
     */
    String email;
    Date createTime;
    Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
