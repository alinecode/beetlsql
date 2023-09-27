# 测试DAO
本例使用H2的内存模式测试，并且，尽量让Entity最为简单，以最大程度验证Dao自身的性能
* BeetlSQL (国产)
* MyBatis-Plus (国产)
* JPA(Spring Data)
* JDBC (基准)
* Wood (国产)
* MyBatis-Flex (国产)
* EasyQuery (国产)

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

 进入JMHMain，运行即可。如果你有新的测试方法，可以暂时屏蔽其他测试方法 ,测试中score越大性能越好.


# 最新测试结果 2023-9-27 : 10个线程并发

测试调整为10个线程并发,之前是1个线程

```
Benchmark                         Mode  Cnt     Score     Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    5   341.271 ± 144.439  ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    5  1205.169 ±  84.461  ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    5   625.493 ± 562.092  ops/ms
JMHMain.beetlsqlFile             thrpt    5   728.505 ± 192.016  ops/ms
JMHMain.beetlsqlGetAll           thrpt    5    35.727 ±   5.299  ops/ms
JMHMain.beetlsqlInsert           thrpt    5   309.352 ± 305.704  ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    5   652.515 ± 482.527  ops/ms
JMHMain.beetlsqlPageQuery        thrpt    5   389.702 ± 218.518  ops/ms
JMHMain.beetlsqlSelectById       thrpt    5   744.182 ± 599.943  ops/ms
JMHMain.easyQueryComplexMapping  thrpt    5   217.903 ± 142.958  ops/ms
JMHMain.easyQueryExecuteJdbc     thrpt    5  1217.743 ± 259.318  ops/ms
JMHMain.easyQueryGetAll          thrpt    5    90.982 ±  40.325  ops/ms
JMHMain.easyQueryInsert          thrpt    5   203.226 ± 341.035  ops/ms
JMHMain.easyQueryLambdaQuery     thrpt    5   644.360 ± 320.116  ops/ms
JMHMain.easyQueryOne2Many        thrpt    5   363.330 ± 318.428  ops/ms
JMHMain.easyQueryPageQuery       thrpt    5   331.492 ± 260.133  ops/ms
JMHMain.easyQuerySelectById      thrpt    5   560.184 ± 366.417  ops/ms
JMHMain.flexGetAll               thrpt    5    12.461 ±   5.274  ops/ms
JMHMain.flexInsert               thrpt    5   302.890 ± 212.731  ops/ms
JMHMain.flexPageQuery            thrpt    5   127.154 ± 127.519  ops/ms
JMHMain.flexSelectById           thrpt    5   306.615 ± 165.034  ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    5  2606.567 ± 572.964  ops/ms
JMHMain.jdbcGetAll               thrpt    5   183.528 ± 112.752  ops/ms
JMHMain.jdbcInsert               thrpt    5   445.636 ± 288.246  ops/ms
JMHMain.jdbcSelectById           thrpt    5  2593.640 ± 694.213  ops/ms
JMHMain.jpaExecuteJdbc           thrpt    5   263.471 ± 286.122  ops/ms
JMHMain.jpaExecuteTemplate       thrpt    5   254.714 ± 347.692  ops/ms
JMHMain.jpaGetAll                thrpt    5    23.625 ±  17.829  ops/ms
JMHMain.jpaInsert                thrpt    5   151.212 ± 239.483  ops/ms
JMHMain.jpaOne2Many              thrpt    5   445.323 ± 756.766  ops/ms
JMHMain.jpaPageQuery             thrpt    5   278.079 ± 324.433  ops/ms
JMHMain.jpaSelectById            thrpt    5   234.647 ± 358.503  ops/ms
JMHMain.mybatisComplexMapping    thrpt    5   402.651 ± 442.974  ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    5   201.648 ± 132.191  ops/ms
JMHMain.mybatisFile              thrpt    5   160.964 ± 186.912  ops/ms
JMHMain.mybatisGetAll            thrpt    5    25.431 ±  15.673  ops/ms
JMHMain.mybatisInsert            thrpt    5   160.638 ± 120.865  ops/ms
JMHMain.mybatisLambdaQuery       thrpt    5   101.694 ±  92.292  ops/ms
JMHMain.mybatisPageQuery         thrpt    5    72.482 ±  56.954  ops/ms
JMHMain.mybatisSelectById        thrpt    5   204.396 ± 127.371  ops/ms
JMHMain.woodGetAll               thrpt    5     2.914 ±   1.029  ops/ms
JMHMain.woodInsert               thrpt    5   131.441 ±  45.869  ops/ms
JMHMain.woodLambdaQuery          thrpt    5   200.426 ±  13.264  ops/ms
JMHMain.woodSelectById           thrpt    5   194.686 ±  21.054  ops/ms

```

