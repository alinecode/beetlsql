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


queryByCondition2
===

```sql
select #{page()} from sys_user where 1=1 
${use("nameCondition")} order by name
```


queryByCondition2$count
===

```sql
select count(1) from sys_user where 1=1  /*提供翻页语句*/
${use("nameCondition")}
```

nameCondition
===

    -- @if(isNotEmpty(name)){
    and name=#{name}
    -- @}


utf8
===

    select '中文' from sys_user where id =1



implementByChild
===

    select * from sys_user  where id =1;

streamTest
===

    select * from sys_user


