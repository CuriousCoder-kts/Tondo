CREATE TABLE IF NOT EXISTS `notification_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '接收者',
    `type` VARCHAR(32) NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `content` VARCHAR(500) DEFAULT NULL,
    `relation_id` BIGINT DEFAULT NULL,
    `plan_id` BIGINT DEFAULT NULL,
    `sender_id` BIGINT DEFAULT NULL,
    `is_read` TINYINT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_read` (`user_id`, `is_read`),
    INDEX `idx_user_created` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内通知收件箱';
