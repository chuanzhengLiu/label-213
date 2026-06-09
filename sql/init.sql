-- 景区票务管理系统数据库初始化脚本
-- 数据库：scenic_manager
-- 字符集：utf8mb4

-- 设置客户端连接字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `scenic_manager` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `scenic_manager`;

-- 设置当前数据库字符集
SET character_set_database = utf8mb4;
SET character_set_server = utf8mb4;

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `role` INT NOT NULL COMMENT '角色：1-超级管理员，2-普通管理员',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 景区表
DROP TABLE IF EXISTS `scenic_spot`;
CREATE TABLE `scenic_spot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '景区名称',
  `intro` TEXT COMMENT '景区简介',
  `area` VARCHAR(100) COMMENT '所在区域',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景区表';

-- 票种表
DROP TABLE IF EXISTS `ticket_type`;
CREATE TABLE `ticket_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scenic_id` BIGINT NOT NULL COMMENT '关联景区ID',
  `type_name` VARCHAR(100) NOT NULL COMMENT '票种名称',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
  `valid_start` DATE COMMENT '有效期开始时间',
  `valid_end` DATE COMMENT '有效期结束时间',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_scenic_id` (`scenic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='票种表';

-- 订单表
DROP TABLE IF EXISTS `ticket_order`;
CREATE TABLE `ticket_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(20) NOT NULL COMMENT '订单编号（唯一）',
  `scenic_id` BIGINT NOT NULL COMMENT '关联景区ID',
  `ticket_type_id` BIGINT NOT NULL COMMENT '关联票种ID',
  `user_name` VARCHAR(50) NOT NULL COMMENT '购票人姓名',
  `user_phone` VARCHAR(20) NOT NULL COMMENT '购票人手机号',
  `ticket_num` INT NOT NULL COMMENT '购票数量',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `order_status` INT NOT NULL DEFAULT 1 COMMENT '订单状态：1-已下单，2-已核销，3-已取消',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_scenic_id` (`scenic_id`),
  KEY `idx_ticket_type_id` (`ticket_type_id`),
  KEY `idx_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 插入测试数据

-- 插入用户（密码使用BCrypt加密，123456对应的加密值）
-- 超级管理员：admin/123456
-- 普通管理员：user/123456
INSERT INTO `sys_user` (`username`, `password`, `role`) VALUES
('admin', '$2a$10$oIj0QAxjg04AJ6FBXXWXfea6l6Zb6zFBwXiaTkuDY7m.VawD9Y5ZC', 1),
('user', '$2a$10$oIj0QAxjg04AJ6FBXXWXfea6l6Zb6zFBwXiaTkuDY7m.VawD9Y5ZC', 2);

-- 插入测试景区
INSERT INTO `scenic_spot` (`name`, `intro`, `area`) VALUES
('测试景区', '测试用景区简介', '测试区'),
('北京故宫', '明清两朝的皇家宫殿，世界文化遗产', '北京市'),
('杭州西湖', '中国著名的风景游览胜地', '杭州市'),
('黄山风景区', '以奇松、怪石、云海、温泉著称', '黄山市');

-- 插入测试票种（关联景区ID=1）
INSERT INTO `ticket_type` (`scenic_id`, `type_name`, `price`, `stock`, `valid_start`, `valid_end`) VALUES
(1, '成人票', 99.00, 100, '2026-01-01', '2026-12-31'),
(1, '儿童票', 49.00, 50, '2026-01-01', '2026-12-31'),
(2, '成人票', 60.00, 200, '2026-01-01', '2026-12-31'),
(2, '学生票', 30.00, 100, '2026-01-01', '2026-12-31'),
(3, '成人票', 0.00, 999, '2026-01-01', '2026-12-31'),
(4, '成人票', 190.00, 150, '2026-01-01', '2026-12-31');
