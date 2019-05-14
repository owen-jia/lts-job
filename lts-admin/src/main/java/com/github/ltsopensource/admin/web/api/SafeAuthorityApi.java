package com.github.ltsopensource.admin.web.api;

import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.cluster.BackendAppContext;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.admin.support.AppConfigurer;
import com.github.ltsopensource.admin.support.ThreadLocalUtil;
import com.github.ltsopensource.admin.web.AbstractMVC;
import com.github.ltsopensource.admin.web.filter.LoginAuthFilter;
import com.github.ltsopensource.admin.web.support.Builder;
import com.github.ltsopensource.admin.web.vo.RestfulResponse;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import javafx.scene.control.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 安全和权限管理模块
 * @author: Owen Jia(owen-jia@outlook.com)
 * @time: 2019/5/5 15:57
 */
@RestController
public class SafeAuthorityApi extends AbstractMVC {
    private static final Logger LOGGER = LoggerFactory.getLogger(SafeAuthorityApi.class);
    @Autowired
    private BackendAppContext appContext;

    @RequestMapping("/safe/modify-password")
    public RestfulResponse modifyPassword(AccountReq request) {
        if(request ==null || request.getPassword().isEmpty() || request.getId() <= 0)
            return Builder.build(false,"id和password为必填！");

        Account account = appContext.getBackendAccountAccess().selectOne(request);
        if(!account.getPassword().equals(request.getOldPassword())){
            return Builder.build(false, "原密码不正确！");
        }

        boolean isOk = appContext.getBackendAccountAccess().modifyInfoById(request);
        if(!isOk) {
            LOGGER.error("[/safe/modify-password] 密码修改异常 AccountReq:{}", request.toString());
            return Builder.build(false, "帐号不存在！");
        }
        return Builder.build(true,"密码修改成功！");
    }

    @RequestMapping("/safe/account-get")
    public RestfulResponse queryAccountInfo() {
        AccountReq accountReq = new AccountReq();
        accountReq.setUsername(ThreadLocalUtil.getAttr("username").toString());
        Account account = appContext.getBackendAccountAccess().selectOne(accountReq);
        if(account != null){
            accountReq.setId(account.getId());
            accountReq.setEmail(account.getEmail());
        } else {
            return Builder.build(false,"请重新登陆或联系管理员");
        }
        RestfulResponse response = new RestfulResponse();
        response.setSuccess(true);
        response.setResults(1);

        List<AccountReq> accounts = new ArrayList<AccountReq>();
        accounts.add(accountReq);
        response.setRows(accounts);
        return response;
    }

    @RequestMapping("/safe/accounts-list")
    public RestfulResponse queryAccountList(AccountReq request) {
        if(!isAdminAuthoriy()){
            return Builder.build(false,"非管理员帐号登陆，禁止操作！");
        }

        AccountReq accountReq = new AccountReq();
        accountReq.setUsername(ThreadLocalUtil.getAttr("username").toString());
        PaginationRsp<Account> accounts = appContext.getBackendAccountAccess().select(request);
        for(Account account: accounts.getRows()){
            account.setPassword("***");
        }
        RestfulResponse response = new RestfulResponse();
        response.setSuccess(true);
        response.setResults(accounts.getResults());
        response.setRows(accounts.getRows());
        return response;
    }

    @RequestMapping("/safe/account-delete")
    public RestfulResponse deleteAccountList(AccountReq request) {
        if(!isAdminAuthoriy()){
            return Builder.build(false,"非管理员帐号登陆，禁止操作！");
        }

        //依托于id、username 进行删除，这两个在库表中都是唯一的
        appContext.getBackendAccountAccess().delete(request);
        return Builder.build(true);
    }

    @RequestMapping("/safe/account-add")
    public RestfulResponse accountAdd(AccountReq request) {
        if(!isAdminAuthoriy()){
            return Builder.build(false,"非管理员帐号登陆，禁止操作！");
        }

        request.setId(null);
        if(appContext.getBackendAccountAccess().selectOne(request) != null){
            return Builder.build(false,"帐号名称已经存在，请重新输入");
        }

        //验证是否存在该帐号
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(request.getPassword());
        account.setEmail(request.getEmail());
        //依托于id、username 进行删除，这两个在库表中都是唯一的
        appContext.getBackendAccountAccess().insert(Collections.singletonList(account));
        return Builder.build(true);
    }

    /**
     * 检测是否是管理员权限
     * @return
     */
    private boolean isAdminAuthoriy(){
        String loginName = ThreadLocalUtil.getAttr("username").toString();
        String defaultUsername = AppConfigurer.getProperty("console.username", LoginAuthFilter.getUsername());
        return defaultUsername.equals(loginName);
    }

}
