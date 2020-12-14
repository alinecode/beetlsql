
-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `rollback_task`;
CREATE TABLE `rollback_task` (
      `id` varchar(36) NOT NULL,
      `gid` varchar(255) NOT NULL,
      `app_name` varchar(255) NOT NULL,
      `time` bigint NOT NULL,
      `status` varchar(32) ,
      `create_time` bigint NOT NULL,
      `update_time` bigint ,
      `task_info` text ,
      `rollback_status` varchar(32) ,
      PRIMARY KEY (`id`)
) ;

-- INSERT INTO `rollback_task` VALUES ('uuid', 'order:axdfdfdf','orderSystem',123456,'SUCCESS',123456,123456,'{}',NULL);


DROP TABLE IF EXISTS `rollback`;
CREATE TABLE `rollback` (
             `gid` varchar(255) NOT NULL,
             `create_time` bigint NOT NULL,
             `update_time` bigint ,
             `rollback_status` varchar(32) ,
             `first_app_name` varchar(32) ,
             `total` int(11) ,
             `success` int(11) ,
             PRIMARY KEY (`gid`)
) ;

