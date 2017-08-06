selectUser
===

	select #page("*")# from user where 1=1
	@if(isNotEmpty(name)){
	 and name like #"%"+name+"%"#
	@}
	
	
selectSingleUser
===

	select u.*,d.name dept_name from user u left join department d on u.department_id=d.id  where u.id=#id#
	
	
selectOrmUser
===

* 加载产品，部门和角色

	select #page("*")# from user where 1=1
	@if(isNotEmpty(name)){
	 and name like #"%"+name+"%"#
	@}
	@//关系映射
	@ orm.single({"departmentId":"id"},"Department");
	@ orm.many({"id":"userId"},"ProductOrder");
	@ orm.many({"id":"userId"},"user.getRole","Role");
	
selectLazyOrmUser
===

* 加载产品，部门和角色

	select #page("*")# from user where 1=1
	@if(isNotEmpty(name)){
	 and name like #"%"+name+"%"#
	@}
	@//关系映射
	@ orm.lazySingle({"departmentId":"id"},"Department");
	@ orm.lazyMany({"id":"userId"},"ProductOrder");
	@ orm.lazyMany({"id":"userId"},"user.getRole","Role");
	

getRole
===

	select r.* from user_role ur, role r where ur.role_id=r.id and ur.user_id=#userId#
	
selectUsers
===

    select * from user where name in (#join(names)#)
