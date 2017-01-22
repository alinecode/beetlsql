queryUsers
===


	select #page("*")# from user where 1=11
	@if(!isEmpty()){
	name=#name#
	@}

	

selectRole
===


select r.* from user_role ur, role r where ur.role_id=r.id and ur.user_id=#userId#


