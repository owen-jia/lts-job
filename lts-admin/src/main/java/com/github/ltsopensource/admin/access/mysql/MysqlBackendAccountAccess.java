package com.github.ltsopensource.admin.access.mysql;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.access.domain.NodeOnOfflineLog;
import com.github.ltsopensource.admin.access.face.BackendAccountAccess;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.monitor.access.mysql.MysqlAbstractJdbcAccess;
import com.github.ltsopensource.queue.domain.NodeGroupPo;
import com.github.ltsopensource.queue.mysql.MysqlPreLoader;
import com.github.ltsopensource.store.jdbc.builder.*;
import com.github.ltsopensource.store.jdbc.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Owen Jia
 * @time: 2019/3/19 13:32
 */
public class MysqlBackendAccountAccess extends MysqlAbstractJdbcAccess implements BackendAccountAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlBackendAccountAccess.class);

    public MysqlBackendAccountAccess(Config config) {
        super(config);
    }

    @Override
    protected String getTableName() {
        return "lts_admin_account";
    }

    @Override
    public void insert(List<Account> accounts) {
        InsertSql insertSql = new InsertSql(getSqlTemplate())
                .insert(getTableName())
                .columns("username",
                        "password",
                        "email");

        for (Account account : accounts) {
            insertSql.values(account.getUsername(),
                    account.getPassword(),
                    account.getEmail()
            );
        }
        insertSql.doBatchInsert();
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
        return new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .table(getTableName())
                .single();
    }

    @Override
    public List<Account> searchAll(AccountReq request) {
        return new SelectSql(getSqlTemplate())
                .select()
                .columns("id","username","email")
                .from()
                .table(getTableName())
                .whereSql(new WhereSql()
                        .andOnNotNull("email like %?%", request.getSearchKeys())
                        .orOnNotNull("username like %?%", request.getSearchKeys()))
                .list(new ResultSetHandler<List<Account>>() {
                    @Override
                    public List<Account> handle(ResultSet rs) throws SQLException {
                        List<Account> list = new ArrayList<Account>();
                        while (rs.next()){
                            Account account = new Account();
                            account.setId(rs.getInt("id"));
                            account.setUsername(rs.getString("username"));
                            account.setEmail(rs.getString("email"));
                            list.add(account);
                        }
                        return list;
                    }
                });
    }

    @Override
    public PaginationRsp<Account> select(AccountReq request) {
        PaginationRsp<Account> response = new PaginationRsp<Account>();
        Long results = new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .table(getTableName())
                .single();

        List<Account> accounts = new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .orderBy()
                .column("update_time", OrderByType.DESC)
                .limit(request.getStart(), request.getLimit())
                .list(RshHandler.ACCOUNT_LIST_RSH);

        response.setResults(results.intValue());
        response.setRows(accounts == null? new ArrayList<Account>(): accounts);
        return response;
    }

    @Override
    public void delete(AccountReq request) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .doDelete();
    }

    public WhereSql buildWhereSql(AccountReq request) {
        return new WhereSql()
                .andOnNotNull("id=?", request.getId())
                .andOnNotNull("username = ?", request.getUsername());
    }

    @Override
    public boolean modifyInfoById(AccountReq request) {
        try {
            return new UpdateSql(getSqlTemplate())
                    .update()
                    .table(getTableName())
                    .setOnNotNull("password", request.getPassword())
                    .where("id=?", request.getId())
                    .doUpdate() == 1;
        } catch (Exception e){
            LOGGER.error("Error when modify password:" + e.getMessage(), e);
            return false;
        }
    }
}
