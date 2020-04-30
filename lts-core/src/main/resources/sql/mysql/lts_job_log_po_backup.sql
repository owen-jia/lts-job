CREATE TABLE IF NOT EXISTS `lts_job_log_po_backup` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `table_name` varchar(30) COMMENT '历史日志表名',
  `del_flag` tinyint(1) COMMENT '是否已删除',
  `gmt_created` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(11) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_table_name` (`table_name`),
  KEY `idx_gmt_created` (`gmt_created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务日志历史表';