# 测试DAO
本例使用H2的内存模式测试，并且，尽量让Entity最为简单，以最大程度验证Dao自身的性能
* BeetlSQL (国产)
* MyBatis-Plus (国产+Mybatis)
* JPA(Spring Data)
* JDBC (基准)
* Wood (国产)
* MyBatis-Flex (国产+MyBatis)
* EasyQuery (国产)
* DBVisitor (国产)

```
mvn clean package
java -jar targets/jmh.jar
```
或者测试特定orm工具
```
java -jar targets/jmh.jar Beetl Jdbc
```

或者测试特定orm特定方法
```
java -jar targets/jmh.jar Beetl.getEntity Jdbc.getEntity
```
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




# 最新测试结果 2024-05-17  ，添加DBVisitor
```
Benchmark                         Mode  Cnt    Score     Error   Units
Beetl.addEntity                  thrpt    5  131.153 ±  36.755  ops/ms
Beetl.complexMapping             thrpt    5  241.160 ± 230.660  ops/ms
Beetl.executeJdbcSql             thrpt    5  283.492 ±  73.667  ops/ms
Beetl.executeTemplateSql         thrpt    5  261.751 ±  92.532  ops/ms
Beetl.getAll                     thrpt    5   13.283 ±   1.476  ops/ms
Beetl.getEntity                  thrpt    5  341.775 ±  87.817  ops/ms
Beetl.lambdaQuery                thrpt    5  178.291 ±  76.958  ops/ms
Beetl.one2Many                   thrpt    5  182.116 ± 121.774  ops/ms
Beetl.pageQuery                  thrpt    5  156.156 ±  61.772  ops/ms
Beetl.sqlFile                    thrpt    5  263.121 ±  74.834  ops/ms
DBVisitor.addEntity              thrpt    5  100.069 ±  32.580  ops/ms
DBVisitor.executeJdbcSql         thrpt    5  182.828 ±  44.383  ops/ms
DBVisitor.executeTemplateSql     thrpt    5    3.198 ±   0.756  ops/ms
DBVisitor.getAll                 thrpt    5    3.135 ±   0.618  ops/ms
DBVisitor.getEntity              thrpt    5  121.144 ±  34.588  ops/ms
DBVisitor.lambdaQuery            thrpt    5   96.125 ±  41.567  ops/ms
DBVisitor.pageQuery              thrpt    5   48.819 ±  15.258  ops/ms
EasyQuery.addEntity              thrpt    5   92.616 ±  36.392  ops/ms
EasyQuery.complexMapping         thrpt    5   63.627 ±  59.919  ops/ms
EasyQuery.executeJdbcSql         thrpt    5  233.435 ±  44.917  ops/ms
EasyQuery.getAll                 thrpt    5   14.289 ±   1.803  ops/ms
EasyQuery.getEntity              thrpt    5  132.508 ±  66.459  ops/ms
EasyQuery.lambdaQuery            thrpt    5  134.101 ±  58.575  ops/ms
EasyQuery.one2Many               thrpt    5   70.632 ±  76.097  ops/ms
EasyQuery.pageQuery              thrpt    5   77.469 ±  48.158  ops/ms
Flex.addEntity                   thrpt    5   83.676 ±  35.254  ops/ms
Flex.getAll                      thrpt    5    2.841 ±   0.956  ops/ms
Flex.getEntity                   thrpt    5   70.677 ±  41.386  ops/ms
Flex.pageQuery                   thrpt    5   33.501 ±  28.323  ops/ms
GeneralBeetl.addEntity           thrpt    5  106.681 ±  41.071  ops/ms
GeneralBeetl.complexMapping      thrpt    5  221.872 ± 291.360  ops/ms
GeneralBeetl.executeJdbcSql      thrpt    5  145.154 ±  41.654  ops/ms
GeneralBeetl.executeTemplateSql  thrpt    5  134.640 ±  34.154  ops/ms
GeneralBeetl.getAll              thrpt    5    7.322 ±   1.034  ops/ms
GeneralBeetl.getEntity           thrpt    5  127.783 ±  41.652  ops/ms
GeneralBeetl.lambdaQuery         thrpt    5  107.401 ±  52.390  ops/ms
GeneralBeetl.one2Many            thrpt    5  123.169 ± 104.847  ops/ms
GeneralBeetl.pageQuery           thrpt    5   95.685 ±  37.589  ops/ms
GeneralBeetl.sqlFile             thrpt    5  133.986 ±  39.981  ops/ms
Jdbc.addEntity                   thrpt    5  215.327 ± 228.142  ops/ms
Jdbc.executeJdbcSql              thrpt    5  704.882 ± 120.221  ops/ms
Jdbc.getAll                      thrpt    5   34.915 ±   9.732  ops/ms
Jdbc.getEntity                   thrpt    5  708.092 ± 121.573  ops/ms
Jpa.addEntity                    thrpt    5   49.685 ±  84.597  ops/ms
Jpa.executeJdbcSql               thrpt    5   62.368 ±  56.983  ops/ms
Jpa.executeTemplateSql           thrpt    5   64.321 ±  64.992  ops/ms
Jpa.getAll                       thrpt    5    4.970 ±   1.593  ops/ms
Jpa.getEntity                    thrpt    5   56.314 ±  56.988  ops/ms
Jpa.one2Many                     thrpt    5   96.373 ±  95.969  ops/ms
Jpa.pageQuery                    thrpt    5   58.908 ±  42.332  ops/ms
MyBatis.addEntity                thrpt    5   41.215 ±  11.790  ops/ms
MyBatis.complexMapping           thrpt    5  101.919 ±  85.690  ops/ms
MyBatis.executeTemplateSql       thrpt    5   46.982 ±   8.396  ops/ms
MyBatis.getAll                   thrpt    5    5.618 ±   1.167  ops/ms
MyBatis.getEntity                thrpt    5   43.790 ±  18.015  ops/ms
MyBatis.lambdaQuery              thrpt    5   24.296 ±  14.280  ops/ms
MyBatis.pageQuery                thrpt    5   15.815 ±   5.403  ops/ms
MyBatis.sqlFile                  thrpt    5   44.577 ±  16.104  ops/ms
Wood.addEntity                   thrpt    5  110.456 ±  44.699  ops/ms
Wood.executeJdbcSql              thrpt    5  133.508 ±  33.219  ops/ms
Wood.executeTemplateSql          thrpt    5  133.187 ±  39.569  ops/ms
Wood.getAll                      thrpt    5    2.133 ±   0.456  ops/ms
Wood.getEntity                   thrpt    5  135.654 ±  34.405  ops/ms
Wood.lambdaQuery                 thrpt    5  131.672 ±  32.597  ops/ms
Wood.pageQuery                   thrpt    5  230.682 ±  81.406  ops/ms
Wood.sqlFile                     thrpt    5  136.683 ±  37.108  ops/ms
```

