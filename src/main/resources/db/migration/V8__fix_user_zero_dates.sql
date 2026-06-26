-- 修复 MySQL 5.7 中 user 表无效的零日期（V7 已跳过此类 UPDATE）
SET @OLD_SQL_MODE = @@SESSION.SQL_MODE;
SET SESSION SQL_MODE = CONCAT(IFNULL(@OLD_SQL_MODE, ''), ',ALLOW_INVALID_DATES');

UPDATE `user` SET `created_at` = NOW()
WHERE `created_at` IS NOT NULL AND YEAR(`created_at`) = 0;

UPDATE `user` SET `updated_at` = NOW()
WHERE `updated_at` IS NOT NULL AND YEAR(`updated_at`) = 0;

SET SESSION SQL_MODE = @OLD_SQL_MODE;
