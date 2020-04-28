package com.github.ltsopensource.store.jdbc.builder;

import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.store.jdbc.SQLFormatter;
import com.github.ltsopensource.store.jdbc.SqlTemplate;
import com.github.ltsopensource.store.jdbc.exception.JdbcException;

/**
 * @author: Owen Jia
 * @time: 2020/4/28 15:56
 */
public class AlterSql {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlterSql.class);
    private SqlTemplate sqlTemplate;
    private StringBuilder sql = new StringBuilder();

    public AlterSql(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public AlterSql rename(String orgName,String tagName){
        sql.append("ALTER TABLE ").append(orgName).append(" RENAME ").append(tagName);
        return this;
    }

    public boolean doAlter() {
        String finalSQL = sql.toString();

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(SQLFormatter.format(finalSQL));
            }

            sqlTemplate.update(sql.toString());
        } catch (Exception e) {
            throw new JdbcException("Alter Table Error:" + SQLFormatter.format(finalSQL), e);
        }
        return true;
    }
}
