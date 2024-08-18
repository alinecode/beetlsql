select
===

```sql
select 
from order_log  where order_id = #{@orderLog.getOrderId()}

```


insertHolder
===

	insert into order_log  (name) values (#{name}) ,  (#{name});

	
	
	