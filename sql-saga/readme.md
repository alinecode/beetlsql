如果想进一步了解Saga，可以参考apache的servicecomb https://docs.servicecomb.io/saga/zh_CN/design_zh.html
BeetlSQL的Saga相对于其他Saga来说，还有如下优点

* 不需要自己编写逆向操作，SagaMapper已经完成
* 实现非常简单，易于调试和扩展。把Saga抽象成逆向操作和任务管理
* 支持嵌套调用，任何微服务都可以作为Saga事务的一环，其他Saga—Server必须明确定义Saga事务的开始
* 提供Saga—Server管理的功能
* 支持多库操作，同一个代码，很容易从多库事务过度到微服务事务

Saga支持俩种模式，如下
local: 如果只是操作多库，本地回滚，本地回滚失败放弃。
microservice：如果是多库+微服务。需要基础设施kafka。 并且启动beetlsql-saga-server作为事务管理器

**注意**，sega模式并不能像XA那样实现数据隔离，实现数据隔离必须业务上考虑如何数据隔离，
saga模块按照saga原理来实现回滚，保证数据一致.如果你只是简单的多库，可以使用分布式事务而不是Saga。使用
BeetlSQL提供的Saga好处是很容易从多库迁移到微服务

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

取决于应用场景是local(多库）还是microservice，如上SagaContext有不同的实现.但其api保持不变



