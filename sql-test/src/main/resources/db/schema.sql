
-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
      `user_name` varchar(250) NOT NULL ,
      `user_id` int(10) NOT NULL,
      `id` varchar(255) AUTO_INCREMENT,
      PRIMARY KEY (`id`)
) ;

INSERT INTO `order` (user_name,user_id) VALUES ('abc', 1);
INSERT INTO `order` (user_name,user_id) VALUES ('edf', 2);

COMMIT;
