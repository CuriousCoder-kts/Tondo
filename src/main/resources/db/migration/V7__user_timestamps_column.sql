-- 兼容早期手动建表缺少时间字段的库（MySQL 5.7 严格模式安全）
DROP PROCEDURE IF EXISTS tondo_add_user_timestamps;
DELIMITER //
CREATE PROCEDURE tondo_add_user_timestamps()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'user' AND column_name = 'created_at'
    ) THEN
        ALTER TABLE `user`
            ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'user' AND column_name = 'updated_at'
    ) THEN
        ALTER TABLE `user`
            ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
    END IF;
END //
DELIMITER ;
CALL tondo_add_user_timestamps();
DROP PROCEDURE IF EXISTS tondo_add_user_timestamps;

-- 仅修复 NULL（勿比较 0000-00-00，MySQL 5.7 严格模式会报错）
UPDATE `user` SET `created_at` = NOW() WHERE `created_at` IS NULL;
UPDATE `user` SET `updated_at` = NOW() WHERE `updated_at` IS NULL;
