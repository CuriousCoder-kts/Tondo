-- 兼容早期手动建表、缺少 avatar_url 字段的库
DROP PROCEDURE IF EXISTS tondo_add_avatar_url_column;
DELIMITER //
CREATE PROCEDURE tondo_add_avatar_url_column()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'user'
          AND column_name = 'avatar_url'
    ) THEN
        ALTER TABLE `user` ADD COLUMN `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像URL';
    END IF;
END //
DELIMITER ;
CALL tondo_add_avatar_url_column();
DROP PROCEDURE tondo_add_avatar_url_column;
