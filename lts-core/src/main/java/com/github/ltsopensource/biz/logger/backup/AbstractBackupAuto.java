package com.github.ltsopensource.biz.logger.backup;

import com.github.ltsopensource.biz.logger.JobLogBackup;
import com.github.ltsopensource.biz.logger.JobLogger;
import com.github.ltsopensource.biz.logger.JobLoggerFactory;
import com.github.ltsopensource.biz.logger.domain.JobLogPoBackup;
import com.github.ltsopensource.biz.logger.domain.LogPoBackupResult;
import com.github.ltsopensource.core.AppContext;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.Callable;
import com.github.ltsopensource.core.commons.utils.DateUtils;
import com.github.ltsopensource.core.factory.NamedThreadFactory;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.spi.ServiceLoader;
import com.github.ltsopensource.core.support.NodeShutdownHook;
import com.github.ltsopensource.core.support.SystemClock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Owen Jia
 * @time: 2020/4/28 14:40
 */
public abstract class AbstractBackupAuto {
    private final Logger LOGGER = LoggerFactory.getLogger(AbstractBackupAuto.class);

    private ScheduledExecutorService LOAD_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("LTS-Log-Backup", true));
    @SuppressWarnings("unused")
    private ScheduledFuture<?> scheduledFuture;
    private AtomicBoolean start = new AtomicBoolean(false);

    JobLogBackup delegate;
    JobLogger jobLogger;

    public AbstractBackupAuto(final AppContext appContext) {
        if (start.compareAndSet(false, true)) {
            Config config = appContext.getConfig();
            JobLoggerFactory jobLoggerFactory = ServiceLoader.load(JobLoggerFactory.class, config);
            this.delegate = jobLoggerFactory.getJobLogBackup(config);
            this.jobLogger = jobLoggerFactory.getJobLogger(config);

            scheduledFuture = LOAD_EXECUTOR_SERVICE.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    LOGGER.info("----");
                    doExce();
                }
            }, 10, 10, TimeUnit.MILLISECONDS);

            NodeShutdownHook.registerHook(appContext, this.getClass().getName(), new Callable() {
                @Override
                public void call() throws Exception {
                    scheduledFuture.cancel(true);
                    LOAD_EXECUTOR_SERVICE.shutdown();
                    start.set(false);
                }
            });
        }
    }

    protected abstract void doExce();

    protected void doBackup(){
        LogPoBackupResult result = this.jobLogger.backup();
        if(result.isSuccess()){
            JobLogPoBackup jobLogPoBackup = new JobLogPoBackup();
            jobLogPoBackup.setDelFlag(false);
            jobLogPoBackup.setTableName(result.getNewTableName());
            jobLogPoBackup.setGmtCreated(DateUtils.currentTimeMillis());
            jobLogPoBackup.setGmtModified(SystemClock.now());

            this.delegate.add(jobLogPoBackup);
        }
    }
}
