select
===

```sql

select * from sys_user u where 1=1 
-- @ if(isNotEmpty(name)){
and name like #{name}
-- @ }
order by u.id desc
```


pageQuery
===

```sql
select #{page('*')} from sys_user where 1=1 
-- @if(isNotEmpty(deptId)){
 and department_id=#{deptId}
-- @}
```

pageQuery2
===

* group 语句翻页需要转成子查询

```sql
select 
#{page('*')}
from (
select count(1) total,department_id from sys_user where 1=1 
-- @if(isNotEmpty(deptId)){
   and department_id=#{deptId}
-- @}
group by department_id
) a
```


pageQuery3
===

```sql
select #{page()} from sys_user where 1=1 
-- @if(isNotEmpty(deptId)){
 and department_id=#{deptId}
-- @}
```

pageQuery3$count
===

```sql
select count(1) from sys_user /* 使用指定的count语句*/
```


departmentJsonMapping
===

* 映射DepartmentInfo

```json
{
"id":"id",
"name":"name",
"users":
  {
   "id":"u_id",
   "name":"u_name"
   }
}
```
  