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
java -jar target/jmh.jar
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




# 最新测试结果2025-8-2 

```
Benchmark                                  Mode  Cnt     Score      Error   Units
Beetl.addEntity                           thrpt    5   133.190 ±   38.548  ops/ms
Beetl.complexMapping                      thrpt    5   236.289 ±  288.370  ops/ms
Beetl.executeJdbcSql                      thrpt    5   298.657 ±   80.480  ops/ms
Beetl.executeTemplateSql                  thrpt    5   251.418 ±   77.757  ops/ms
Beetl.getAll                              thrpt    5    14.531 ±    0.605  ops/ms
Beetl.getEntity                           thrpt    5   359.275 ±   85.099  ops/ms
Beetl.lambdaQuery                         thrpt    5   187.228 ±   59.697  ops/ms
Beetl.one2Many                            thrpt    5   178.041 ±  181.829  ops/ms
Beetl.pageQuery                           thrpt    5   148.982 ±   39.327  ops/ms
Beetl.sqlFile                             thrpt    5   249.206 ±  100.083  ops/ms
DBVisitor.addEntity                       thrpt    5    99.294 ±   33.564  ops/ms
DBVisitor.executeJdbcSql                  thrpt    5   185.177 ±   50.993  ops/ms
DBVisitor.executeTemplateSql              thrpt    5     3.189 ±    0.748  ops/ms
DBVisitor.getAll                          thrpt    5     3.163 ±    0.980  ops/ms
DBVisitor.getEntity                       thrpt    5   125.683 ±   44.190  ops/ms
DBVisitor.lambdaQuery                     thrpt    5    94.314 ±   38.499  ops/ms
DBVisitor.pageQuery                       thrpt    5    49.954 ±   18.254  ops/ms
EasyQuery.addEntity                       thrpt    5    98.904 ±   39.890  ops/ms
EasyQuery.complexMapping                  thrpt    5    67.397 ±   59.919  ops/ms
EasyQuery.executeJdbcSql                  thrpt    5   243.160 ±   48.080  ops/ms
EasyQuery.getAll                          thrpt    5    14.896 ±    2.219  ops/ms
EasyQuery.getEntity                       thrpt    5   134.370 ±   67.962  ops/ms
EasyQuery.lambdaQuery                     thrpt    5   137.292 ±   53.438  ops/ms
EasyQuery.one2Many                        thrpt    5    75.083 ±   53.738  ops/ms
EasyQuery.pageQuery                       thrpt    5    84.596 ±   46.942  ops/ms
Flex.addEntity                            thrpt    5    86.484 ±   40.022  ops/ms
Flex.getAll                               thrpt    5     2.926 ±    0.993  ops/ms
Flex.getEntity                            thrpt    5    73.445 ±   39.437  ops/ms
Flex.pageQuery                            thrpt    5    33.999 ±   26.776  ops/ms
GeneralBeetl.addEntity                    thrpt    5   114.199 ±   44.321  ops/ms
GeneralBeetl.complexMapping               thrpt    5   225.466 ±  302.527  ops/ms
GeneralBeetl.executeJdbcSql               thrpt    5   142.470 ±   61.574  ops/ms
GeneralBeetl.executeTemplateSql           thrpt    5   133.518 ±   33.337  ops/ms
GeneralBeetl.getAll                       thrpt    5     8.117 ±    1.770  ops/ms
GeneralBeetl.getEntity                    thrpt    5   133.860 ±   39.847  ops/ms
GeneralBeetl.lambdaQuery                  thrpt    5   112.544 ±   30.221  ops/ms
GeneralBeetl.one2Many                     thrpt    5   134.852 ±   75.105  ops/ms
GeneralBeetl.pageQuery                    thrpt    5    94.309 ±   29.102  ops/ms
GeneralBeetl.sqlFile                      thrpt    5   139.083 ±   37.979  ops/ms
Jdbc.addEntity                            thrpt    5   225.223 ±  304.585  ops/ms
Jdbc.executeJdbcSql                       thrpt    5   691.616 ±  145.280  ops/ms
Jdbc.getAll                               thrpt    5    35.827 ±    7.295  ops/ms
Jdbc.getEntity                            thrpt    5   678.791 ±  165.527  ops/ms
Jpa.addEntity                             thrpt    5    53.219 ±   78.543  ops/ms
Jpa.executeJdbcSql                        thrpt    5    60.734 ±   61.299  ops/ms
Jpa.executeTemplateSql                    thrpt    5    64.478 ±   48.688  ops/ms
Jpa.getAll                                thrpt    5     4.710 ±    2.062  ops/ms
Jpa.getEntity                             thrpt    5    56.325 ±   53.744  ops/ms
Jpa.one2Many                              thrpt    5    92.168 ±  111.591  ops/ms
Jpa.pageQuery                             thrpt    5    58.756 ±   48.056  ops/ms
MyBatis.addEntity                         thrpt    5    40.533 ±   12.461  ops/ms
MyBatis.complexMapping                    thrpt    5   106.348 ±   57.688  ops/ms
MyBatis.executeTemplateSql                thrpt    5    46.953 ±   13.541  ops/ms
MyBatis.getAll                            thrpt    5     5.394 ±    0.502  ops/ms
MyBatis.getEntity                         thrpt    5    45.303 ±   11.928  ops/ms
MyBatis.lambdaQuery                       thrpt    5    23.756 ±   13.613  ops/ms
MyBatis.pageQuery                         thrpt    5    16.190 ±    5.682  ops/ms
MyBatis.sqlFile                           thrpt    5    42.558 ±   18.025  ops/ms
SqlUtils.addEntity                        thrpt    5   232.501 ±  332.656  ops/ms
SqlUtils.executeJdbcSql                   thrpt    5   682.610 ±  171.506  ops/ms
SqlUtils.getAll                           thrpt    5    44.353 ±    4.346  ops/ms
SqlUtils.getEntity                        thrpt    5   643.873 ±  131.587  ops/ms
Wood.addEntity                            thrpt    5   105.097 ±   79.961  ops/ms
Wood.executeJdbcSql                       thrpt    5   127.020 ±   48.142  ops/ms
Wood.executeTemplateSql                   thrpt    5   129.193 ±   44.455  ops/ms
Wood.getAll                               thrpt    5     2.051 ±    0.524  ops/ms
Wood.getEntity                            thrpt    5   126.104 ±   41.976  ops/ms
Wood.lambdaQuery                          thrpt    5   125.013 ±   43.991  ops/ms
Wood.pageQuery                            thrpt    5   220.101 ±   90.028  ops/ms
Wood.sqlFile                              thrpt    5   132.431 ±   50.141  ops/ms


```


JDBC作为基准测试(SqlUtils类似)，在不考虑JDBC的情况下，BeetlSQL性能比MyBatis和JPA都好很多 
另外BeetlSQL支持全部9个场景的测试