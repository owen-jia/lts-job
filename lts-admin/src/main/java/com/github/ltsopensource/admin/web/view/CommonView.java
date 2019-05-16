package com.github.ltsopensource.admin.web.view;

import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.access.domain.AccountNode;
import com.github.ltsopensource.admin.cluster.BackendAppContext;
import com.github.ltsopensource.admin.request.AccountNodeReq;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.core.cluster.NodeType;
import com.github.ltsopensource.core.commons.utils.DateUtils;
import com.github.ltsopensource.admin.support.ThreadLocalUtil;
import com.github.ltsopensource.queue.domain.NodeGroupPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Robert HG (254963746@qq.com) on 6/6/15.
 */
@Controller
public class CommonView {

    @Autowired
    private BackendAppContext appContext;

    @ModelAttribute
    public void setModel(Model model){
        model.addAttribute("authority", ThreadLocalUtil.getAttr("authority"));
        model.addAttribute("username", ThreadLocalUtil.getAttr("username"));
    }

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("node-manager")
    public String nodeManagerUI() {
        return "nodeManager";
    }

    @RequestMapping("node-group-manager")
    public String nodeGroupManagerUI() {
        return "nodeGroupManager";
    }

    @RequestMapping("node-onoffline-log")
    public String nodeOnOfflineLogUI(Model model) {
        model.addAttribute("startLogTime", DateUtils.formatYMD_HMS(DateUtils.addDay(new Date(), -10)));
        model.addAttribute("endLogTime", DateUtils.formatYMD_HMS(new Date()));
        return "nodeOnOfflineLog";
    }

    @RequestMapping("node-jvm-info")
    public String nodeJVMInfo(Model model, String identity) {
        model.addAttribute("identity", identity);
        return "nodeJvmInfo";
    }

    @RequestMapping("job-add")
    public String addJobUI(Model model) {
        setAttr(model);
        return "jobAdd";
    }

    @RequestMapping("job-logger")
    public String jobLoggerUI(Model model, String realTaskId, String taskTrackerNodeGroup,
                              Date startLogTime, Date endLogTime) {
        model.addAttribute("realTaskId", realTaskId);
        model.addAttribute("taskTrackerNodeGroup", taskTrackerNodeGroup);
        if (startLogTime == null) {
            startLogTime = DateUtils.addMinute(new Date(), -10);
        }
        model.addAttribute("startLogTime", DateUtils.formatYMD_HMS(startLogTime));
        if (endLogTime == null) {
            endLogTime = new Date();
        }
        model.addAttribute("endLogTime", DateUtils.formatYMD_HMS(endLogTime));
        setAttr(model);
        return "jobLogger";
    }

    @RequestMapping("cron-job-queue")
    public String cronJobQueueUI(Model model) {
        setAttr(model);
        return "cronJobQueue";
    }

    @RequestMapping("repeat-job-queue")
    public String repeatJobQueueUI(Model model) {
        setAttr(model);
        return "repeatJobQueue";
    }

    @RequestMapping("executable-job-queue")
    public String executableJobQueueUI(Model model) {
        setAttr(model);
        return "executableJobQueue";
    }

    @RequestMapping("executing-job-queue")
    public String executingJobQueueUI(Model model) {
        setAttr(model);
        return "executingJobQueue";
    }

    @RequestMapping("load-job")
    public String loadJobUI(Model model) {
        setAttr(model);
        return "loadJob";
    }

    @RequestMapping("cron_generator_iframe")
    public String cronGeneratorIframe(Model model){
        return "cron/cronGenerator";
    }

	@RequestMapping("suspend-job-queue")
	public String suspendJobQueueUI(Model model) {
		setAttr(model);
		return "suspendJobQueue";
	}

    /**
     * 跳转修改密码页面
     * @return
     */
    @RequestMapping("/authority/modify-password")
    public String modifyPasswordUI() {
        return "authority/modifyPassword";
    }

    /**
     * 跳转权限中心页面
     * @return
     */
    @RequestMapping("/authority/authority-manage")
    public String authorityManage(Model model) {
        AccountReq request = new AccountReq();
        List<Account> accounts = appContext.getBackendAccountAccess().searchAll(request);
        model.addAttribute("accounts", accounts);
        return "authority/authorityManage";
    }

    /**
     * 跳转权限中心页面
     * @return
     */
    @RequestMapping("/authority/account-manage")
    public String accountManage(Model model) {
        setAttr(model);
        return "authority/accountManage";
    }

    private void setAttr(Model model) {
        List<NodeGroupPo> jobClientNodeGroups = appContext.getNodeGroupStore().getNodeGroup(NodeType.JOB_CLIENT);
        model.addAttribute("jobClientNodeGroups", dataAuthority(jobClientNodeGroups));
        List<NodeGroupPo> taskTrackerNodeGroups = appContext.getNodeGroupStore().getNodeGroup(NodeType.TASK_TRACKER);
        model.addAttribute("taskTrackerNodeGroups", dataAuthority(taskTrackerNodeGroups));
        model.addAttribute("taskTrackerNodeGroupsAll", taskTrackerNodeGroups);
    }

    /**
     * 节点数据根据帐号权限过滤
     * @param nodeGroupPos
     * @return
     */
    private List<NodeGroupPo> dataAuthority(List<NodeGroupPo> nodeGroupPos){
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
