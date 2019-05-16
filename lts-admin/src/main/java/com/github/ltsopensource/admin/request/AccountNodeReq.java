package com.github.ltsopensource.admin.request;

import com.github.ltsopensource.core.cluster.NodeType;

/**
 * 帐号节点权限表查询参数
 * @author: Owen Jia
 * @time: 2019/5/15 16:24
 */
public class AccountNodeReq extends PaginationReq{

    Integer id;
    NodeType nodeType;
    String nodeGroup;
    Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeGroup() {
        return nodeGroup;
    }

    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AccountNodeReq{" +
                "id=" + id +
                ", nodeType=" + nodeType +
                ", nodeGroup='" + nodeGroup + '\'' +
                ", userId=" + userId +
                '}';
    }
}
