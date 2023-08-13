当数据库表是动态生成的，使用DynamicTableLoader类操作动态表，实现CRUD，会非常方便。

其原理是为每个动态表动态的生成对应的Java类，因此，Beetlsql可以像操作其他表一样操作动态表


```

SQLManager sqlManager = getSQLManager();
int max = 5;
//创建5个表
for(int i=0;i<max;i++){
	String dml="create table my_table"+i+"(id int NOT NULL"
	+ ",name varchar(20)"
	+ ",PRIMARY KEY (`id`)"
	+ ") ";
	sqlManager.executeUpdate(new SQLReady(dml));
}

//生成DynamicTableLoader实例，注意必须是单例子
DynamicEntityLoader<BaseEntity> dynamicEntityLoader = new DynamicEntityLoader(sqlManager);

for(int i=0;i<max;i++){
	// 获得动态表对应的java类
	Class<? extends BaseEntity> c = dynamicEntityLoader.getDynamicClass("my_table"+i);
	//查询动态表的总数
	long  count = sqlManager.allCount(c);
	System.out.println(count);
	//插入数据到动态表
	BaseEntity obj = c.newInstance();
	obj.setValue("id",1);
	obj.setValue("name","hello");
	sqlManager.insert(obj);
}



```

BaseObject是内置的一个具有setVaue和getValue的类，你可以自己定义基类
```java
public  class Office {

}

```
然后初始化dynamicTableLoader
```
DynamicEntityLoader<Office> dynamicEntityLoader = new DynamicEntityLoader(sqlManager,"com.temp",Office.class);

```

或者
```java
DynamicEntityLoader<BaseEntity> dynamicEntityLoader = new DynamicEntityLoader(sqlManager);
//指定父类
Class<? extends Office> c = dynamicEntityLoader.getDynamicClass("order_log",Office.class);

Class<? extends NyCommonOffice> c2 = dynamicEntityLoader.getDynamicClass("order_log",NyCommonOffice.class);

```

这样生成的类的父类是Office对象
```
Class<? extends Office> c = dynamicEntityLoader.getDynamicClass("order_log");
Office office = sqlManager.unique(c,1);
System.out.println(office.getValue("orderId"));
System.out.println(office.getValue("age"));

office.setValue("age",1);
sqlManager.updateById(office);

```



