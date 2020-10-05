BeetlSQL 的代码生成框架，并提供一些entity，md生成的样子。

代码生成框架提供了Entity和Attribute对象，并抽象了 "Project"，"SourceBuilder"，提供了很强的灵活性

用户可以自己扩展生成任意代码。系统自带

* EntitySourceBuilder ，生成POJO代码
* MapperSourceBuilder， 生成Mapper
* MDSourceBuilder 生成markdown的sql文件
* MDDocBuilder，生成数据库表说明文档

* ConsoleOnlyProject 生成代码输出到控制台
* SimpleMavenProject 生成文档输出到工程里
* StringOnlyProject  生成结果是字符串

