package com.github.ltsopensource.admin.access.face;

import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.core.domain.NodeGroupGetReq;
import com.github.ltsopensource.queue.domain.NodeGroupPo;

import java.util.List;

/**
 * 帐户表查询接口
 * @author: Owen Jia
 * @time: 2019/3/19 13:41
 */
public interface BackendAccountAccess {

    void insert(List<Account> accounts);

    Account selectOne(AccountReq request);

    /**
     * 分页查询帐号
     * @return
     */
    PaginationRsp<Account> select(AccountReq request);

    Long count(AccountReq request);

    void delete(AccountReq request);

    /**
     * 根据id修改信息
     * @param request
     */
    boolean modifyInfoById(AccountReq request);
}
