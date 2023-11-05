DROP TABLE IF EXISTS `device_data`;
CREATE TABLE `device_data` (
         `id` varchar(255) NOT NULL,
         `data` varchar(255)  DEFAULT NULL,
         PRIMARY KEY (`id`)
) ;


DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
       `id` int(11) NOT NULL AUTO_INCREMENT,
       `sn` varchar(255)  DEFAULT NULL,
       PRIMARY KEY (`id`)
) ;


-- 一个综合例子
DROP TABLE IF EXISTS `big_data`;
CREATE TABLE `big_data` (
       `order_id` int(11) NOT NULL ,
       `status` int(11) NOT NULL ,
       `data_id` int(11) NOT NULL AUTO_INCREMENT,
       `label` varchar(255)  DEFAULT NULL,
       PRIMARY KEY (`order_id`,`status`)
) ;

CREATE SEQUENCE label_sequence
START WITH 1
INCREMENT BY 1;


DROP TABLE IF EXISTS `device_detail`;
CREATE TABLE `device_detail` (
      `id` int(11) NOT NULL ,
      `json` varchar(255)  DEFAULT NULL,
      PRIMARY KEY (`id`)
) ;


DROP TABLE IF EXISTS `auto_bean`;
CREATE TABLE `auto_bean` (
       `order_id` int(11) NOT NULL AUTO_INCREMENT,
       `age` int(10) NOT NULL AUTO_INCREMENT,
		name varchar(24) NOT NULL,
      PRIMARY KEY (`order_id`)
) ;









