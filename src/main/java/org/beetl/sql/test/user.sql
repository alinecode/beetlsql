queryUser
===
* 按照年纪查找用户
select #page()# from User where 1 =1 
@if(isNotEmpty(age)){
and age = #age#
@}

queryNewUser
===
* 按照年纪查找用户
	select 
	#page()#
	from User a

insertTestUser
===
* 按照年纪查找用户
	
	insert User (id,name) values (#id#,#name#)




findById
===
select * from User where #globalUse("share.id")#
getCount
===
select count(*) from User 

setAge
===
update user set age = #age# where id=#id#

setUserStatus
===
update user set age = #age#,name=#name# where id=#id#

newUser
===
insert into user (name,age) values (#name#,#age#)

insertBatch
===  批量插入
insert into  user_info  (  id,user_name)
		values
		@for(item in list){
			(   #item.id# ,
				#item.userName# 
			)
			@debug(itemLP.last);
			#text(!itemLP.last?",")#
		@}

getMyNames
===
	select * from user where name like #name#
	
initUserDatabase
===

	CREATE TABLE `user_#text(suffix)#` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `name` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
	  `age` int(11) DEFAULT NULL COMMENT '年纪123',
	  `bir` datetime DEFAULT NULL COMMENT '生日',
	  `user_name` varchar(255) DEFAULT NULL,
		  PRIMARY KEY (`id`)
	) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
		


	CREATE TABLE `user_role_#text(suffix)#` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `name` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
	  `age` int(11) DEFAULT NULL COMMENT '年纪123',
	  `bir` datetime DEFAULT NULL COMMENT '生日',
	  `user_name` varchar(255) DEFAULT NULL,
		  PRIMARY KEY (`id`)
	) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
		

