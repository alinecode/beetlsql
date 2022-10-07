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

# 最新结果 2022-10-07 

```
Benchmark                         Mode  Cnt     Score   Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    2   217.418          ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    2   501.621          ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    2   429.457          ops/ms
JMHMain.beetlsqlFile             thrpt    2   428.673          ops/ms
JMHMain.beetlsqlInsert           thrpt    2   252.065          ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    2   268.247          ops/ms
JMHMain.beetlsqlOne2Many         thrpt    2   119.450          ops/ms
JMHMain.beetlsqlPageQuery        thrpt    2   207.616          ops/ms
JMHMain.beetlsqlSelectById       thrpt    2   400.748          ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    2   997.115          ops/ms
JMHMain.jdbcInsert               thrpt    2   341.021          ops/ms
JMHMain.jdbcSelectById           thrpt    2  1056.166          ops/ms
JMHMain.jpaExecuteJdbc           thrpt    2   110.642          ops/ms
JMHMain.jpaExecuteTemplate       thrpt    2   134.947          ops/ms
JMHMain.jpaInsert                thrpt    2    64.890          ops/ms
JMHMain.jpaOne2Many              thrpt    2    98.547          ops/ms
JMHMain.jpaPageQuery             thrpt    2   114.931          ops/ms
JMHMain.jpaSelectById            thrpt    2   326.087          ops/ms
JMHMain.mybatisComplexMapping    thrpt    2   108.084          ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    2   204.084          ops/ms
JMHMain.mybatisFile              thrpt    2   165.927          ops/ms
JMHMain.mybatisInsert            thrpt    2   138.675          ops/ms
JMHMain.mybatisLambdaQuery       thrpt    2    10.413          ops/ms
JMHMain.mybatisPageQuery         thrpt    2    65.887          ops/ms
JMHMain.mybatisSelectById        thrpt    2   198.970          ops/ms
JMHMain.weedExecuteJdbc          thrpt    2   366.112          ops/ms
JMHMain.weedExecuteTemplate      thrpt    2   402.302          ops/ms
JMHMain.weedFile                 thrpt    2   430.715          ops/ms
JMHMain.weedInsert               thrpt    2   209.032          ops/ms
JMHMain.weedLambdaQuery          thrpt    2   343.186          ops/ms
JMHMain.weedPageQuery            thrpt    2   234.511          ops/ms
JMHMain.weedSelectById           thrpt    2   372.200          ops/ms

```


JDBC作为基准测试，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试