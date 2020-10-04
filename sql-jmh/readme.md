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
JMHMain.beetlsqlComplexMapping   thrpt    5   195.364 ±  77.645  ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    5   394.119 ± 194.906  ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    5   381.499 ±  26.086  ops/ms
JMHMain.beetlsqlFile             thrpt    5   447.060 ±  11.511  ops/ms
JMHMain.beetlsqlInsert           thrpt    5   251.468 ± 130.649  ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    5   264.216 ±  15.167  ops/ms
JMHMain.beetlsqlOne2Many         thrpt    5   109.499 ±  14.781  ops/ms
JMHMain.beetlsqlPageQuery        thrpt    5   209.418 ±  10.847  ops/ms
JMHMain.beetlsqlSelectById       thrpt    5   382.884 ±  22.160  ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    5  1096.030 ±  37.110  ops/ms
JMHMain.jdbcInsert               thrpt    5   331.819 ± 228.323  ops/ms
JMHMain.jdbcSelectById           thrpt    5  1069.215 ±  81.210  ops/ms
JMHMain.jpaExecuteJdbc           thrpt    5   109.956 ±  13.743  ops/ms
JMHMain.jpaExecuteTemplate       thrpt    5   134.473 ±  11.563  ops/ms
JMHMain.jpaInsert                thrpt    5    81.052 ±  14.006  ops/ms
JMHMain.jpaOne2Many              thrpt    5   101.677 ±  13.461  ops/ms
JMHMain.jpaPageQuery             thrpt    5   119.050 ±   7.998  ops/ms
JMHMain.jpaSelectById            thrpt    5   324.978 ±  14.455  ops/ms
JMHMain.mybatisComplexMapping    thrpt    5    96.171 ±  13.213  ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    5   192.065 ±  17.957  ops/ms
JMHMain.mybatisFile              thrpt    5   136.911 ±  12.952  ops/ms
JMHMain.mybatisInsert            thrpt    5   142.749 ±  34.862  ops/ms
JMHMain.mybatisLambdaQuery       thrpt    5    14.581 ±   1.696  ops/ms
JMHMain.mybatisPageQuery         thrpt    5    62.365 ±   9.497  ops/ms
JMHMain.mybatisSelectById        thrpt    5   194.090 ±  46.959  ops/ms
JMHMain.weedExecuteJdbc          thrpt    5   454.037 ±  37.330  ops/ms
JMHMain.weedExecuteTemplate      thrpt    5   253.859 ±  21.164  ops/ms
JMHMain.weedFile                 thrpt    5   534.792 ±  48.711  ops/ms
JMHMain.weedInsert               thrpt    5   233.368 ± 141.993  ops/ms
JMHMain.weedLambdaQuery          thrpt    5   454.978 ±  46.672  ops/ms
JMHMain.weedPageQuery            thrpt    5   237.196 ±  33.993  ops/ms
JMHMain.weedSelectById           thrpt    5   420.247 ±  28.087  ops/ms

```
JDBC作为基准测试，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试