insert
===

	insert user (name,age) value(#name#,#age#)

selectAll1
===
	select * from user where 1=1 and 
	`id` = #id#
	order by id desc

updateTest
===

	update activitys set `name` = #name#
	where `id` = #id#
	


