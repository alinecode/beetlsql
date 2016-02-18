insert
===

	insert user (name,age) value(#name#,#age#)

selectUser
===
    select * from user where 1=1
    @if(user.age==1){
    and age = #user.age#
    @}
    
selectUser2
===
    select * from user where 1=1
    @if(age==12){
    and age = #age#
    @}
    
selectCountUser2
===
    select  count(*) from user where 1=1
    @if(age==12){
    and age = #age#
    @}
    
selectAll
===
    select * from user 
    
    
updateName
===
    update user set name =#name# where age<#age#
    
selectByExample
===
    select * from user  
    #use("example")#
  
example
===  
   where 1=1 and name = #name#
   
   
selectCountUser3
===
    select  count(*) from user where 1=1
    and age = #age#
    
queryUser
===

* 按照年纪查找用户

	select * from User where 1 =1 
	@if(isNotEmpty(age)){
	and age = #age#
	@}
	@if(isNotEmpty(name)){
	and name = #name#
	@}
	




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



 