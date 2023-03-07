select
===

```sql
	-- @ var a = 1; debug(a);
    select *  from order_log where 1=1 
    -- @ if(!isEmpty(ids)){
        and order_id in ( #{join(ids)} )
    -- @ }
```

