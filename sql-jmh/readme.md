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



# 最新测试结果 2023-08-02

增加了flex,easyQuery.   score越大越好

```
Benchmark                         Mode  Cnt     Score     Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    3   224.234 ± 551.444  ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    3   504.526 ± 208.228  ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    3   434.031 ± 110.087  ops/ms
JMHMain.beetlsqlFile             thrpt    3   428.368 ± 235.069  ops/ms
JMHMain.beetlsqlGetAll           thrpt    3     3.023 ±   3.741  ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    3   259.796 ±  37.421  ops/ms
JMHMain.beetlsqlOne2Many         thrpt    3   120.713 ±  68.157  ops/ms
JMHMain.beetlsqlPageQuery        thrpt    3   202.702 ±  92.875  ops/ms
JMHMain.beetlsqlSelectById       thrpt    3   423.833 ± 204.166  ops/ms
JMHMain.easyQueryComplexMapping  thrpt    3    60.562 ± 238.406  ops/ms
JMHMain.easyQueryExecuteJdbc     thrpt    3   656.139 ± 162.165  ops/ms
JMHMain.easyQueryGetAll          thrpt    3     9.028 ±   3.977  ops/ms
JMHMain.easyQueryLambdaQuery     thrpt    3   326.350 ± 280.253  ops/ms
JMHMain.easyQueryPageQuery       thrpt    3   132.464 ±  91.686  ops/ms
JMHMain.easyQuerySelectById      thrpt    3   292.797 ± 324.954  ops/ms
JMHMain.flexGetAll               thrpt    3     0.910 ±   0.503  ops/ms
JMHMain.flexPageQuery            thrpt    3    65.606 ± 402.289  ops/ms
JMHMain.flexSelectById           thrpt    3   218.314 ± 152.861  ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    3  1081.216 ± 225.041  ops/ms
JMHMain.jdbcGetAll               thrpt    3    13.381 ±   2.713  ops/ms
JMHMain.jdbcSelectById           thrpt    3  1077.471 ± 197.462  ops/ms
JMHMain.jpaExecuteJdbc           thrpt    3   104.056 ± 301.241  ops/ms
JMHMain.jpaExecuteTemplate       thrpt    3   130.304 ± 168.880  ops/ms
JMHMain.jpaGetAll                thrpt    3     0.552 ±   0.260  ops/ms
JMHMain.jpaInsert                thrpt    3    63.933 ± 359.824  ops/ms
JMHMain.jpaOne2Many              thrpt    3    99.006 ±  87.633  ops/ms
JMHMain.jpaPageQuery             thrpt    3   116.191 ± 191.231  ops/ms
JMHMain.jpaSelectById            thrpt    3   319.718 ± 129.206  ops/ms
JMHMain.mybatisComplexMapping    thrpt    3   106.319 ±  28.726  ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    3   222.515 ± 252.383  ops/ms
JMHMain.mybatisFile              thrpt    3   171.852 ± 188.859  ops/ms
JMHMain.mybatisGetAll            thrpt    3     1.183 ±   0.142  ops/ms
JMHMain.mybatisLambdaQuery       thrpt    3    10.197 ±  17.721  ops/ms
JMHMain.mybatisPageQuery         thrpt    3    66.245 ±  53.023  ops/ms
JMHMain.mybatisSelectById        thrpt    3   205.925 ± 481.288  ops/ms
JMHMain.woodExecuteJdbc          thrpt    3   378.089 ± 124.102  ops/ms
JMHMain.woodExecuteTemplate      thrpt    3   408.150 ± 172.513  ops/ms
JMHMain.woodFile                 thrpt    3   426.547 ± 182.839  ops/ms
JMHMain.woodGetAll               thrpt    3     1.100 ±   0.729  ops/ms
JMHMain.woodLambdaQuery          thrpt    3   402.107 ± 159.309  ops/ms
JMHMain.woodPageQuery            thrpt    3   247.074 ± 132.585  ops/ms
JMHMain.woodSelectById           thrpt    3   395.659 ±  39.505  ops/ms


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