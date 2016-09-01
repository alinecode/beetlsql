selectUser
===

	select #page("*")# from user where 1=1
	@if(isNotEmpty(name)){
	 and name like #"%"+name+"%"#
	@}
	
	
selectSingleUser
===

	select u.*,d.name dept_name from user u left join department d on u.department_id=d.id  where u.id=#id#
	