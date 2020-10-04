BeetlSQL 的核心，包括sql加载，sql脚本执行，sqlManager管理，映射,数据源管理

```
ConnectionSource source = ConnectionSourceHelper.getSimple(driver, url, userName, password);
DBStyle mysql = new MySqlStyle();
// sql语句放在classpagth的/sql 目录下
SQLLoader loader = new ClasspathLoader("/sql");
// 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
UnderlinedNameConversion nc = new  UnderlinedNameConversion();
// 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
SQLManager sqlManager = new SQLManager(mysql,loader,source,nc,new Interceptor[]{new DebugInterceptor()});


```

# box


参考 https://docs.jboss.org/hibernate/search/6.0/reference/en-US/html_single/#search-dsl-query

负责SQL拼接,

```
Select select =  selectBox();
select.from(User.class).with("a","b").where("department_id","departmetnId").lmit(1,10);

String sql = select.toString();\\select a,b from User where department_id=#department#;



 Box box  =  jdbcBox();
select.from(User.class).cols("a","b").where("department_id").is(1);
List paras = select.getParas(); // [1];

```