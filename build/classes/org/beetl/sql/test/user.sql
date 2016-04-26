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



findById
===
select * from User where id = #id#

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

		


