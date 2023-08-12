SQL防火墙，用于防止一些意外操作，如删除全表


```java

FireWall fireWall = new FireWall().setDmlCreateEnable(false).setSqlMaxLength(50);
FireWallConfig fireWallConfig = new FireWallConfig(fireWall);
fireWallConfig.config(sqlManager);

try{
	String sql = "delete from order_log";
	sqlManager.executeUpdate(new SQLReady(sql));
	Assert.fail();
}catch (Exception exception){
	Assert.assertTrue(exception instanceof  BeetlSQLException);
}

```

> 注意，因为涉及到解析sql，因此有性能损耗