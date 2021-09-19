DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
      `order_id` int(11) NOT NULL ,
      `status` varchar(10) NOT NULL,
      `id` varchar(255) AUTO_INCREMENT,
      `create_date` TIMESTAMP WITH TIME ZONE,
      PRIMARY KEY (`order_id`,`status`)
) ;

INSERT INTO `order_log` (order_id,status) VALUES (1, 'u');
INSERT INTO `order_log` (order_id,status) VALUES (1, 'd');
INSERT INTO `order_log` (order_id,status) VALUES (2, 'u');




COMMIT;
