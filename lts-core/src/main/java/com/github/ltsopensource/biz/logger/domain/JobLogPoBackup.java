package com.github.ltsopensource.biz.logger.domain;

/**
 * 日志表备份
 * @author: Owen Jia
 * @time: 2020/4/28 10:26
 */
public class JobLogPoBackup {
    String tableName;
    boolean delFlag;
    Long gmtCreated;
    Long gmtModified;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }

    public Long getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Long gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "JobLogPoBackup{" +
                "tableName='" + tableName + '\'' +
                ", delFlag=" + delFlag +
                ", gmtCreated=" + gmtCreated +
                ", gmtModified=" + gmtModified +
                '}';
    }
}
