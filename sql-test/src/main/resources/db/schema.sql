DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
      `order_id` int(11) NOT NULL AUTO_INCREMENT,
       `version` int(255) ,
       `a_bc` varchar(255) ,
       `create_time` long,
       `status` char(1),
		name varchar(24)  ,
      PRIMARY KEY (`order_id`)
) ;

INSERT INTO `order_log` (order_id,version,status,a_bc) VALUES (1, 100,'a','a');
INSERT INTO `order_log` (order_id,version,status,a_bc)  VALUES (2, 101,'b','a');
INSERT INTO `order_log` (order_id,version,status,a_bc)  VALUES (3, 102,'b','a');




COMMIT;

