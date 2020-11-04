
-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `address` varchar(255) DEFAULT NULL,
                              `user_id` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ;

INSERT INTO `person` VALUES (1, '地址1',3);
INSERT INTO `person` VALUES (2, '地址2',1);
INSERT INTO `person` VALUES (3, '地址3',2);


-- ----------------------------
-- Table structure for beetlSQLSysUser
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) DEFAULT NULL,
                            `person_id` int(11) DEFAULT NULL,
                            `create_time` datetime DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 'lijz', 2, NULL);
INSERT INTO `sys_user` VALUES (2, 'lucy', 3, NULL);
INSERT INTO `sys_user` VALUES (3, 'lucy', 1, NULL);

COMMIT;
