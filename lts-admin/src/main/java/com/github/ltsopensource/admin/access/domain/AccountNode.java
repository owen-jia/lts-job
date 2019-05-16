package com.github.ltsopensource.admin.access.domain;

import com.github.ltsopensource.core.cluster.NodeType;

import java.util.Date;

/**
 * 帐号节点权限
 * @author: Owen Jia
 * @time: 2019/5/15 16:14
 */
public class AccountNode {

    Integer id;
    NodeType nodeType;
    String nodeGroup;
    Integer userId;
    Date createTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AccountNode{" +
                "id=" + id +
                ", nodeType=" + nodeType +
                ", nodeGroup='" + nodeGroup + '\'' +
                ", userId=" + userId +
                ", createTime=" + createTime +
                '}';
    }
}
