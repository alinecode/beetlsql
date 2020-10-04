package org.beetl.sql.test.vo;

import lombok.Data;
import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.mapping.join.JsonConfigMapper;

import java.util.List;

@Table(name="department")
@Data

@ResultProvider(JsonConfigMapper.class)
//@JsonMapper(
//        "{'id':'id','name':'name','users':{'id':'u_id','name':'u_name'}}")
@org.beetl.sql.annotation.entity.JsonMapper(resource ="dept.departmentJsonMapping")
public class MyDepartment {
    Integer id;
    String name;
    List<MyUser>  users;
}
