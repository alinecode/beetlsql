用于加速beetlsql,性能至少提升50%，有的提升500%

* 加强反射调用，通过字节码生成BeanPropertyWrite
* 加强NameConversion,使用了缓存，避免频繁调用
* 对beetlsql使用的beetl模板进行增强，如使用发射优化，对AST语法树进行优化

```java

PerformanceConfig perforanceConfig = new PerformanceConfig();
perforanceConfig.config(sqlManagaer)

```