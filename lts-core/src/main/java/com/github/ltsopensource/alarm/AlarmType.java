package com.github.ltsopensource.alarm;

/**
 * 系统能够支持的所有告警
 * @author owen-jia 2020-06-01
 */
public enum AlarmType {
    NODE_GROUP_OFFLINE("节点组离线");

    String title;

    AlarmType(String title) {
        this.title = title;
    }
}
