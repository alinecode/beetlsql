BeetlSQL和Jooq集成，充分利用Jooq使用Java代码拼接SQL的优


```java
JooqHelper jooqHelper = new JooqHelper(sqlManager);

ReLog reLog= jooqHelper.queryOne(ReLog.class,
	create -> create.select().from(ORDER_LOG).where(ORDER_LOG.VERSION.eq(100))
);

List<ReLog> lists = jooqHelper.query(ReLog.class,
	create -> create.select().from(ORDER_LOG).where(ORDER_LOG.VERSION.eq(100))
);

```

Jooq使用前必须生成表和列的类和字符串，你可以按照其官方文档说明生成，或者使用JooqCodeGenHelper来生成

如下代码生成代码位于当前工程的test代码里，

```java
JooqCodeGenHelper codeGen = new JooqCodeGenHelper("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE", "sa", ""
			, "org.h2.Driver", "com.yourpackge", "./src/test/java");

codeGen.genCode(null, "PUBLIC", false);
```

