/*
Navicat MySQL Data Transfer

Source Server         : sue
Source Server Version : 50621
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2015-08-14 09:42:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `userName` varchar(64) DEFAULT NULL,
  `roleId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'name1', '11', 'username1',1);
INSERT INTO `user` VALUES ('2', 'name2', '12', 'username2',1);
INSERT INTO `user` VALUES ('3', 'name3', '13', 'username3',1);

-- ----------------------------
-- Table structure for user1
-- ----------------------------
DROP TABLE IF EXISTS `user1`;
CREATE TABLE `user1` (
  `id` int(11) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `age` int(64) DEFAULT NULL,
  `sex` char(2) DEFAULT NULL,
  `createdate` datetime DEFAULT NULL,
  `deletedate` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user1
-- ----------------------------
INSERT INTO `user1` VALUES ('1', 'name1', '11', 'M', '2015-08-09 14:07:28', '2015-08-09 14:07:30');
INSERT INTO `user1` VALUES ('2', 'name2', '12', 'F', '2015-08-09 14:07:31', '2015-08-09 14:07:35');

-- ----------------------------
-- Table structure for user2
-- ----------------------------
DROP TABLE IF EXISTS `user2`;
CREATE TABLE `user2` (
  `id` int(11) NOT NULL,
  `t_name` varchar(64) DEFAULT NULL,
  `t_age` int(64) DEFAULT NULL,
  `t_sex` char(2) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `delete_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user2
-- ----------------------------
INSERT INTO `user2` VALUES ('1', 'name1', '11', 'M', '2015-08-12 14:11:46', '2015-08-09 14:11:48');
INSERT INTO `user2` VALUES ('2', 'name2', '12', 'F', '2015-08-22 14:11:49', '2015-08-20 14:11:56');

-- ----------------------------
-- Table structure for user3
-- ----------------------------
DROP TABLE IF EXISTS `user3`;
CREATE TABLE `user3` (
  `t_varchar` varchar(255) NOT NULL,
  `t_char` char(255) DEFAULT NULL,
  `t_blob` blob,
  `t_tinyblob` tinyblob,
  `t_mediumblob` mediumblob,
  `t_longblob` longblob,
  `t_text` text,
  `t_tinytext` tinytext,
  `t_mediumtext` mediumtext,
  `t_longtext` longtext,
  `t_integer` int(255) DEFAULT NULL,
  `t_tinyint` tinyint(255) DEFAULT NULL,
  `t_smallint` smallint(255) DEFAULT NULL,
  `t_mediumint` mediumint(255) DEFAULT NULL,
  `t_bit` bit(1) DEFAULT NULL,
  `t_bigint` bigint(20) DEFAULT NULL,
  `t_float` float(4,2) DEFAULT NULL,
  `t_double` double(22,0) DEFAULT NULL,
  `t_decimal` decimal(11,0) DEFAULT NULL,
  `t_date` date DEFAULT NULL,
  `t_time` time DEFAULT NULL,   -- 数据库不支持？？time(6)
  `t_datetime` datetime DEFAULT NULL,
  `t_timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `t_year` year DEFAULT NULL,
  PRIMARY KEY (`t_varchar`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Records of user3
-- ----------------------------
INSERT INTO `user3` VALUES ('beetlSql', 'M', null, null, null, null, '这是text类型', '这是tinytext类型', '合适mediumtext类型', '这是longtext类型', '1000000001', '101', '10001', '1000001', '', '1000000000000000001', '12.35', '12', '123456789', '2015-08-20', '20:01:51.000000', '2015-08-18 20:01:56', '2015-08-09 20:02:09', '2015');


-- 测试主键
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1

INSERT INTO `role`  (id,name) VALUES (null,"mananger"); 


-- ----------------------------
--  测试lob类型
-- ----------------------------
DROP TABLE IF EXISTS `lobBean`;
CREATE TABLE `lobBean` (
  `picture` longblob,
  `article` text,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) DEFAULT NULL,
  `SYS_FLAG` varchar(5) DEFAULT NULL COMMENT '系统角色不能删除',
  `APP_ID` varchar(50) DEFAULT NULL,
  `COMPANY_ID` int(11) DEFAULT NULL,
  `MISC_DESC` longtext,
  `STATUS` varchar(5) DEFAULT NULL COMMENT '状态：DICT_GLOBAL_STATUS 有效V、无效I、草稿D、待审核W',
  `CREATE_TIME` datetime DEFAULT NULL,
  `CREATE_OPER_ID` int(11) DEFAULT NULL,
  `CREATE_OPER_NAME` varchar(50) DEFAULT NULL,
  `LAST_MOD_TIME` datetime DEFAULT NULL,
  `LAST_MOD_OPER_ID` int(11) DEFAULT NULL,
  `LAST_MOD_OPER_NAME` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='系统角色：可以分为系统的角色还是公司的角色，通过限制角色范围来区分前端的角色还是后端管理的角色';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '超级管理员', 'Y', '', '1', '超级管理员', 'V', '2014-10-22 15:30:32', '1', 'admin', '2014-10-22 15:30:32', '1', 'admin');
INSERT INTO `sys_role` VALUES ('2', 'console管理员', 'N', 'console', null, 'console管理员44', 'V', '2015-01-05 14:07:42', '1', 'admin', '2015-01-05 14:07:42', '1', 'admin');
INSERT INTO `sys_role` VALUES ('3', 'portal管理员', 'N', 'portal', '1', 'portal管理员', 'V', null, null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('4', '游客1', 'Y', '', '1', '游客1', 'V', '2014-09-15 16:59:48', '1', 'admin', '2014-10-23 22:19:59', '1', 'admin');
INSERT INTO `sys_role` VALUES ('7', '222', 'Y', 'console', '1', '222', 'V', null, null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('8', '444', 'Y', 'portal', '4', '4444', 'V', null, null, null, null, null, null);

