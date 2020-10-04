page
===

```sql
select #{page()} from sys_user where name=#{name} and age =#{age}
```
    
    

queryByCondition
===

```sql
select #{page()} from sys_user where 1=1
-- @if(isNotEmpty(name)){
and name=#{name}
-- @}
```

   
    