-- Tier 2: 举报记录表（若库中尚无此表，请执行本脚本）
CREATE TABLE IF NOT EXISTS `report_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `reporter_id` BIGINT NOT NULL COMMENT '举报者ID',
    `target_type` VARCHAR(20) NOT NULL COMMENT 'CARD/REPLY/MESSAGE/USER',
    `target_id` BIGINT NOT NULL COMMENT '目标ID',
    `reason` VARCHAR(50) NOT NULL COMMENT 'HARASSMENT/FAKE_INFO/HATE_SPEECH/OTHER',
    `description` TEXT DEFAULT NULL COMMENT '补充说明',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/RESOLVED/DISMISSED',
    `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `handle_result` VARCHAR(50) DEFAULT NULL COMMENT '处理结果',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报记录表';

-- 可选：将某用户设为管理员（替换为实际用户 ID）
-- UPDATE `user` SET `role` = 'ADMIN' WHERE `id` = 1;
