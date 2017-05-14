queryName
===
* 根据id查找用户姓名

	select name from user where 1=1 
	@if(!isEmpty(id)a){
	and id = #id#
	@} 
	
queryNewUser
===
* 用一个sql做翻页查询，page将输出 count(1) 或者 u.*

	select #page("u.*")# from user u

updateAge
===

	update user set age = #age# where id = #id#

findUser
===

	select * from user where name=#name#  and age=#age#
	