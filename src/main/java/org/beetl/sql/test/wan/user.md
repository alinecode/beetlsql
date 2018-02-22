getIds3
===
	select 
	@pageTag(){
	 #use("cols")#
	@}
	from test  u where 1=1 and 1=1 and 1=1

cols	
===

	u.name
	
select  
===

	select 
	*
	from user 
	
	
selectUserAndDepartment
===
    select * from user where 1=1
    @ orm.lazyMany({"id":"userId"},"wan.user.selectRole","Role",{'alias':'myRoles'});

selectRole
===

    select r.* from user_role ur left join role r on ur.role_id=r.id
    where ur.user_id=#userId# 
    @ /* and state=#state# */