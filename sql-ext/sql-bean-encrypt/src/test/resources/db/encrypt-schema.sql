DROP TABLE IF EXISTS `my_order`;
CREATE TABLE `my_order` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `code` varchar(10) ,
      `content` varchar(100) ,
      PRIMARY KEY (`id`)
) ;

COMMIT;
