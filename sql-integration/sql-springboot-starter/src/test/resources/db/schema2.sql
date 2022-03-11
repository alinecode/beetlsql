
-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `name` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ;

INSERT INTO `department` VALUES (1, '部门1');
INSERT INTO `department` VALUES (2, '部门2');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `name` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Records of role
-- ----------------------------

INSERT INTO `role` VALUES (1, '经理');
INSERT INTO `role` VALUES (2, '行政');
INSERT INTO `role` VALUES (3, '运维');
INSERT INTO `role` VALUES (4, '程序员');


-- ----------------------------
-- Table structure for beetlSQLSysUser
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) DEFAULT NULL,
                            `department_id` int(11) DEFAULT NULL,
                            `create_time` datetime DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 'lijz', 1, NULL);

COMMIT;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `user_id` int(11) DEFAULT NULL,
                             `role_id` int(11) DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ;



