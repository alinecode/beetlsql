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

CREATE SEQUENCE label_sequence
START WITH 1
INCREMENT BY 1;


DROP TABLE IF EXISTS `device_detail`;
CREATE TABLE `device_detail` (
      `id` int(11) NOT NULL ,
      `json` varchar(255)  DEFAULT NULL,
      PRIMARY KEY (`id`)
) ;






