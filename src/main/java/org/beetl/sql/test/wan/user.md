getIds
===
	select id from user  where 1=1  
	
getIdNames
===
	select id,name from user

pageQuery
===

	select #page("name")# from user  where 1=1 
	
getCount
===

	select count(1) from user where name = #name#
	
	
getOneUser
===

	select * from user limit 1;

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
  
   update 
   @ var name = users[0].name,id=users[0].id;
   user set name=#name# where id=#id#
   
deleteByUserIds
===    

    DELETE FROM user_role WHERE userId IN (#join(userIds)#)
   
addOne    
===

   insert into User (name) values (#name#)