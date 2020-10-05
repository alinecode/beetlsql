BeetlSQL 所有插件开发例子。注意，有一定难度，建议初学者先掌握beetlsql基本功能再考虑开发扩展功能

* PluginAnnotation,关于自定义各种注解
    * 自定义一个属性注解@Jackson，用于把属性序列化成json存放到数据库或者反序列化到java属性里
    * 自定义一个Mapper方法的注解@Matcher，使得beetlsql执行的时候考虑一个Condition参数辅助生成sql
    * 自定义一个@LoadOne,当查询出结果后，在执行加载更多的数据
    * 自定义一个@Tenant注解，在任何关于此对象的数据库操作的时候，提供额外租户信息
* MappingSample 演示自定义映射查询结果集
    * 自定义个RowMapper，在BeetlSQL映射基础上，把一些字段映射到java属性里
    * 自定义一个BitIntTypeHandler和JsonNodeTypeHanlder，处理把结果集映射到BigInteger和JsonNode属性上
    * 自定义一个xml 配置文件的映射配置规则，类似mybatis
* InterceptSample 定义Interceptor
    * 自定义一个Interceptor，能在执行sql前为sql追加一个注释，内容是sqlId，这样方便数据库dba与程序员打交道
    * 修改DebugInteceptor ，将Debug内容输出到日志框架
* DbStyleSample ，BeetlSQL核心之一，了解beetlsql底层如何运行，随意根据需求扩展
    * 自定义一个Executor，执行底层jdbc操作

   




