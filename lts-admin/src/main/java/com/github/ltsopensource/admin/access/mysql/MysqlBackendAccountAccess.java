package com.github.ltsopensource.admin.access.mysql;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.access.face.BackendAccountAccess;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.mysql.MysqlAbstractJdbcAccess;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;

import java.util.List;

/**
 * @author: Owen Jia
 * @time: 2019/3/19 13:32
 */
public class MysqlBackendAccountAccess extends MysqlAbstractJdbcAccess implements BackendAccountAccess {

    public MysqlBackendAccountAccess(Config config) {
        super(config);
    }

    @Override
    protected String getTableName() {
        return "lts_admin_account";
    }

    @Override
    public void insert(List<Account> nodeOnOfflineLogs) {

    }

    @Override
    public Account selectOne(AccountReq request) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .single(RshHandler.ACCOUNT_RSH);
    }

    @Override
    public Long count(AccountReq request) {
        return null;
    }

    @Override
    public void delete(AccountReq request) {

    }

    public WhereSql buildWhereSql(AccountReq request) {
        return new WhereSql()
                .andOnNotNull("username = ?", request.getUsername());
    }

}
