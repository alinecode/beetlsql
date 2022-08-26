select
===

```sql
    select *  from order_log where 1=1 
    -- @ if(!isEmpty(ids)){
        and order_id in ( #{join(ids)} )
    -- @ }
```

