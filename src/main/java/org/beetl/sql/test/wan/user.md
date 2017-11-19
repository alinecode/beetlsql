getIds
===
	select 
	@pageTag(){
	 #use("cols")#
	@}
	from user  u where 1=1 and 1=1 and 1=1

cols	
===

	u.*
	
dateTest  
===

	select 
	#page("id")#
	from TB_user 