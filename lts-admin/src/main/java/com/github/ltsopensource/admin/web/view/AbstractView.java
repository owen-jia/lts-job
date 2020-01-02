package com.github.ltsopensource.admin.web.view;

import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.access.domain.AccountNode;
import com.github.ltsopensource.admin.cluster.BackendAppContext;
import com.github.ltsopensource.admin.request.AccountNodeReq;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.support.ThreadLocalUtil;
import com.github.ltsopensource.queue.domain.NodeGroupPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Owen Jia
 * @time: 2020/1/2 16:04
 */
public abstract class AbstractView {
    @Autowired
    protected BackendAppContext appContext;

    @ModelAttribute
    protected void setModel(Model model){
        model.addAttribute("authority", ThreadLocalUtil.getAttr("authority"));
        model.addAttribute("username", ThreadLocalUtil.getAttr("username"));
    }

    /**
     * 节点数据根据帐号权限过滤
     * @param nodeGroupPos
     * @return
     */
    protected List<NodeGroupPo> dataAuthority(List<NodeGroupPo> nodeGroupPos){
        List<NodeGroupPo> result = new ArrayList<NodeGroupPo>();
        if(!(Boolean) ThreadLocalUtil.getAttr("authority")) {
            String loginUsername = ThreadLocalUtil.getAttr("username").toString();
            AccountReq accountReq = new AccountReq();
            accountReq.setUsername(loginUsername);
            Account account = appContext.getBackendAccountAccess().selectOne(accountReq);

            AccountNodeReq accountNodeReq = new AccountNodeReq();
            accountNodeReq.setUserId(account.getId());
            List<AccountNode> accountNodes = appContext.getBackendAccountNodeAccess().searchAll(accountNodeReq);
            if(accountNodes != null && accountNodes.size()> 0 && nodeGroupPos != null && nodeGroupPos.size() > 0){
                for(NodeGroupPo nodeGroupPo: nodeGroupPos){
                    for(AccountNode accountNode: accountNodes){
                        if(accountNode.getNodeGroup().equals(nodeGroupPo.getName())){
                            result.add(nodeGroupPo);//权限拥有
                            break;
                        }
                    }
                }
            }
        } else {
            result.addAll(nodeGroupPos);//管理员全部可见
        }
        return result;
    }
}
