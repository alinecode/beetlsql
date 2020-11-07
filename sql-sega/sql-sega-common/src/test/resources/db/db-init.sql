-- 常规测试,不要修改，否则单元测试不通过
drop table if exists  `sys_user` ;
CREATE TABLE `sys_user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(64) DEFAULT NULL,
    `age` int(4) DEFAULT NULL,
    `department_id` int(11) DEFAULT NULL,
    `create_date` datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
) ;

insert into  sys_user values (1,'lijz',12,1,null);
insert into  sys_user values (2,'用户二',12,2,null);
insert into  sys_user values (3,'用户三',18,2,null);


drop table if exists  `department` ;
CREATE TABLE `department` (
      `id` int(11) NOT NULL AUTO_INCREMENT ,
      `name` varchar(64) DEFAULT NULL,
      PRIMARY KEY (`id`)
) ;


insert into  department values (1,'部门1');
insert into  department values (2,'部门2');

drop table if exists  `stock` ;
CREATE TABLE `stock` (
      `id` varchar(64) NOT NULL  ,
      `count` int(11) DEFAULT NULL,
      PRIMARY KEY (`id`)
) ;

insert into  stock values ('1',29);
insert into  stock values ('2',5);


