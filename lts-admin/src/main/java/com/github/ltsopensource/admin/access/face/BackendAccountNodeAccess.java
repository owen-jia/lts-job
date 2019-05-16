package com.github.ltsopensource.admin.access.face;

import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.access.domain.AccountNode;
import com.github.ltsopensource.admin.request.AccountNodeReq;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.response.PaginationRsp;

import java.util.List;

/**
 * 帐号节点权限表
 * @author: Owen Jia
 * @time: 2019/3/19 13:41
 */
public interface BackendAccountNodeAccess {

    void insert(List<AccountNode> accounts);

    AccountNode selectOne(AccountNodeReq request);

    /**
     * 分页查询帐号
     * @return
     */
    PaginationRsp<AccountNode> select(AccountNodeReq request);

    /**
     * 关键字搜索
     * @param request
     * @return
     */
    List<AccountNode> searchAll(AccountNodeReq request);

    void delete(AccountNodeReq request);

}
