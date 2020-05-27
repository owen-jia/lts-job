package com.github.ltsopensource.biz.logger.mongo;

import com.github.ltsopensource.biz.logger.JobLogBackup;
import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.JobLogPoBackup;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.commons.utils.DateUtils;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.store.mongo.MongoRepository;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;

/**
 * @author: Owen Jia
 * @time: 2020/5/27 13:08
 */
public class MongoJobLogBackup extends MongoRepository implements JobLogBackup {

    public MongoJobLogBackup(Config config) {
        super(config);
        setTableName("lts_job_log_po_backup");

        // create table
        DBCollection dbCollection = template.getCollection();
        List<DBObject> indexInfo = dbCollection.getIndexInfo();
        // create index if not exist
        if (CollectionUtils.sizeOf(indexInfo) <= 1) {
            template.ensureIndex("idx_table_name", "table_name");
            template.ensureIndex("idx_gmt_created", "taskId,gmt_created");
        }
    }

    @Override
    public List<JobLogPoBackup> findAll() {
        Query<JobLogPoBackup> query = template.createQuery(JobLogPoBackup.class);
        query.field("delFlag").equal(false);
        query.order("-gmtCreated");
        return query.asList();
    }

    @Override
    public boolean add(JobLogPoBackup backup) {
        return template.save(backup)!=null;
    }

    @Override
    public boolean remove(String tableName) {
        Query<JobLogPoBackup> query = template.createQuery(JobLogPoBackup.class);
        query.field("delFlag").equal(false);
        query.field("tableName").equal(tableName);

        UpdateOperations<JobLogPoBackup> operations = template.createUpdateOperations(JobLogPoBackup.class);
        operations.set("delFlag", true);
        operations.set("gmtModified", SystemClock.now());
        template.update(query, operations).getUpdatedCount();
        return template.update(query, operations).getUpdatedCount() > 1;
    }
}
