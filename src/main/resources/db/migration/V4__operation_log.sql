-- Admin operation audit log (enterprise governance)
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `operator_id` BIGINT NOT NULL COMMENT '操作人用户ID',
    `action` VARCHAR(64) NOT NULL COMMENT '动作标识',
    `target_type` VARCHAR(32) DEFAULT NULL COMMENT '目标类型',
    `target_id` BIGINT DEFAULT NULL COMMENT '目标ID',
    `detail` VARCHAR(500) DEFAULT NULL COMMENT '详情摘要',
    `ip` VARCHAR(64) DEFAULT NULL COMMENT '客户端IP',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_operator` (`operator_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理操作审计日志';
