insert
===

	insert user (name,age) value(#name#,#age#)

selectAll
===
	select * from user where 1=1
	@if(!isEmpty(id)){
		id = #id#
	@}

