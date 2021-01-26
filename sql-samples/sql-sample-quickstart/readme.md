BeetlSQL使用简单实例,采用H2,可以直接运行，H2的脚本在common工程里，sample.sql
* S1QuickStart.SQLMananger API，链式操作Query类，Mapper使用，基本的CRUD映射
* S2MappingSample: 如何把结果集映射到Java对象，通过注解，通过json配置，或者约定习俗进行复杂映射，通过自定义注解来扩展映射方式
* S3PageSample: 翻页和范围查询
* S4Other: 其他常用操作示例，一些常见的like，in，batch操作
* S5Fetch：自动fetch功能 ，在查询对象后，还可以自动fetch其他对象，类似JPA的ORM，但ORM对CRUD影响过大，fetch功能则简单很多
* S6MoreSource: 非常方便的实现多数据源，每个实体可以标记自己的数据源;或者简单一个主从数据库的例子;或者分表例子；或者分库+分表。
* S7CodeGen: 使用BeetlSQL生成代码，SQL语句和数据库文档


用户能在2小时内浏览完所有例子并基本掌握BeetlSQL的用法，如果需要更多例子，请参考sql-sample-usage模块，包含了几乎所有用法


