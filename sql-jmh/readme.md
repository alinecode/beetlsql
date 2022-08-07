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

# 最新结果 2022-08-07 

```
Benchmark                         Mode  Cnt     Score   Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    2   261.097          ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    2   545.487          ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    2   468.580          ops/ms
JMHMain.beetlsqlFile             thrpt    2   475.087          ops/ms
JMHMain.beetlsqlInsert           thrpt    2   260.108          ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    2   296.364          ops/ms
JMHMain.beetlsqlOne2Many         thrpt    2   132.043          ops/ms
JMHMain.beetlsqlPageQuery        thrpt    2   230.195          ops/ms
JMHMain.beetlsqlSelectById       thrpt    2   458.701          ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    2  1182.139          ops/ms
JMHMain.jdbcInsert               thrpt    2   335.903          ops/ms
JMHMain.jdbcSelectById           thrpt    2  1164.131          ops/ms
JMHMain.jpaExecuteJdbc           thrpt    2   124.374          ops/ms
JMHMain.jpaExecuteTemplate       thrpt    2   150.468          ops/ms
JMHMain.jpaInsert                thrpt    2    88.970          ops/ms
JMHMain.jpaOne2Many              thrpt    2   109.989          ops/ms
JMHMain.jpaPageQuery             thrpt    2   133.352          ops/ms
JMHMain.jpaSelectById            thrpt    2   359.050          ops/ms
JMHMain.mybatisComplexMapping    thrpt    2   117.422          ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    2   251.915          ops/ms
JMHMain.mybatisFile              thrpt    2   192.212          ops/ms
JMHMain.mybatisInsert            thrpt    2   167.225          ops/ms
JMHMain.mybatisLambdaQuery       thrpt    2    12.453          ops/ms
JMHMain.mybatisPageQuery         thrpt    2    76.006          ops/ms
JMHMain.mybatisSelectById        thrpt    2   265.810          ops/ms
JMHMain.weedExecuteJdbc          thrpt    2   424.919          ops/ms
JMHMain.weedExecuteTemplate      thrpt    2   464.600          ops/ms
JMHMain.weedFile                 thrpt    2   498.603          ops/ms
JMHMain.weedInsert               thrpt    2   248.010          ops/ms
JMHMain.weedLambdaQuery          thrpt    2   383.933          ops/ms
JMHMain.weedPageQuery            thrpt    2   249.315          ops/ms
JMHMain.weedSelectById           thrpt    2   401.897          ops/ms

```

# (2021-11-21)
 
有些dao并不支持一些特性，所以并未出现在下面列表，比如JDBC不支持自动翻页查询，MyBatis不支持one2Many等
```
Benchmark                         Mode  Cnt     Score   Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    2   226.834          ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    2   510.638          ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    2   461.694          ops/ms
JMHMain.beetlsqlFile             thrpt    2   467.866          ops/ms
JMHMain.beetlsqlInsert           thrpt    2   273.023          ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    2   285.992          ops/ms
JMHMain.beetlsqlOne2Many         thrpt    2   125.105          ops/ms
JMHMain.beetlsqlPageQuery        thrpt    2   215.244          ops/ms
JMHMain.beetlsqlSelectById       thrpt    2   447.269          ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    2  1120.869          ops/ms
JMHMain.jdbcInsert               thrpt    2   355.742          ops/ms
JMHMain.jdbcSelectById           thrpt    2  1153.968          ops/ms
JMHMain.jpaExecuteJdbc           thrpt    2   109.625          ops/ms
JMHMain.jpaExecuteTemplate       thrpt    2   138.528          ops/ms
JMHMain.jpaInsert                thrpt    2    71.405          ops/ms
JMHMain.jpaOne2Many              thrpt    2   103.901          ops/ms
JMHMain.jpaPageQuery             thrpt    2   119.841          ops/ms
JMHMain.jpaSelectById            thrpt    2   344.053          ops/ms
JMHMain.mybatisComplexMapping    thrpt    2   101.239          ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    2   212.812          ops/ms
JMHMain.mybatisFile              thrpt    2   137.140          ops/ms
JMHMain.mybatisInsert            thrpt    2   150.837          ops/ms
JMHMain.mybatisLambdaQuery       thrpt    2    14.421          ops/ms
JMHMain.mybatisPageQuery         thrpt    2    64.915          ops/ms
JMHMain.mybatisSelectById        thrpt    2   222.265          ops/ms
JMHMain.weedExecuteJdbc          thrpt    2   430.943          ops/ms
JMHMain.weedExecuteTemplate      thrpt    2   430.418          ops/ms
JMHMain.weedFile                 thrpt    2   493.588          ops/ms
JMHMain.weedInsert               thrpt    2   241.865          ops/ms
JMHMain.weedLambdaQuery          thrpt    2   441.746          ops/ms
JMHMain.weedPageQuery            thrpt    2   252.932          ops/ms
JMHMain.weedSelectById           thrpt    2   434.757          ops/ms
```
JDBC作为基准测试，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试