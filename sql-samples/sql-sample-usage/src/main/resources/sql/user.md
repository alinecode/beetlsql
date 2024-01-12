selectByUserName
===

```sql
select * from sys_user u where 1=1 
-- @ if(isNotEmpty(name)){
and name like #{name} 
-- @ }
order by u.id desc
```



updateBySqlId
===

```sql
update sys_user set name=#{name} where id = #{id}

```



pageQueryByCondition
===

```sql
select #{page('*')} from sys_user u where 1=1 
-- @ if(isNotEmpty(name)){
and name like #{name} 
-- @ }
order by u.id desc
```  


pageQueryByCondition2
===
* pageTag标签函数同page方法，类似page("id,name,department_id")，pageTag虽然看着复杂，但对于复杂的sql有很好的支持
* 因此可以用pageIgnoreTag来排除在求总数时候的排序操作

```sql
select 
-- @ pageTag(){
id,name,department_id
-- @ }
from sys_user u where 1=1 
-- @ if(isNotEmpty(name)){
and name like #{name} 
-- @ }
-- @ pageIgnoreTag(){
order by u.id desc
-- @ }
```  

batchExecSql1
===
* 批量执行sql模板，每条sql模板可以有不同参数
update sys_user set name=#{name} where id=#{id}

batchExecSql2
===
* 批量执行sql模板，每条sql模板可以有不同参数
update sys_user set department_id=#{departmentId} where id=#{id}