如果多数据源没有分布式事务管理，sega模块可以实现分布式事务的功能，能统一提交和回滚，回滚调用根据SegaMapper的API自动生成

**注意**，sega模式并不能像XA那样实现数据隔离，实现数据隔离必须数据库提供，sega模块按照sega原理来实现回滚，保证数据一致

参考代码`org.beetlsql.sql.sega.test.SimpleTest`
```
UserMapper userMapper = sqlManager.getMapper(UserMapper.class);
long count = sqlManager.allCount(User.class);
try{
    User user = new User();
    user.setName("abc");
    userMapper.insert(user);
    User user2 = new User();
    user2.setName("abc");
    userMapper.insert(user2);
    throw new RuntimeException("模拟异常");
}catch(RuntimeException ex){
    segaContext.rollback();
}

```

UserMapper需要继承SegaMapper，而不是BaseMapper
```

public interface UserMapper extends SegaMapper<User> {
}

```

如果目标框架，比如spring 出现回滚，则自动使用sega模式进行回滚调用，不需要程序调用segaContext.rollback()