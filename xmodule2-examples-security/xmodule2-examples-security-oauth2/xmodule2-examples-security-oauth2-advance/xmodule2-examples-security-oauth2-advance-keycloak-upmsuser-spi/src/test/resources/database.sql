/*
Navicat MySQL Data Transfer

Source Server         : mysql-127.0.0.1
Source Server Version : 50617
Source Host           : 127.0.0.1:3306
Source Database       : xmodule

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2019-12-19 17:13:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for upms_resource
-- ----------------------------
DROP TABLE IF EXISTS `upms_resource`;
CREATE TABLE `upms_resource` (
  `resource_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源id',
  `resource_name` varchar(100) NOT NULL COMMENT '资源名称',
  `parent_resource_id` bigint(20) NOT NULL COMMENT '父级资源id',
  `app_root_resource` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是应用的根节点：1-是，0-否',
  `resource_url` varchar(512) DEFAULT NULL COMMENT '资源URL',
  `resource_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '资源类型：0-系统资源,1-普通资源',
  `http_method` varchar(20) NOT NULL DEFAULT 'GET' COMMENT 'HTTP方法(GET,POST,DELETE,PUT)',
  `action_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '功能类型：0-菜单，1-按钮',
  `siblings_index` int(11) NOT NULL COMMENT '兄弟节点间的排序号,asc排序',
  `resource_icon` varchar(255) DEFAULT 'fa-circle-o' COMMENT '资源菜单ICON(font-awesome类名)',
  `index_page` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是首页：1-是，0-否',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` bigint(20) NOT NULL COMMENT '创建者,用户表id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者,用户表的id',
  PRIMARY KEY (`resource_id`),
  UNIQUE KEY `upms_resource_uk_resource_name` (`resource_name`,`parent_resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COMMENT='用户权限管理系统-资源菜单表';

-- ----------------------------
-- Records of upms_resource
-- ----------------------------
INSERT INTO `upms_resource` VALUES ('100', '统一用户权限管理系统', '1', '1', null, '0', 'GET', '0', '1', 'fa-circle-o', '0', '2018-10-24 10:02:47', '1', null, null);
INSERT INTO `upms_resource` VALUES ('101', '用户管理', '100', '0', '/user/index.html', '0', 'GET', '0', '1', 'fa-circle-o', '0', '2018-10-24 10:03:48', '1', null, null);
INSERT INTO `upms_resource` VALUES ('102', '角色管理', '100', '0', '/role/index.html', '0', 'GET', '0', '2', 'fa-circle-o', '0', '2018-10-24 10:04:24', '1', null, null);
INSERT INTO `upms_resource` VALUES ('103', '资源管理', '100', '0', '/resource/index.html', '0', 'GET', '0', '3', 'fa-circle-o', '0', '2018-10-24 10:04:52', '1', null, null);
INSERT INTO `upms_resource` VALUES ('104', '系统设置', '100', '0', null, '0', 'GET', '0', '4', 'fa-circle-o', '0', '2018-10-24 10:07:37', '1', null, null);
INSERT INTO `upms_resource` VALUES ('105', '缓存设置', '104', '0', '/settings/cache/index.html', '0', 'GET', '0', '1', 'fa-circle-o', '0', '2018-10-24 10:08:14', '1', null, null);
INSERT INTO `upms_resource` VALUES ('106', '参数设置', '104', '0', '/settings/param/index.html', '0', 'GET', '0', '2', 'fa-circle-o', '0', '2018-10-24 10:08:42', '1', null, null);
INSERT INTO `upms_resource` VALUES ('107', '测试菜单1', '100', '0', null, '0', 'GET', '0', '5', 'fa-circle-o', '0', '2018-10-30 15:45:34', '1', null, null);
INSERT INTO `upms_resource` VALUES ('108', '测试菜单11', '107', '0', '/test1/test11.html', '0', 'GET', '0', '1', 'fa-circle-o', '0', '2018-10-30 15:46:20', '1', null, null);
INSERT INTO `upms_resource` VALUES ('109', '测试菜单12', '107', '0', '/test1/test12.html', '0', 'GET', '0', '2', 'fa-circle-o', '0', '2018-10-30 15:46:47', '1', null, null);
INSERT INTO `upms_resource` VALUES ('110', '测试菜单2', '100', '0', '', '0', 'GET', '0', '6', 'fa-circle-o', '0', '2018-10-30 15:57:54', '1', null, null);
INSERT INTO `upms_resource` VALUES ('111', '测试菜单21', '110', '0', '/test2/test21.html', '0', 'GET', '0', '1', 'fa-circle-o', '0', '2018-10-30 15:58:40', '1', null, null);

-- ----------------------------
-- Table structure for upms_role
-- ----------------------------
DROP TABLE IF EXISTS `upms_role`;
CREATE TABLE `upms_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(100) NOT NULL COMMENT '角色名称',
  `role_code` varchar(100) NOT NULL COMMENT '角色代码,由字母、下划线组成',
  `role_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '角色类型：0-系统角色,1-普通角色',
  `description` varchar(512) NOT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` bigint(20) NOT NULL COMMENT '创建者,用户表的id',
  `update_time` datetime DEFAULT NULL COMMENT '最近更新时间',
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `upms_role_uk_role_name` (`role_name`),
  UNIQUE KEY `upms_role_uk_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户权限管理系统-角色表';

-- ----------------------------
-- Records of upms_role
-- ----------------------------
INSERT INTO `upms_role` VALUES ('1', '超级管理员', 'SUPER_ADMIN', '0', '系统管理员角色', '2018-10-24 10:13:31', '1', null, null);
INSERT INTO `upms_role` VALUES ('2', '管理员', 'ADMIN', '0', '管理员', '2018-10-25 10:45:02', '1', null, null);
INSERT INTO `upms_role` VALUES ('3', '测试员', 'TESTER', '0', '测试员', '2018-10-30 15:50:26', '1', null, null);

-- ----------------------------
-- Table structure for upms_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `upms_role_resource`;
CREATE TABLE `upms_role_resource` (
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `resource_id` bigint(20) NOT NULL COMMENT '资源id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` bigint(20) NOT NULL COMMENT '创建者,用户表id',
  PRIMARY KEY (`role_id`,`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限管理系统-角色.资源关系表';

-- ----------------------------
-- Records of upms_role_resource
-- ----------------------------
INSERT INTO `upms_role_resource` VALUES ('1', '100', '2018-10-24 10:19:52', '1');
INSERT INTO `upms_role_resource` VALUES ('1', '101', '2018-10-24 10:19:52', '1');
INSERT INTO `upms_role_resource` VALUES ('1', '102', '2018-10-24 10:19:52', '1');
INSERT INTO `upms_role_resource` VALUES ('1', '103', '2018-10-24 10:19:52', '1');
INSERT INTO `upms_role_resource` VALUES ('1', '104', '2018-10-24 10:19:52', '1');
INSERT INTO `upms_role_resource` VALUES ('1', '105', '2018-10-24 10:19:52', '1');
INSERT INTO `upms_role_resource` VALUES ('1', '106', '2018-10-24 10:19:52', '1');
INSERT INTO `upms_role_resource` VALUES ('2', '100', '2018-10-25 10:49:57', '1');
INSERT INTO `upms_role_resource` VALUES ('2', '101', '2018-10-25 10:49:57', '1');
INSERT INTO `upms_role_resource` VALUES ('2', '102', '2018-10-25 10:49:57', '1');
INSERT INTO `upms_role_resource` VALUES ('2', '103', '2018-10-25 10:49:57', '1');
INSERT INTO `upms_role_resource` VALUES ('2', '104', '2018-10-25 10:49:57', '1');
INSERT INTO `upms_role_resource` VALUES ('2', '105', '2018-10-25 10:49:57', '1');
INSERT INTO `upms_role_resource` VALUES ('2', '106', '2018-10-25 10:49:57', '1');
INSERT INTO `upms_role_resource` VALUES ('3', '107', '2018-10-30 15:50:54', '1');
INSERT INTO `upms_role_resource` VALUES ('3', '108', '2018-10-30 15:51:03', '1');
INSERT INTO `upms_role_resource` VALUES ('3', '109', '2018-10-30 15:51:11', '1');

-- ----------------------------
-- Table structure for upms_user
-- ----------------------------
DROP TABLE IF EXISTS `upms_user`;
CREATE TABLE `upms_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(32) NOT NULL COMMENT '用户名',
  `real_name` varchar(32) DEFAULT NULL COMMENT '真实姓名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `user_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用户类型：0-系统用户,1-普通用户',
  `nick_name` varchar(32) NOT NULL COMMENT '昵称',
  `mobile_phone` varchar(11) NOT NULL COMMENT '手机号码',
  `email` varchar(100) NOT NULL COMMENT '电子邮箱',
  `user_icon` varchar(255) DEFAULT '/images/default-user-avatar.png' COMMENT '用户头像',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户状态:0-冻结,1-正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `login_times` int(11) NOT NULL DEFAULT '0' COMMENT '登录次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` bigint(20) NOT NULL COMMENT '创建者,用户表的id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者,用户表的id',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_upms_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='用户权限管理系统-用户表';

-- ----------------------------
-- Records of upms_user
-- ----------------------------
INSERT INTO `upms_user` VALUES ('1', 'admin', '系统管理员', '$2a$10$uSDFFgwohIj6jWMPJ5BrkO4ASEeUW5zmvl6YnmlQvYf/65/NMo2QK', '0', '阿三', '13812345678', 'admin@qq.com', '/images/default-user-avatar.png', '1', null, '0', '2018-10-24 10:12:23', '1', null, null);

-- ----------------------------
-- Table structure for upms_user_role
-- ----------------------------
DROP TABLE IF EXISTS `upms_user_role`;
CREATE TABLE `upms_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` bigint(20) NOT NULL COMMENT '创建者,用户表id',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限管理系统-用户角色关系表';

-- ----------------------------
-- Records of upms_user_role
-- ----------------------------
INSERT INTO `upms_user_role` VALUES ('1', '1', '2018-10-24 10:13:41', '1');