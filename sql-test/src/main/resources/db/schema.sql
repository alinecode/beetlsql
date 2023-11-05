DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
      `order_id` int(11) NOT NULL AUTO_INCREMENT,
       `age` int(10) NOT NULL AUTO_INCREMENT,
		name varchar(24) NOT NULL,
      PRIMARY KEY (`order_id`)
) ;



COMMIT;
