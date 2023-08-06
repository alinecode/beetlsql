DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
      `order_id` int(11) NOT NULL AUTO_INCREMENT,

       `age` int(1) ,
       `version` int(255) ,
       `create_time` long,
       `status` char(1),
      PRIMARY KEY (`order_id`)
) ;

INSERT INTO `order_log` (order_id,version,status) VALUES (1, 100,'a');
INSERT INTO `order_log` (order_id,version,status)  VALUES (2, 101,'b');
INSERT INTO `order_log` (order_id,version,status)  VALUES (3, 102,'b');




COMMIT;
