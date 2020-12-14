local: 如果只是操作多库，本地回滚，本地回滚失败放弃
microservice：如果是多库+微服务。需要基础设施kafka+ 和启动beetlsql-saga-server作为事务管理器



**注意**，sega模式并不能像XA那样实现数据隔离，实现数据隔离必须业务上考虑如何数据隔离，saga模块按照saga原理来实现回滚，保证数据一致

参考代码`SimpleTest`
```
@Test
	public void simple(){
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
		long count = sqlManager.allCount(User.class);
		try{
			sagaContext.start(); //标记开始事务
			User user = new User();
			user.setName("abc");
			userMapper.insert(user);
			User user2 = new User();
			user2.setName("abc");
			userMapper.insert(user2);
			if(1==1){
				throw new RuntimeException("模拟异常");
			}
			sagaContext.commit();  //标记提交事务
		}catch(RuntimeException ex){
			sagaContext.rollback();//标记回滚事务
		}
		long  afterCount = sqlManager.allCount(User.class);
		Assert.assertEquals(count,afterCount);
	}
```

UserMapper需要继承SagaMapper，而不是BaseMapper
```

public interface UserMapper extends SagaMapper<User> {
}

```

取决于应用场景level1_3，如上SagaContext有不同的实现.但其api保持不变



