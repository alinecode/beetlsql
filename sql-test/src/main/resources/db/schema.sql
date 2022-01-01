DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
      `order_id` int(11) NOT NULL ,

       `age` int(1) AUTO_INCREMENT,
       `version` int(255) ,
      PRIMARY KEY (`order_id`)
) ;

INSERT INTO `order_log` (order_id,version) VALUES (1, 100);
INSERT INTO `order_log` (order_id,version) VALUES (2, 101);
INSERT INTO `order_log` (order_id,version) VALUES (3, 102);




COMMIT;
