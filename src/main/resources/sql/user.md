 getIds
===

	select *  
	@var name="i";
	from user  where name like #"%"+user.name+"%"#

cols	
===

	1=1 and #use("nameCondition")#
	
nameCondition	
===
    name=#bkl#
    
    
updateUser
===

    update user set name=#user.name# where id = #id#
	
pageQuery
===

	
	select #page()# 
	from (
	select count(1),department_id from user group by department_id ) a

	
select  
===

	select 
	*
	from user where 1=1 
	#use("condition")#
	
condition	
===	

	@if(isNotEmpty(name)){
	and name = '${name}'
	@}
	
	
	
selectUserAndDepartment
===
    select * from user where 1=1
    @ orm.lazyMany({"id":"userId"},"wan.user.selectRole","Role",{'alias':'myRoles'});

selectRole
===

    select r.* from user_role ur left join role r on ur.role_id=r.id
    where ur.user_id=#userId# 
    @ /* and state=#state# */

batchUpdate    
===

	update user set department_id = 1 where id  in ( #join(users,"id")#)


getCount
===

    select count(1) from user where name = #name#
    
    
queryAll
===

@ println("--: 注解");

```javascript
    select * from user;
```
