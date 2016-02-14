queryUser
===

select * from User where age = #age#


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



