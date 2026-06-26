-- 核心表结构（新环境部署；已有库通过 Flyway baseline 跳过）
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `phone` VARCHAR(20) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(50) NOT NULL,
    `avatar_url` VARCHAR(512) DEFAULT NULL,
    `status_label` VARCHAR(100) DEFAULT NULL,
    `confusion_tags` VARCHAR(500) DEFAULT NULL,
    `companion_style` VARCHAR(20) DEFAULT NULL,
    `trust_level` INT NOT NULL DEFAULT 1,
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER',
    `is_frozen` TINYINT NOT NULL DEFAULT 0,
    `signed_community_rule` TINYINT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

CREATE TABLE IF NOT EXISTS `confusion_card` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(200) DEFAULT NULL,
    `event_description` TEXT NOT NULL,
    `emotion_tags` VARCHAR(500) DEFAULT NULL,
    `attempt_description` TEXT DEFAULT NULL,
    `need_type` VARCHAR(20) NOT NULL,
    `confusion_tags` VARCHAR(500) DEFAULT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    `resolution_content` TEXT DEFAULT NULL,
    `resolved_at` DATETIME DEFAULT NULL,
    `thanks_count` INT NOT NULL DEFAULT 0,
    `reply_count` INT NOT NULL DEFAULT 0,
    `view_count` INT NOT NULL DEFAULT 0,
    `heat_score` DOUBLE NOT NULL DEFAULT 0,
    `is_pinned` TINYINT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status_created` (`status`, `created_at`),
    INDEX `idx_heat_score` (`heat_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='困惑卡片';

CREATE TABLE IF NOT EXISTS `reply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `card_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `parent_id` BIGINT DEFAULT NULL,
    `experience_situation` TEXT NOT NULL,
    `experience_action` TEXT NOT NULL,
    `experience_result` TEXT NOT NULL,
    `reply_type` VARCHAR(20) NOT NULL DEFAULT 'EXPERIENCE',
    `thanks_count` INT NOT NULL DEFAULT 0,
    `is_hidden` TINYINT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_card_id` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卡片回复';

CREATE TABLE IF NOT EXISTS `thanks_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `target_type` VARCHAR(20) NOT NULL,
    `target_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='感谢记录';

CREATE TABLE IF NOT EXISTS `companion_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `creator_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `goal_description` TEXT NOT NULL,
    `confusion_tags` VARCHAR(500) DEFAULT NULL,
    `duration_days` INT NOT NULL DEFAULT 21,
    `checkin_frequency` VARCHAR(20) NOT NULL DEFAULT 'DAILY',
    `companion_style_preferred` VARCHAR(20) DEFAULT 'ANY',
    `status` VARCHAR(20) NOT NULL DEFAULT 'SEEKING',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_creator_id` (`creator_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='陪伴计划';

CREATE TABLE IF NOT EXISTS `companion_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `plan_id` BIGINT NOT NULL,
    `inviter_id` BIGINT NOT NULL,
    `invitee_id` BIGINT NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `started_at` DATETIME DEFAULT NULL,
    `ended_at` DATETIME DEFAULT NULL,
    `daily_checkin_count` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_plan_id` (`plan_id`),
    INDEX `idx_invitee_status` (`invitee_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='陪伴关系';

CREATE TABLE IF NOT EXISTS `private_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `relation_id` BIGINT NOT NULL,
    `sender_id` BIGINT NOT NULL,
    `receiver_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `content_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    `is_read` TINYINT NOT NULL DEFAULT 0,
    `is_recalled` TINYINT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_relation_created` (`relation_id`, `created_at`),
    INDEX `idx_receiver_unread` (`receiver_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私聊消息';
