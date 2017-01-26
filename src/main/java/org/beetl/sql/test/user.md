queryUsers
===


	select 
	@select(groupFilter.op){
		@case 2:{
			like CONCAT(CONCAT('%',#groupFilter.value#),'%')
		@}
	@}
	
	
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


