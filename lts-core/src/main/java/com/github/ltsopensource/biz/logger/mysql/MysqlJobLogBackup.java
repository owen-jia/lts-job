package com.github.ltsopensource.biz.logger.mysql;

import com.github.ltsopensource.biz.logger.JobLogBackup;
import com.github.ltsopensource.biz.logger.domain.JobLogPoBackup;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.JdbcAbstractAccess;
import com.github.ltsopensource.store.jdbc.builder.*;

import java.util.List;

/**
 * @author: Owen Jia
 * @time: 2020/4/28 11:05
 */
public class MysqlJobLogBackup extends JdbcAbstractAccess implements JobLogBackup {

    public MysqlJobLogBackup(Config config) {
        super(config);
        this.createTable();
    }

    private void createTable(){
        createTable(readSqlFile("sql/mysql/lts_job_log_po_backup.sql"));
    }

    @Override
    public List<JobLogPoBackup> findAll() {
        List<JobLogPoBackup> jobLogPoBackups = new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .whereSql(new WhereSql().and("del_flag = ?", 0))
                .orderBy()
                .column("gmt_created", OrderByType.DESC)
                .list(RshHolder.JOB_PO_BACKUP_LIST_RSH);
        return jobLogPoBackups;
    }

    @Override
    public boolean add(JobLogPoBackup backup) {
        InsertSql insertSql = buildInsertSql();
        return setInsertSqlValues(insertSql, backup).doInsert() == 1;
    }

    private InsertSql buildInsertSql() {
        return new InsertSql(getSqlTemplate())
                .insert(getTableName())
                .columns("table_name",
                        "del_flag",
                        "gmt_created",
                        "gmt_modified"
                );
    }

    private InsertSql setInsertSqlValues(InsertSql insertSql, JobLogPoBackup jobLogPoBackup) {
        return insertSql.values(jobLogPoBackup.getTableName(),
                jobLogPoBackup.getDelFlag(),
                jobLogPoBackup.getGmtCreated(),
                jobLogPoBackup.getGmtModified());
    }

    @Override
    public boolean remove(String tableName) {
        return new UpdateSql(getSqlTemplate())
                .update()
                .table(getTableName())
                .set("del_flag", 1)
                .set("gmt_modified",SystemClock.now())
                .where("table_name = ?", tableName)
                .doUpdate() == 1;
    }

    private String getTableName() {
        return "lts_job_log_po_backup";
    }
}
