
-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `rollback_task`;
CREATE TABLE `rollback_task` (
      `id` varchar(26) NOT NULL,
      `gid` varchar(255) NOT NULL,
      `app_name` varchar(255) NOT NULL,
      `time` int(11) NOT NULL,
      `status` varchar(32) NOT NULL,
      `create_time` int(11) NOT NULL,
      `update_time` int(11) ,
      `task_info` text NOT NULL,
      `rollback_status` varchar(32) ,
      PRIMARY KEY (`id`)
) ;

INSERT INTO `rollback_task` VALUES ('uuid', 'order:axdfdfdf',123456,1,123456,123456,'{}');

