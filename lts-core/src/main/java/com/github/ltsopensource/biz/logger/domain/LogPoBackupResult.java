package com.github.ltsopensource.biz.logger.domain;

/**
 * @author: Owen Jia
 * @time: 2020/4/28 17:22
 */
public class LogPoBackupResult {
    String newTableName;
    boolean success;

    public String getNewTableName() {
        return newTableName;
    }

    public void setNewTableName(String newTableName) {
        this.newTableName = newTableName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
