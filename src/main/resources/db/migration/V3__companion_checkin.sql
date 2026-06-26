-- Incremental migration: companion check-in
CREATE TABLE IF NOT EXISTS `companion_checkin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `relation_id` BIGINT NOT NULL COMMENT '陪伴关系ID',
    `user_id` BIGINT NOT NULL COMMENT '打卡用户ID',
    `checkin_date` DATE NOT NULL COMMENT '打卡日期（自然日）',
    `note` VARCHAR(500) DEFAULT NULL COMMENT '今日一句记录',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_relation_user_date` (`relation_id`, `user_id`, `checkin_date`),
    INDEX `idx_relation_id` (`relation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='陪伴打卡记录';
