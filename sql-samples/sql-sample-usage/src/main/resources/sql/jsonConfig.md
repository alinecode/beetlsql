selectUser
===

* 调用jsonMapping 提供一个配置

```sql
select u.*,d.name as dept_name from sys_user u 
                left  join department d on u.department_id= d.id where u.id=#{id}

```

${jsonMapping("userConfig")}



userConfig
===
```json
{
	"id": "id",
	"name": "name",
	"deptName": "dept_name"
}
```
selectUserDetail
===
* 调用jsonMapping 提供一个配置
```sql

select u.*,d.name as dept_name,d.id as dept_id ,r.id r_id,r.`name` r_name from sys_user u 
      left  join department d on u.department_id= d.id 
      left join user_role ur on ur.user_id = u.id 
       left join role r on r.id=ur.role_id											
where u.id in ( #{join(ids)} )

```
${jsonMapping("userDetailConfig")}

userDetailConfig
===

```json
{
	"id": "id",
	"name": "name",
	"dept": {
		"id": "dept_id",
		"name": "dept_name"
	},
	"roles": {
		"id": "r_id",
		"name": "r_name"
	}
}
```


