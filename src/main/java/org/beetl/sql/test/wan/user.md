getIds3
===
	select 
	@pageTag(){
	 #use("cols")#
	@}
	from user  u where 1=1 and 1=1 
	
	
	@if(departmentId1!=null){
		and departmentId = #departmentId#
	@}
	

cols	
===

	u.name
	
select  
===

	select 
	*
	from user where 1=1 
	@if(isNotEmpty(name)){
	and name = #name#
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