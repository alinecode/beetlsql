
-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
      `id` varchar(36) NOT NULL,
      `name` varchar(255) NOT NULL,
      `balance` int(11) NOT NULL,
      PRIMARY KEY (`id`)
) ;

INSERT INTO `t_user` VALUES ('xiandafu', '闲大赋',4);


DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
             `id` varchar(36) NOT NULL,
             `user_id` varchar(36)  NOT NULL,
             `product_id` varchar(36) NOT NULL,
             `fee` int(11) ,
             PRIMARY KEY (`id`)
) ;

