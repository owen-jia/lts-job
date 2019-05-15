CREATE TABLE IF NOT EXISTS `lts_admin_account_node`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `node_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点类型',
  `node_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点组',
  `userId` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `index_authority`(`userId`,`node_type`,`node_group`) USING BTREE COMMENT '用户节点权限唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户节点权限配置表';
