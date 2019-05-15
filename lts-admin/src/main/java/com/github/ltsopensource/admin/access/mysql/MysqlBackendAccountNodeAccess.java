package com.github.ltsopensource.admin.access.mysql;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.access.domain.AccountNode;
import com.github.ltsopensource.admin.access.face.BackendAccountAccess;
import com.github.ltsopensource.admin.access.face.BackendAccountNodeAccess;
import com.github.ltsopensource.admin.request.AccountNodeReq;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.monitor.access.mysql.MysqlAbstractJdbcAccess;
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
public class MysqlBackendAccountNodeAccess extends MysqlAbstractJdbcAccess implements BackendAccountNodeAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlBackendAccountNodeAccess.class);

    public MysqlBackendAccountNodeAccess(Config config) {
        super(config);
    }

    @Override
    protected String getTableName() {
        return "lts_admin_account_node";
    }

    @Override
    public void insert(List<AccountNode> accountNodes) {
        InsertSql insertSql = new InsertSql(getSqlTemplate())
                .insert(getTableName())
                .columns("node_type",
                        "node_group",
                        "userId");

        for (AccountNode account : accountNodes) {
            insertSql.values(account.getNodeType().name(),
                    account.getNodeGroup(),
                    account.getUserId()
            );
        }
        insertSql.doBatchInsert();
    }

    @Override
    public AccountNode selectOne(AccountNodeReq request) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .single(RshHandler.ACCOUNT_NODE_RSH);
    }

    @Override
    public List<AccountNode> searchAll(AccountNodeReq request) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .list(RshHandler.ACCOUNT_NODE_LIST_RSH);
    }

    @Override
    public PaginationRsp<AccountNode> select(AccountNodeReq request) {
        PaginationRsp<AccountNode> response = new PaginationRsp<AccountNode>();

        Long results = new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .table(getTableName())
                .single();

        List<AccountNode> accounts = new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName())
                .orderBy()
                .column("create_time", OrderByType.DESC)
                .limit(request.getStart(), request.getLimit())
                .list(RshHandler.ACCOUNT_NODE_LIST_RSH);

        response.setResults(results.intValue());
        response.setRows(accounts == null? new ArrayList<AccountNode>(): accounts);
        return response;
    }

    @Override
    public void delete(AccountNodeReq request) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .doDelete();
    }

    public WhereSql buildWhereSql(AccountNodeReq request) {
        return new WhereSql()
                .andOnNotNull("id=?", request.getId())
                .andOnNotNull("userId=?", request.getUserId())
                .andOnNotNull("node_type = ?", request.getNodeType() == null?null:request.getNodeType().name());
    }
}
