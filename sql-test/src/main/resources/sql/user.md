select
===
	select #{page("*")}  from order_log 
	#{globalUse("common.where")}
	
	