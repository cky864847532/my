-- MySQL 初始化脚本：创建数据库并授权（根据需要修改用户名/密码）
CREATE DATABASE IF NOT EXISTS `root` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- 使用前请修改用户与密码，或在本机创建相应用户
-- GRANT ALL ON `root`.* TO 'root'@'localhost';

USE `root`;

-- 管理员用户初始化（密码需用BCrypt加密，示例为123456的加密值）
INSERT INTO users (username, password, roles, created_at) VALUES ('admin', '$2a$10$7KcZ.EW9WZ0K/8M.RQ5vcO7SE1L/1W9mJshlb38O0TkFLUJ.T6Phm', 'ROLE_ADMIN', NOW())
	ON DUPLICATE KEY UPDATE username=username;
