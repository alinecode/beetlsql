# 测试DAO
本例使用H2的内存模式测试，并且，尽量让Entity最为简单，以最大程度验证Dao自身的性能
* BeetlSQL (国产)
* MyBatis(plus)
* JPA(Spring Data)
* JDBC (基准)
* Weed3 (国产)

# 测试标准

参考 BaseService ，测试了Dao的各个功能
```java

public interface BaseService {
    /**
     * 简单增加一个实体
     */
    public void addEntity();

    /**
     * 根据主键查询实体
     * @return
     */
    public Object getEntity();

    /**
     * 动态构造sql语句，并支持重构
     * */
    public void lambdaQuery();

    /**
     * 执行jdbc sql
     */
    public void executeJdbcSql();

    /**
     * 执行sql模板或者HQL
     */
    public void executeTemplateSql();

    /**
     * SQL放到文件管理
     */
    public void sqlFile();

    /**
     * 一个一对多的例子
     */
    public void one2Many();

    /**
     * 测试翻页查询
     */
    public void pageQuery();

    /**
     * 通过配置文件来映射复杂的查询结果，目前只有mybatis和beetlsql支持
     */
    public void complexMapping();

```

# 测试方法

 进入JMHMain，运行即可。如果你有新的测试方法，可以暂时屏蔽其他测试方法
 
 # 最新结果(2020-08-17)
 
有些dao并不支持一些特性，所以并未出现在下面列表，比如JDBC不支持自动翻页查询，MyBatis不支持one2Many等
```
Benchmark                         Mode  Cnt     Score     Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    5   215.756 ±  29.222  ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    5   445.213 ±  21.442  ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    5   395.083 ±  19.829  ops/ms
JMHMain.beetlsqlFile             thrpt    5   440.363 ±  18.299  ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    5   267.491 ±  32.322  ops/ms
JMHMain.beetlsqlOne2Many         thrpt    5   113.734 ±  13.268  ops/ms
JMHMain.beetlsqlPageQuery        thrpt    5   204.162 ±  25.208  ops/ms
JMHMain.beetlsqlSelectById       thrpt    5   398.027 ±  15.852  ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    5  1122.664 ±  48.194  ops/ms
JMHMain.jdbcInsert               thrpt    5   321.063 ± 203.877  ops/ms
JMHMain.jdbcSelectById           thrpt    5  1068.928 ±  39.522  ops/ms
JMHMain.jpaExecuteJdbc           thrpt    5   112.631 ±  12.682  ops/ms
JMHMain.jpaExecuteTemplate       thrpt    5   131.009 ±  12.017  ops/ms
JMHMain.jpaInsert                thrpt    5    83.756 ±   4.869  ops/ms
JMHMain.jpaOne2Many              thrpt    5   102.321 ±   2.554  ops/ms
JMHMain.jpaPageQuery             thrpt    5   120.206 ±  12.380  ops/ms
JMHMain.jpaSelectById            thrpt    5   318.164 ±  58.139  ops/ms
JMHMain.mybatisComplexMapping    thrpt    5   100.092 ±  12.463  ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    5   191.652 ±  37.449  ops/ms
JMHMain.mybatisFile              thrpt    5   137.557 ±  69.462  ops/ms
JMHMain.mybatisInsert            thrpt    5   141.741 ±  25.191  ops/ms
JMHMain.mybatisLambdaQuery       thrpt    5    14.595 ±   1.055  ops/ms
JMHMain.mybatisPageQuery         thrpt    5    58.819 ±  24.402  ops/ms
JMHMain.mybatisSelectById        thrpt    5   149.276 ± 127.379  ops/ms
JMHMain.weedExecuteJdbc          thrpt    5   400.952 ±  59.950  ops/ms
JMHMain.weedExecuteTemplate      thrpt    5   436.414 ±   7.724  ops/ms
JMHMain.weedFile                 thrpt    5   464.335 ±  43.434  ops/ms
JMHMain.weedInsert               thrpt    5   228.271 ±  91.209  ops/ms
JMHMain.weedLambdaQuery          thrpt    5   391.371 ±  15.198  ops/ms
JMHMain.weedPageQuery            thrpt    5   242.859 ±  16.747  ops/ms
JMHMain.weedSelectById           thrpt    5   409.601 ±  37.261  ops/ms
```
JDBC作为基准测试，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试