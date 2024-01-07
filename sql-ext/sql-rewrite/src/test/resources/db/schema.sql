DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
       `name` varchar(12) ,
       `tenant_id` int ,
      PRIMARY KEY (`id`)
) ;

INSERT INTO `order_log` (id,tenant_id,name) VALUES (1, 1,'a');
INSERT INTO `order_log` (id,tenant_id,name)  VALUES (2, 2,'b');
INSERT INTO `order_log` (id,tenant_id,name)  VALUES (3, 2,'b');
INSERT INTO `order_log` (id,tenant_id,name)  VALUES (4, 1,'b');



COMMIT;
