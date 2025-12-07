# 说明

演示了SpringBoot2和SpringBoot3如何集成beetlsql

* 简单集成，单数据源，SimpleTest
* 多数据源，一主多从 MasterSlaveTest
* 动态SQLManager DynamicTest

使用者可以直接copy这些代码和beetlsql配置放到工程里使用


# 配置说明

配置参考类 BeetlSqlConfig

## 单个或者多个SQLManager情况下

```
beetlsql = sqlManger1,sqlManger2

beetlsql.sqlManger1.ds=ds1
beetlsql.sqlManger1.nc= org.beetl.sql.core.UnderlinedNameConversion
beetlsql.sqlManger1.basePackage=com.xxx,com.yyy
beetlsql.sqlManger1.daoSuffix = Mapper

beetlsql._default.nc =
beetlsql._default.daoSuffix = Mapper
```

> 如果是单个sqlManger，则beetlsql=sqlManger1既可

## 如果每个SQLManger是主从库

只需要在ds后面增加多个数据源即可
```
beetlsql.sqlManger1.ds=ds1,ds2
```


第一个数据源是主库，其他是从库，在所有标注@Transanal(readonly=true),的select方法，将访问从库


## 动态SQLManager

一个sqlmanager代理了多个sqlManager
```
beetlsql=sqlManger1
beetlsql.sqlManger1.dynamic = sqlManger2,sqlManger3
beetlsql.sqlManger1.dynamic.condititional = xxxxBean
beetlsql.sqlManger2.ds=ds1
beetlsql.sqlManger3.ds=ds2
```


# 修改SQLManager

如果想通过代码修改SQLManager，可以实现 SQLManagerCustomize 接口,参考 SimpleDataSourceConfig

```java
 @Bean
    public SQLManagerCustomize mySQLManagerCustomize(){
        return new SQLManagerCustomize(){

            @Override
            public void customize(String sqlMangerName, SQLManager manager) {
                System.out.println("change your config "+sqlMangerName);
            }
        };
    }

```