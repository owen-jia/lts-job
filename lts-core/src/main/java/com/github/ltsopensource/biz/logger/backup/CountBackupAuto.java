package com.github.ltsopensource.biz.logger.backup;

import com.github.ltsopensource.biz.logger.JobLogBackup;
import com.github.ltsopensource.biz.logger.domain.JobLogPoBackup;
import com.github.ltsopensource.core.AppContext;

import java.util.List;

/**
 * 自动备份lts_job_log_po表
 * （单表超过200W数据就备份，并创建新表）
 * （200W的数据量通过配置文件配置）
 * @author: Owen Jia
 * @time: 2020/4/28 10:46
 */
public class CountBackupAuto extends AbstractBackupAuto implements JobLogBackup{

    int defaultRowCount = 2;//200W记录

    public CountBackupAuto(AppContext appContext) {
        super(appContext);
    }

    @Override
    protected void doExce() {
        Long rowCount = this.jobLogger.maxId();
        if(rowCount > defaultRowCount){
            doBackup();
        }
    }

    @Override
    public List<JobLogPoBackup> findAll() {
        return delegate.findAll();
    }

    @Override
    public boolean add(JobLogPoBackup backup) {
        return delegate.add(backup);
    }

    @Override
    public boolean remove(String tableName) {
        return delegate.remove(tableName);
    }

}
