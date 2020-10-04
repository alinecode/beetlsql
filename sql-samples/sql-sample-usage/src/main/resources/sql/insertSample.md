sql文件采用markdown语法，如果使用idea企业版，可以配置好数据源，那么sql会有着色和语法提示，列名提示

insertUser
===

```sql
insert into sys_user (name,department_id) values (#{name},#{departmentId})
```

updateUser
===

```sql
update sys_user set name=#{name} where id = #{id}
```