# 测试结果 2023-8-18

* beetlsql,mybatis-plus,mybatis-flex 版本更新
* jpa取消一级缓存

```
Benchmark                         Mode  Cnt    Score     Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    5  255.458 ±  60.277  ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    5  310.825 ±  25.901  ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    5  272.615 ±  10.883  ops/ms
JMHMain.beetlsqlFile             thrpt    5  265.682 ±  15.083  ops/ms
JMHMain.beetlsqlGetAll           thrpt    5   13.869 ±   0.431  ops/ms
JMHMain.beetlsqlInsert           thrpt    5  129.510 ±  73.525  ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    5  194.066 ±  20.773  ops/ms
JMHMain.beetlsqlOne2Many         thrpt    5  174.328 ±  18.469  ops/ms
JMHMain.beetlsqlPageQuery        thrpt    5  153.528 ±  21.530  ops/ms
JMHMain.beetlsqlSelectById       thrpt    5  261.294 ±  10.722  ops/ms
JMHMain.easyQueryComplexMapping  thrpt    5   65.675 ±  24.593  ops/ms
JMHMain.easyQueryExecuteJdbc     thrpt    5  247.011 ±  10.968  ops/ms
JMHMain.easyQueryGetAll          thrpt    5   16.568 ±   1.000  ops/ms
JMHMain.easyQueryInsert          thrpt    5   97.206 ±  13.522  ops/ms
JMHMain.easyQueryLambdaQuery     thrpt    5  118.802 ±   8.587  ops/ms
JMHMain.easyQueryOne2Many        thrpt    5   92.302 ±  11.150  ops/ms
JMHMain.easyQueryPageQuery       thrpt    5   76.613 ±   6.511  ops/ms
JMHMain.easyQuerySelectById      thrpt    5  112.608 ±  15.603  ops/ms
JMHMain.flexGetAll               thrpt    5    2.512 ±   0.300  ops/ms
JMHMain.flexInsert               thrpt    5   99.749 ±  13.610  ops/ms
JMHMain.flexPageQuery            thrpt    5   34.197 ±   2.840  ops/ms
JMHMain.flexSelectById           thrpt    5   69.624 ±   3.205  ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    5  678.643 ±  22.680  ops/ms
JMHMain.jdbcGetAll               thrpt    5   40.712 ±   1.240  ops/ms
JMHMain.jdbcInsert               thrpt    5  248.417 ± 157.517  ops/ms
JMHMain.jdbcSelectById           thrpt    5  670.334 ±  41.391  ops/ms
JMHMain.jpaExecuteJdbc           thrpt    5   64.450 ±   8.940  ops/ms
JMHMain.jpaExecuteTemplate       thrpt    5   66.666 ±   6.162  ops/ms
JMHMain.jpaGetAll                thrpt    5    5.061 ±   0.736  ops/ms
JMHMain.jpaInsert                thrpt    5   59.919 ±  21.168  ops/ms
JMHMain.jpaOne2Many              thrpt    5  102.370 ±   5.603  ops/ms
JMHMain.jpaPageQuery             thrpt    5   59.366 ±  12.308  ops/ms
JMHMain.jpaSelectById            thrpt    5   61.110 ±  11.092  ops/ms
JMHMain.mybatisComplexMapping    thrpt    5  107.533 ±  21.488  ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    5   43.625 ±   3.022  ops/ms
JMHMain.mybatisFile              thrpt    5   39.576 ±   6.700  ops/ms
JMHMain.mybatisGetAll            thrpt    5    5.970 ±   0.584  ops/ms
JMHMain.mybatisInsert            thrpt    5   42.409 ±   4.339  ops/ms
JMHMain.mybatisLambdaQuery       thrpt    5   23.981 ±   6.069  ops/ms
JMHMain.mybatisPageQuery         thrpt    5   16.313 ±   3.207  ops/ms
JMHMain.mybatisSelectById        thrpt    5   44.626 ±   4.049  ops/ms
JMHMain.woodExecuteJdbc          thrpt    5  124.015 ±   3.367  ops/ms
JMHMain.woodExecuteTemplate      thrpt    5  123.251 ±   7.691  ops/ms
JMHMain.woodFile                 thrpt    5  124.088 ±  11.738  ops/ms
JMHMain.woodGetAll               thrpt    5    1.882 ±   0.148  ops/ms
JMHMain.woodInsert               thrpt    5  107.541 ±   9.479  ops/ms
JMHMain.woodLambdaQuery          thrpt    5  121.923 ±  11.266  ops/ms
JMHMain.woodPageQuery            thrpt    5  231.230 ±  16.197  ops/ms
JMHMain.woodSelectById           thrpt    5  124.842 ±  10.048  ops/ms

```


