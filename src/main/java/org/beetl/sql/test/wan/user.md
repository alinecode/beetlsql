getIds3
===
	select #page("id")# from user  where 1=1  
	
select
===
    select *  from user  where 1=1 	
	
selectRole
===
* hello
	~~~sql
	select * from role 
	and 1 = 1 and  a=#b..c# and 1=1
	@ var c = 1/0;
	~~~

getUser5
===

    select #page("*")# from user where 1=1 and name = #name#
    
queryUser5
===

    select * from user where 1=1 and 1=1 and 1=1
    
updateUser
===
    
   update user set name=#name# where id=#id#
   
   
addOne    
===

   insert into User (name) values (#name#)