dynamicFetchOrder1
===

	select * from sys_order where id = #{id}
	-- @ fetchEnableOn("c"); //fetchEnableOn用于标记，参考CustomerOrder3


dynamicFetchOrder2
===
	* 验证不增加fetchEnableOn函数，不会发生Fetch
	select * from sys_order where id = #{id}