# 23-08-06

```
Benchmark                         Mode  Cnt    Score     Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    5  240.875 ±  26.462  ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    5  318.344 ±  15.416  ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    5  268.337 ±  16.458  ops/ms
JMHMain.beetlsqlFile             thrpt    5  266.299 ±  12.910  ops/ms
JMHMain.beetlsqlGetAll           thrpt    5   13.444 ±   0.110  ops/ms
JMHMain.beetlsqlInsert           thrpt    5  139.848 ±  67.006  ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    5  196.197 ±  10.572  ops/ms
JMHMain.beetlsqlOne2Many         thrpt    5  162.912 ±   7.643  ops/ms
JMHMain.beetlsqlPageQuery        thrpt    5  159.421 ±   9.474  ops/ms
JMHMain.beetlsqlSelectById       thrpt    5  259.260 ±  12.983  ops/ms
JMHMain.easyQueryComplexMapping  thrpt    5   70.631 ±   9.350  ops/ms
JMHMain.easyQueryExecuteJdbc     thrpt    5  251.553 ±  13.124  ops/ms
JMHMain.easyQueryGetAll          thrpt    5   15.556 ±   0.919  ops/ms
JMHMain.easyQueryInsert          thrpt    5   94.289 ±  46.075  ops/ms
JMHMain.easyQueryLambdaQuery     thrpt    5  116.482 ±   6.220  ops/ms
JMHMain.easyQueryOne2Many        thrpt    5   93.552 ±   7.485  ops/ms
JMHMain.easyQueryPageQuery       thrpt    5   79.880 ±   4.101  ops/ms
JMHMain.easyQuerySelectById      thrpt    5  118.695 ±   5.622  ops/ms
JMHMain.flexGetAll               thrpt    5    2.629 ±   0.175  ops/ms
JMHMain.flexInsert               thrpt    5   69.733 ±   9.791  ops/ms
JMHMain.flexPageQuery            thrpt    5   44.935 ±  13.077  ops/ms
JMHMain.flexSelectById           thrpt    5   69.332 ±   4.738  ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    5  692.908 ±  29.405  ops/ms
JMHMain.jdbcGetAll               thrpt    5   41.017 ±   0.999  ops/ms
JMHMain.jdbcInsert               thrpt    5  235.220 ± 170.271  ops/ms
JMHMain.jdbcSelectById           thrpt    5  685.740 ±  21.861  ops/ms
JMHMain.jpaExecuteJdbc           thrpt    5   67.159 ±   6.612  ops/ms
JMHMain.jpaExecuteTemplate       thrpt    5   75.465 ±   4.267  ops/ms
JMHMain.jpaGetAll                thrpt    5    5.279 ±   0.542  ops/ms
JMHMain.jpaInsert                thrpt    5   63.982 ±  10.347  ops/ms
JMHMain.jpaOne2Many              thrpt    5  105.669 ±   6.529  ops/ms
JMHMain.jpaPageQuery             thrpt    5   66.304 ±   4.215  ops/ms
JMHMain.jpaSelectById            thrpt    5  344.930 ±  15.419  ops/ms
JMHMain.mybatisComplexMapping    thrpt    5  114.651 ±   7.607  ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    5   44.020 ±   2.327  ops/ms
JMHMain.mybatisFile              thrpt    5   41.459 ±   1.880  ops/ms
JMHMain.mybatisGetAll            thrpt    5    4.961 ±   0.197  ops/ms
JMHMain.mybatisInsert            thrpt    5   43.624 ±   5.580  ops/ms
JMHMain.mybatisLambdaQuery       thrpt    5    9.141 ±   1.295  ops/ms
JMHMain.mybatisPageQuery         thrpt    5   17.451 ±   1.409  ops/ms
JMHMain.mybatisSelectById        thrpt    5   43.716 ±   4.768  ops/ms
JMHMain.woodExecuteJdbc          thrpt    5  106.223 ±  88.136  ops/ms
JMHMain.woodExecuteTemplate      thrpt    5  120.959 ±   5.220  ops/ms
JMHMain.woodFile                 thrpt    5  123.587 ±   9.268  ops/ms
JMHMain.woodGetAll               thrpt    5    1.961 ±   0.158  ops/ms
JMHMain.woodInsert               thrpt    5  101.279 ±  50.068  ops/ms
JMHMain.woodLambdaQuery          thrpt    5  128.158 ±   7.174  ops/ms
JMHMain.woodPageQuery            thrpt    5  240.657 ±  13.274  ops/ms
JMHMain.woodSelectById           thrpt    5  125.943 ±   5.696  ops/ms


```



JDBC作为基准测试，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试