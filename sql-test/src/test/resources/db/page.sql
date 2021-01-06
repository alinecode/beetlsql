-- 常规测试,不要修改，否则单元测试不通过
drop table if exists  `sys_user` ;
CREATE TABLE `sys_user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(64) DEFAULT NULL,
    `age` int(4) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ;

insert into  sys_user values (1,'lijz',12);
insert into  sys_user values (2,'用户二',12);
insert into  sys_user values (3,'用户三',18);
insert into  sys_user values (5,'用户三',18);
insert into  sys_user values (6,'用户三',18);
insert into  sys_user values (7,'用户三',18);
insert into  sys_user values (8,'用户三',18);
insert into  sys_user values (9,'用户三',18);
insert into  sys_user values (10,'用户三',18);
insert into  sys_user values (11,'用户三',18);
insert into  sys_user values (12,'用户三',18);
insert into  sys_user values (13,'用户三',18);
insert into  sys_user values (14,'用户三',18);

