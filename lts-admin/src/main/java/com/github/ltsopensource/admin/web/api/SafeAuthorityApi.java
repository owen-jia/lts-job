package com.github.ltsopensource.admin.web.api;

import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.cluster.BackendAppContext;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.support.ThreadLocalUtil;
import com.github.ltsopensource.admin.web.AbstractMVC;
import com.github.ltsopensource.admin.web.support.Builder;
import com.github.ltsopensource.admin.web.vo.RestfulResponse;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全和权限管理模块
 * @author: Owen Jia(owen-jia@outlook.com)
 * @time: 2019/5/5 15:57
 */
@RestController
public class SafeAuthorityApi extends AbstractMVC {
    private static final Logger LOGGER = LoggerFactory.getLogger(SafeAuthorityApi.class);
    private static final String AUTH_PREFIX = "Basic ";
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
//        String authorization = request.getHeader("authorization");
//        authorization = authorization.substring(AUTH_PREFIX.length(), authorization.length());
//        String usernameAndPassword = new String(Base64.decodeFast(authorization));
//        String username = usernameAndPassword.split(":")[0];

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

}
