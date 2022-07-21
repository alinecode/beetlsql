DROP TABLE IF EXISTS test.`user2`;
CREATE TABLE `user2` (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
     `day` timestamp,
     `create_ts` bigint DEFAULT NULL,
     PRIMARY KEY (`id`)

) ENGINE=InnoDB AUTO_INCREMENT=104245 DEFAULT CHARSET=utf8 COMMENT='用户信息';
