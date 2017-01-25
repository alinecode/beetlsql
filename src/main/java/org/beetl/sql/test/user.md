queryUsers
===


	select #page("*")# from user where 1=1
	#use("condition")#
	@if(!isEmpty()){
	name=#name#
	@}
	
	

	
condition
===

1=11


selectRole
===


select r.* from user_role ur, role r where ur.role_id=r.id and ur.user_id=#userId#


