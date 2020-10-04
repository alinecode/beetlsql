<%
/*生成markdown 文档*/
%>
## ${tableName}

**说明**

   ${isEmpty(comment)?"无注释":comment}

**表信息**
<%
var ids = table.idNames;
%>
* 主键 ${ids}
* 表注释

| 名称 | 数据类型 | 长度  |  说明 |
| :--: | :--- | :------: |  :----: |
<% for(col in colsMap){ 

var name = col.key;

if(@ids.contains(name)){
    name="*"+name;
}
var detail = col.value;
var dbType =@org.beetl.sql.clazz.kit.JavaType.jdbcTypeId2Names.get(detail.sqlType);
%>
|${name} | ${dbType}| ${detail.size} |    ${detail.remark} |
<%}%>



