-- 常规测试,不要修改，否则单元测试不通过
drop table if exists  `sys_user` ;
CREATE TABLE `sys_user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(64) DEFAULT NULL,
    `age` int(4) DEFAULT NULL,
    `department_id` int(11) DEFAULT NULL,
    `create_date` datetime NULL DEFAULT NULL,
    `version` int(11) DEFAULT 0 NOT NULL,
    PRIMARY KEY (`id`)
) ;

insert into  sys_user values (1,'lijz',12,1,null,0);
insert into  sys_user values (2,'用户二',12,2,null,0);
insert into  sys_user values (3,'用户三',18,2,null,0);


drop table if exists  `department` ;
CREATE TABLE `department` (
      `id` int(11) NOT NULL AUTO_INCREMENT ,
      `name` varchar(64) DEFAULT NULL,
      PRIMARY KEY (`id`)
) ;


insert into  department values (1,'部门1');
insert into  department values (2,'部门2');

-- orm 测试的
drop table if exists  `sys_customer` ;
CREATE TABLE sys_customer (
   ID int(20) NOT NULL ,
   CODE  varchar(16) DEFAULT NULL,
   NAME  varchar(16) DEFAULT NULL,
   PRIMARY KEY ( ID )
) ;

insert into  sys_customer values (1,'a','客户一');
insert into  sys_customer values (2,'b','客户二');
insert into  sys_customer values (3,'c','客户三');

drop table if exists  `sys_order` ;
CREATE TABLE sys_order (
     ID int(20) NOT NULL ,
     NAME  varchar(16) DEFAULT NULL,
     customer_id int(20) ,
   PRIMARY KEY ( ID )
) ;

insert into  sys_order values (1,'a',1);
insert into  sys_order values (2,'b',1);
insert into  sys_order values (3,'c',2);
insert into  sys_order values (4,'d',2);

