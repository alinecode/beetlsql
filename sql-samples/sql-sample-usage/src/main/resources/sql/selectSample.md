sql文件采用markdown语法，如果使用idea企业版，可以配置好数据源，那么sql会有着色和语法提示，列名提示

selectByCondition
===

```sql
select * from sys_user where 1=1
-- @ if(isNotEmpty(name)){
and name=#{name}
-- @ }
```


count
===

* 查询总数

```sql
select count(1) from sys_user
```

selectUserById
===

```sql
select * from sys_user where id = #{id}
```


pageQuery
===

* 使用pageTag,在运行时候输出内容是原样输出，或者输出成count(1),参考代码 PageQueryTag.java
* 当翻页的时候，pageIgnoreTag是可选的

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


groupByTest
===

* 分组语句只能转化为子查询
* use函数可以引用sql文件片段，globalUse(xxx.yyyyyy)可以引用其他sql文件的sql片段

```sql

select 
#{page("*")}
from ( #{use('groupBySql')}) a


```  

groupBySql
===

```sql
select name,count(1) count from sys_user group by name
```


includeTest
===

```sql
select #{use("part1")} from sys_user where #{use("part2")}
```

globalIncludeTest
===

* 引用另外一个sql文件的文件片段

```sql
select #{use("part1")} from sys_user where #{globalUse("common.id")}
```

likeAndIn
===

* 符号${}代表文本输出。

```sql
select * from  sys_user where id in ( #{join(ids)} ) and name like #{name} order by ${order}
```


includeDynamicSql
===

* 如果sql是动态的，通过java定义的,比如变量myDynamicSql

```sql
select * from  sys_user where #{db.dynamicSql(myDynamicSql,{"notes":true\})}
```

part1
===

```
*
```

part2
===

```
 id = #{id}
```