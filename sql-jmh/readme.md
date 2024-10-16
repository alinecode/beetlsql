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
* SqlUtils

```
mvn clean package
java -jar targets/jmh.jar
```
或者测试特定orm工具
```
java -jar target/jmh.jar Beetl Jdbc
```

或者测试特定orm特定方法
```
java -jar target/jmh.jar Beetl.getEntity Jdbc.getEntity
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




# 最新测试结果2024-10-17  ，添加SqlUtils
```
Benchmark                         Mode  Cnt    Score     Error   Units
Beetl.addEntity                  thrpt    5  107.421 ±  63.432  ops/ms
Beetl.complexMapping             thrpt    5  199.583 ± 291.175  ops/ms
Beetl.executeJdbcSql             thrpt    5  294.096 ±  72.272  ops/ms
Beetl.executeTemplateSql         thrpt    5  248.226 ±  88.204  ops/ms
Beetl.getAll                     thrpt    5   13.582 ±   0.590  ops/ms
Beetl.getEntity                  thrpt    5  324.596 ±  79.393  ops/ms
Beetl.lambdaQuery                thrpt    5  172.802 ±  63.639  ops/ms
Beetl.one2Many                   thrpt    5  159.437 ± 130.334  ops/ms
Beetl.pageQuery                  thrpt    5  136.335 ±  45.815  ops/ms
Beetl.sqlFile                    thrpt    5  219.103 ±  46.331  ops/ms
DBVisitor.addEntity              thrpt    5   89.384 ±  44.257  ops/ms
DBVisitor.executeJdbcSql         thrpt    5  169.082 ±  52.123  ops/ms
DBVisitor.executeTemplateSql     thrpt    5    3.195 ±   0.957  ops/ms
DBVisitor.getAll                 thrpt    5    3.024 ±   0.737  ops/ms
DBVisitor.getEntity              thrpt    5  111.517 ±  73.239  ops/ms
DBVisitor.lambdaQuery            thrpt    5   96.086 ±  35.182  ops/ms
DBVisitor.pageQuery              thrpt    5   47.389 ±  16.052  ops/ms
EasyQuery.addEntity              thrpt    5   92.502 ±  15.537  ops/ms
EasyQuery.complexMapping         thrpt    5   55.517 ±  70.688  ops/ms
EasyQuery.executeJdbcSql         thrpt    5  227.574 ±  86.152  ops/ms
EasyQuery.getAll                 thrpt    5   14.828 ±   1.719  ops/ms
EasyQuery.getEntity              thrpt    5  125.526 ±  87.996  ops/ms
EasyQuery.lambdaQuery            thrpt    5  130.913 ±  49.065  ops/ms
EasyQuery.one2Many               thrpt    5   71.507 ±  64.279  ops/ms
EasyQuery.pageQuery              thrpt    5   85.120 ±  45.768  ops/ms
Flex.addEntity                   thrpt    5   82.741 ±  35.010  ops/ms
Flex.getAll                      thrpt    5    2.857 ±   0.938  ops/ms
Flex.getEntity                   thrpt    5   72.427 ±  48.586  ops/ms
Flex.pageQuery                   thrpt    5   31.866 ±  26.011  ops/ms
GeneralBeetl.addEntity           thrpt    5  101.724 ±  51.099  ops/ms
GeneralBeetl.complexMapping      thrpt    5  195.298 ± 278.922  ops/ms
GeneralBeetl.executeJdbcSql      thrpt    5  141.368 ±  41.212  ops/ms
GeneralBeetl.executeTemplateSql  thrpt    5  129.438 ±  30.630  ops/ms
GeneralBeetl.getAll              thrpt    5    7.889 ±   1.251  ops/ms
GeneralBeetl.getEntity           thrpt    5  127.539 ±  41.250  ops/ms
GeneralBeetl.lambdaQuery         thrpt    5  106.850 ±  42.768  ops/ms
GeneralBeetl.one2Many            thrpt    5  128.468 ±  86.845  ops/ms
GeneralBeetl.pageQuery           thrpt    5   93.118 ±  32.210  ops/ms
GeneralBeetl.sqlFile             thrpt    5  134.057 ±  27.093  ops/ms
Jdbc.addEntity                   thrpt    5  212.159 ± 239.850  ops/ms
Jdbc.executeJdbcSql              thrpt    5  661.997 ± 127.704  ops/ms
Jdbc.getAll                      thrpt    5   34.760 ±   9.619  ops/ms
Jdbc.getEntity                   thrpt    5  686.916 ± 136.272  ops/ms
Jpa.addEntity                    thrpt    5   49.954 ±  77.964  ops/ms
Jpa.executeJdbcSql               thrpt    5   57.676 ±  62.554  ops/ms
Jpa.executeTemplateSql           thrpt    5   66.322 ±  45.752  ops/ms
Jpa.getAll                       thrpt    5    4.492 ±   1.375  ops/ms
Jpa.getEntity                    thrpt    5   55.482 ±  53.664  ops/ms
Jpa.one2Many                     thrpt    5   88.538 ± 101.083  ops/ms
Jpa.pageQuery                    thrpt    5   59.696 ±  32.990  ops/ms
MyBatis.addEntity                thrpt    5   39.380 ±   9.763  ops/ms
MyBatis.complexMapping           thrpt    5  106.194 ±  52.432  ops/ms
MyBatis.executeTemplateSql       thrpt    5   46.564 ±   9.037  ops/ms
MyBatis.getAll                   thrpt    5    5.534 ±   0.603  ops/ms
MyBatis.getEntity                thrpt    5   44.593 ±  21.428  ops/ms
MyBatis.lambdaQuery              thrpt    5   23.765 ±  12.741  ops/ms
MyBatis.pageQuery                thrpt    5   15.374 ±   5.496  ops/ms
MyBatis.sqlFile                  thrpt    5   42.523 ±  14.635  ops/ms
SqlUtils.addEntity               thrpt    5  230.601 ± 334.842  ops/ms
SqlUtils.executeJdbcSql          thrpt    5  493.587 ±  98.666  ops/ms
SqlUtils.getAll                  thrpt    5   12.910 ±   1.889  ops/ms
SqlUtils.getEntity               thrpt    5  449.299 ±  89.746  ops/ms
Wood.addEntity                   thrpt    5  110.529 ±  42.803  ops/ms
Wood.executeJdbcSql              thrpt    5  124.189 ±  44.326  ops/ms
Wood.executeTemplateSql          thrpt    5  129.305 ±  23.248  ops/ms
Wood.getAll                      thrpt    5    2.013 ±   0.394  ops/ms
Wood.getEntity                   thrpt    5  125.678 ±  43.944  ops/ms
Wood.lambdaQuery                 thrpt    5  123.713 ±  47.535  ops/ms
Wood.pageQuery                   thrpt    5  215.478 ±  84.635  ops/ms
Wood.sqlFile                     thrpt    5  130.792 ±  47.667  ops/ms

```


JDBC作为基准测试，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试