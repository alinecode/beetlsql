
-- ----------------------------
-- 为了简单起见，user系统和order系统都有这样的表结构
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

