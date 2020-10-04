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