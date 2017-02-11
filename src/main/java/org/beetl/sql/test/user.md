queryUsers
===

	select #page("*")# from user

insertTemplate	
===

	insert into user (
	@trim({suffixOverrides:","}){
		@if(!isEmpty(name)){
			name,
		@}
		@if(!isEmpty(departmentId)){
			department_id,
		@}
	@}
	)
	values(
	@trim({suffixOverrides:","}){
		@if(!isEmpty(name)){
			#name#,
		@}
		@if(!isEmpty(departmentId)){
			#departmentId#,
		@}
	@}
	)


querySingle
===


	select  count(1) from user where 1=1
	
condition
===

	#text(groupFilter.field)#
	@select(groupFilter.op){
		@case 2:{
			like CONCAT(CONCAT('%',#groupFilter.value#),'%')
			@}
	@}
	
condition2
===

	#text(groupFilter.field)# like CONCAT(CONCAT('%',#groupFilter.value#),'%')


selectRole
===


select r.* from user_role ur, role r where ur.role_id=r.id and ur.user_id=#userId#


