select
===

```sql
select /* comment 5 */
-- @pageTag(){
order_id
-- @}
from order_log  where order_id in ( #{join(ids)} )

```


insertHolder
===

	insert into order_log  (name) values (#{name}) ,  (#{name});

	
	
	