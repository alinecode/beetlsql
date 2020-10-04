BeetlSQL 集成与其他框架的核心代码,包括
* Spring集成
* Spring Boot集成
* Act集成
* Solon集成
* Jfinal集成
* Servlet集成


单元测试集成使用了mysql数据库

```sql
/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1-mysql-docker
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost:13306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 25/06/2020 11:34:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for beetlSQLSysUser
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of beetlSQLSysUser
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'lijz', 1, NULL);
INSERT INTO `user` VALUES (2, 'lucy', 1, NULL);
INSERT INTO `user` VALUES (3, 'bear', 2, NULL);
INSERT INTO `user` VALUES (4, 'mike', 1, NULL);
INSERT INTO `user` VALUES (5, 'lisan', 1, NULL);
INSERT INTO `user` VALUES (6, 'xb', 1, NULL);
INSERT INTO `user` VALUES (7, 'duanwu', 2, NULL);
INSERT INTO `user` VALUES (8, 'fenh', 1, NULL);
INSERT INTO `user` VALUES (9, 'lj', 2, NULL);
INSERT INTO `user` VALUES (10, 'gshen', 1, NULL);
INSERT INTO `user` VALUES (11, 'lihui', 1, NULL);
COMMIT;


SET FOREIGN_KEY_CHECKS = 1;

```