package com.github.ltsopensource.admin.web.api;

import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.access.domain.AccountNode;
import com.github.ltsopensource.admin.cluster.BackendAppContext;
import com.github.ltsopensource.admin.request.AccountNodeReq;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.admin.support.AppConfigurer;
import com.github.ltsopensource.admin.support.ThreadLocalUtil;
import com.github.ltsopensource.admin.web.AbstractMVC;
import com.github.ltsopensource.admin.web.filter.LoginAuthFilter;
import com.github.ltsopensource.admin.web.support.Builder;
import com.github.ltsopensource.admin.web.support.PasswordUtil;
import com.github.ltsopensource.admin.web.vo.RestfulResponse;
import com.github.ltsopensource.core.cluster.NodeType;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.queue.domain.NodeGroupPo;
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
        if(!PasswordUtil.conformPassword(request.getOldPassword(), account.getPassword())){
            return Builder.build(false, "原密码不正确！");
        }

        request.setPassword(PasswordUtil.encode(request.getPassword()));

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

        for(Account account : accounts.getRows()){
            account.setPassword("******");
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

        //清空帐号权限配置
        AccountNodeReq accountNodeReq = new AccountNodeReq();
        accountNodeReq.setUserId(request.getId());
        appContext.getBackendAccountNodeAccess().delete(accountNodeReq);
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
        account.setPassword(PasswordUtil.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        //依托于id、username 进行删除，这两个在库表中都是唯一的
        appContext.getBackendAccountAccess().insert(Collections.singletonList(account));
        return Builder.build(true);
    }

    @RequestMapping("/safe/account/search")
    public RestfulResponse searchAccount(AccountReq request){
        if(!isAdminAuthoriy()){
            return Builder.build(false,"非管理员帐号登陆，禁止操作！");
        }

        List<Account> accounts = appContext.getBackendAccountAccess().searchAll(request);
        RestfulResponse response = new RestfulResponse();
        response.setSuccess(true);
        response.setResults(accounts.size());
        response.setRows(accounts);
        return response;
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

    /**
     * 查询用户具有的NodeGroup列表
     * @param request
     * @return
     */
    @RequestMapping("/safe/account/node-get")
    public RestfulResponse queryNodes(AccountNodeReq request){
        if(!isAdminAuthoriy()){
            return Builder.build(false,"非管理员帐号登陆，禁止操作！");
        }
        if(request.getUserId() <= 0){
            return Builder.build(false,"userId 不合法");
        }

        request.setId(null);
        request.setNodeType(null);
        request.setNodeGroup(null);
        PaginationRsp<AccountNode> accountNodes = appContext.getBackendAccountNodeAccess().select(request);
        RestfulResponse response = new RestfulResponse();
        response.setSuccess(true);
        response.setResults(accountNodes.getResults());
        response.setRows(accountNodes.getRows());
        return response;
    }

    @RequestMapping("/safe/account/node-add")
    public RestfulResponse accountNodeAdd(AccountNodeReq req){
        if(!isAdminAuthoriy()){
            return Builder.build(false,"非管理员帐号登陆，禁止操作！");
        }
        if(req.getUserId() == null || req.getUserId() <= 0 || req.getNodeGroup() == null || req.getNodeGroup().isEmpty()|| req.getNodeType() == null)
            return Builder.build(false,"参数不合法，userId&nodeType&nodeGroup");

        AccountNode account = appContext.getBackendAccountNodeAccess().selectOne(req);
        if(account != null && account.getUserId().equals(req.getUserId())){
            LOGGER.warn("该用户节点权限已配置：{}",req.toString());
            return Builder.build(false,"已拥有该节点权限");
        } else {
            AccountNode accountNode = new AccountNode();
            accountNode.setUserId(req.getUserId());
            accountNode.setNodeGroup(req.getNodeGroup());
            accountNode.setNodeType(req.getNodeType());
            appContext.getBackendAccountNodeAccess().insert(Collections.singletonList(accountNode));
        }
        return Builder.build(true);
    }

    @RequestMapping("/safe/account/node-delete")
    public RestfulResponse accountNodeDelete(AccountNodeReq req){
        if(!isAdminAuthoriy()){
            return Builder.build(false,"非管理员帐号登陆，禁止操作！");
        }
        if(req.getId() == null || req.getId() <= 0)
            return Builder.build(false,"参数id不合法");

        req.setNodeType(null);
        req.setNodeGroup(null);
        appContext.getBackendAccountNodeAccess().delete(req);
        return Builder.build(true, "删除成功");
    }

    @RequestMapping("/safe/node/all-get")
    public RestfulResponse queryNodeGroup(){
        List<NodeGroupPo> nodeGroups = appContext.getNodeGroupStore().getNodeGroup(NodeType.JOB_CLIENT);
        nodeGroups.addAll(appContext.getNodeGroupStore().getNodeGroup(NodeType.TASK_TRACKER));

        return null;
    }

}
