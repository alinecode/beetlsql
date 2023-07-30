# 测试DAO
本例使用H2的内存模式测试，并且，尽量让Entity最为简单，以最大程度验证Dao自身的性能
* BeetlSQL (国产)
* MyBatis(plus)
* JPA(Spring Data)
* JDBC (基准)
* Wood (国产)
* Flex (国产)

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

有些框架不完全支持如上特性，因此性能测试无改项结果，比如JDBC并没有One2Many这种

# 测试方法

 进入JMHMain，运行即可。如果你有新的测试方法，可以暂时屏蔽其他测试方法



# 最新测试结果 2023-07-29

增加了flex

```
Benchmark                         Mode  Cnt     Score     Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    3   218.350 ± 419.573  ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    3   501.651 ± 451.931  ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    3   445.979 ± 303.805  ops/ms
JMHMain.beetlsqlFile             thrpt    3   418.770 ± 193.617  ops/ms
JMHMain.beetlsqlInsert           thrpt    3   244.853 ±  69.934  ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    3   269.737 ±  82.457  ops/ms
JMHMain.beetlsqlOne2Many         thrpt    3   118.366 ±  84.986  ops/ms
JMHMain.beetlsqlPageQuery        thrpt    3   208.211 ± 122.478  ops/ms
JMHMain.beetlsqlSelectById       thrpt    3   408.891 ± 135.243  ops/ms
JMHMain.flexInsert               thrpt    3   165.386 ±  94.696  ops/ms
JMHMain.flexPageQuery            thrpt    3    78.241 ± 431.396  ops/ms
JMHMain.flexSelectById           thrpt    3   218.724 ±  85.335  ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    3  1069.574 ± 642.748  ops/ms
JMHMain.jdbcInsert               thrpt    3   336.188 ±  71.714  ops/ms
JMHMain.jdbcSelectById           thrpt    3  1057.947 ± 487.527  ops/ms
JMHMain.jpaExecuteJdbc           thrpt    3   103.096 ± 194.019  ops/ms
JMHMain.jpaExecuteTemplate       thrpt    3   134.640 ±   4.033  ops/ms
JMHMain.jpaInsert                thrpt    3    67.380 ± 360.504  ops/ms
JMHMain.jpaOne2Many              thrpt    3    98.640 ±  28.748  ops/ms
JMHMain.jpaPageQuery             thrpt    3   113.869 ± 167.875  ops/ms
JMHMain.jpaSelectById            thrpt    3   323.054 ± 248.879  ops/ms
JMHMain.mybatisComplexMapping    thrpt    3   100.981 ± 306.305  ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    3   215.829 ± 253.516  ops/ms
JMHMain.mybatisFile              thrpt    3   173.024 ± 111.938  ops/ms
JMHMain.mybatisInsert            thrpt    3   146.358 ±  79.569  ops/ms
JMHMain.mybatisLambdaQuery       thrpt    3     9.973 ±  20.414  ops/ms
JMHMain.mybatisPageQuery         thrpt    3    68.850 ±  18.252  ops/ms
JMHMain.mybatisSelectById        thrpt    3   233.024 ± 145.846  ops/ms
JMHMain.woodExecuteJdbc          thrpt    3   373.744 ± 256.796  ops/ms
JMHMain.woodExecuteTemplate      thrpt    3   387.877 ± 116.131  ops/ms
JMHMain.woodFile                 thrpt    3   402.589 ± 509.941  ops/ms
JMHMain.woodInsert               thrpt    3   214.500 ±  93.478  ops/ms
JMHMain.woodLambdaQuery          thrpt    3   358.374 ± 470.141  ops/ms
JMHMain.woodPageQuery            thrpt    3   231.666 ± 122.246  ops/ms
JMHMain.woodSelectById           thrpt    3   361.699 ± 182.763  ops/ms

```

# 2022-10-07 

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
JMHMain.woodExecuteJdbc          thrpt    2   366.112          ops/ms
JMHMain.woodExecuteTemplate      thrpt    2   402.302          ops/ms
JMHMain.woodFile                 thrpt    2   430.715          ops/ms
JMHMain.woodInsert               thrpt    2   209.032          ops/ms
JMHMain.woodLambdaQuery          thrpt    2   343.186          ops/ms
JMHMain.woodPageQuery            thrpt    2   234.511          ops/ms
JMHMain.woodSelectById           thrpt    2   372.200          ops/ms

```


JDBC作为基准测试，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试