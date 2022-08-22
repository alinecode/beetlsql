
-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `t_order0`;
CREATE TABLE `t_order0` (
		  `order_id` int(11) NOT NULL AUTO_INCREMENT,
		  `user_id` int(11) NOT NULL,
		  PRIMARY KEY (`order_id`)
) ;


INSERT INTO `t_order0` VALUES (2, 2);
INSERT INTO `t_order0` VALUES (4, 2);


DROP TABLE IF EXISTS `t_order1`;
CREATE TABLE `t_order1` (
		  `order_id` int(11) NOT NULL AUTO_INCREMENT,
		  `user_id` int(11) NOT NULL,
		  PRIMARY KEY (`order_id`)
) ;


INSERT INTO `t_order1` VALUES (1, 2);
INSERT INTO `t_order1` VALUES (3, 2);

