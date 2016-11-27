queryUsers
===

	select #page("*")# from user where 1=1
	@if(!isEmpty()){
	name=#name#
	@}
	@ orm.single({"departmentId":"id"},"Department");
	@ orm.many({"id":"userId"},"ProductOrder");
	@ orm.many({"id":"userId"},"user.selectRole","Role");

selectRole
===

	select r.* from user_role ur, role r where ur.role_id=r.id and ur.user_id=#userId#

