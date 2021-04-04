allUserInDepartment
===
    * 这一段将作为Query类的子查询，参考 @SubQuery
    select * from sys_user where department_id=#{deptId}