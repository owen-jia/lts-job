package com.github.ltsopensource.biz.logger;

import com.github.ltsopensource.biz.logger.domain.JobLogPoBackup;

import java.util.List;

/**
 * 日志表备份表
 * @author: Owen Jia
 * @time: 2020/4/28 11:11
 */
public interface JobLogBackup {

    /**
     * 查询所有备份表
     * @return
     */
    List<JobLogPoBackup> findAll();

    /**
     * 添加新备份表
     * @param backup
     * @return
     */
    boolean add(JobLogPoBackup backup);

    /**
     * 逻辑移除备份表
     * @param tableName
     * @return
     */
    boolean remove(String tableName);

}
