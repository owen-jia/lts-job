package com.github.ltsopensource.admin.web.api;

import com.github.ltsopensource.admin.cluster.BackendAppContext;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.web.AbstractMVC;
import com.github.ltsopensource.admin.web.support.Builder;
import com.github.ltsopensource.admin.web.vo.RestfulResponse;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        if(request.getPassword().isEmpty() || request.getId() <= 0)
            return Builder.build(false,"id和password为必填！");

        boolean isOk = appContext.getBackendAccountAccess().modifyInfoById(request);
        if(!isOk) {
            LOGGER.error("[/safe/modify-password] 密码修改异常 AccountReq:{}", request.toString());
            return Builder.build(false, "帐号不存在！");
        }
        return Builder.build(true,"密码修改成功！");
    }

}