# 024-05-09  ，采用一个线程和独立的数据源,重构测试

```
Benchmark                         Mode  Cnt    Score     Error   Units
JMHMain.beetlsqlComplexMapping   thrpt    5  233.156 ± 208.476  ops/ms
JMHMain.beetlsqlExecuteJdbc      thrpt    5  282.260 ±  24.661  ops/ms
JMHMain.beetlsqlExecuteTemplate  thrpt    5  246.209 ±  12.273  ops/ms
JMHMain.beetlsqlFile             thrpt    5  250.863 ±  15.360  ops/ms
JMHMain.beetlsqlGetAll           thrpt    5   12.516 ±   1.146  ops/ms
JMHMain.beetlsqlInsert           thrpt    5  126.183 ±  29.520  ops/ms
JMHMain.beetlsqlLambdaQuery      thrpt    5  185.843 ±  12.943  ops/ms
JMHMain.beetlsqlOne2Many         thrpt    5  159.150 ±  60.892  ops/ms
JMHMain.beetlsqlPageQuery        thrpt    5  150.434 ±   9.298  ops/ms
JMHMain.beetlsqlSelectById       thrpt    5  328.551 ±  19.237  ops/ms
JMHMain.easyQueryComplexMapping  thrpt    5   62.247 ±  46.338  ops/ms
JMHMain.easyQueryExecuteJdbc     thrpt    5  231.846 ±  26.835  ops/ms
JMHMain.easyQueryGetAll          thrpt    5   16.385 ±   3.068  ops/ms
JMHMain.easyQueryInsert          thrpt    5   93.631 ±  39.568  ops/ms
JMHMain.easyQueryLambdaQuery     thrpt    5  112.432 ±  26.381  ops/ms
JMHMain.easyQueryOne2Many        thrpt    5   81.791 ±  62.740  ops/ms
JMHMain.easyQueryPageQuery       thrpt    5   72.185 ±  17.946  ops/ms
JMHMain.easyQuerySelectById      thrpt    5  113.857 ±  10.999  ops/ms
JMHMain.flexGetAll               thrpt    5    2.461 ±   0.341  ops/ms
JMHMain.flexInsert               thrpt    5   95.708 ±  13.937  ops/ms
JMHMain.flexPageQuery            thrpt    5   30.701 ±  13.874  ops/ms
JMHMain.flexSelectById           thrpt    5   63.497 ±  18.736  ops/ms
JMHMain.jdbcExecuteJdbc          thrpt    5  629.189 ±  39.342  ops/ms
JMHMain.jdbcGetAll               thrpt    5   35.020 ±   1.305  ops/ms
JMHMain.jdbcInsert               thrpt    5  245.532 ± 137.001  ops/ms
JMHMain.jdbcSelectById           thrpt    5  665.782 ±  44.120  ops/ms
JMHMain.jpaExecuteJdbc           thrpt    5   53.824 ±  65.500  ops/ms
JMHMain.jpaExecuteTemplate       thrpt    5   63.023 ±  46.466  ops/ms
JMHMain.jpaGetAll                thrpt    5    4.837 ±   1.474  ops/ms
JMHMain.jpaInsert                thrpt    5   45.563 ±  88.693  ops/ms
JMHMain.jpaOne2Many              thrpt    5   86.590 ± 100.940  ops/ms
JMHMain.jpaPageQuery             thrpt    5   57.796 ±  40.838  ops/ms
JMHMain.jpaSelectById            thrpt    5   54.654 ±  56.873  ops/ms
JMHMain.mybatisComplexMapping    thrpt    5  102.544 ±  54.211  ops/ms
JMHMain.mybatisExecuteTemplate   thrpt    5   41.866 ±   9.671  ops/ms
JMHMain.mybatisFile              thrpt    5   39.227 ±  12.103  ops/ms
JMHMain.mybatisGetAll            thrpt    5    5.537 ±   0.952  ops/ms
JMHMain.mybatisInsert            thrpt    5   42.532 ±   9.035  ops/ms
JMHMain.mybatisLambdaQuery       thrpt    5   22.435 ±  10.581  ops/ms
JMHMain.mybatisPageQuery         thrpt    5   15.777 ±   4.858  ops/ms
JMHMain.mybatisSelectById        thrpt    5   42.815 ±   9.291  ops/ms
JMHMain.woodExecuteJdbc          thrpt    5  118.146 ±   3.004  ops/ms
JMHMain.woodExecuteTemplate      thrpt    5  120.074 ±   0.877  ops/ms
JMHMain.woodFile                 thrpt    5  123.143 ±   5.414  ops/ms
JMHMain.woodGetAll               thrpt    5    1.855 ±   0.292  ops/ms
JMHMain.woodInsert               thrpt    5  101.123 ±  17.260  ops/ms
JMHMain.woodLambdaQuery          thrpt    5  120.616 ±  10.396  ops/ms
JMHMain.woodPageQuery            thrpt    5  222.833 ±  16.873  ops/ms
JMHMain.woodSelectById           thrpt    5  121.595 ±   4.646  ops/ms

```


JDBC作为基准测试，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试