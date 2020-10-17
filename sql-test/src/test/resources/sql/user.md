queryById
===

```sql
select * from sys_user where id=#{id} 
```
    
    

queryByCondition
===

```sql
select #{page()} from sys_user where 1=1 
-- @if(isNotEmpty(name)){
and name=#{name}
-- @}
```

