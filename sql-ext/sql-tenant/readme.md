支持多租户，数据权限，逻辑删除等 需要重写sql，增加额外过滤条件的场景，可以运行RewriteTest和TenantTest了解如何使用

```java
TenantConfig tenantConfig = new TenantConfig();
tenantConfig.config(sqlManager);
tenantConfig.addColRewriteConfig(new ColRewriteParam("tenant_id", new ColValueProvider() {
	@Override
	public Object getCurrentValue() {
	return localValue.get();
	}
}));


```

如上应该在应用初始化时候配置，这样，当遇到sql中的表具有tenant_id字段的时候，将会发生sql改写，改写的值为ColValueProvider提供
如下sql
```sql
select * from user u ;
```
将被改写成
```sql
select * from user u where u.tenant_id= x 
```
需要注意，并不是所有sql都被改写，**只有**集成了RewriteBaseMapper的Mapper发出的操作才能触发sql改写（SqlRewriteInterceptor）

如下mapper的select方法，或者是内置的all,updateById,都会触发改写
```java
public interface MyRewriteMapper extends RewriteBaseMapper<OrderLog> {
	List<OrderLog> select(String name);
}

```

逻辑删除，数据权限过滤也可以用此方法,比如，通过dept_id 来过滤数据权限

```java
tenantConfig.addColRewriteConfig(new ColRewriteParam("dept_id", new ColValueProvider() {
	@Override
	public Object getCurrentValue() {
		return Array.asList(1,2,3); 
	}
}));

```

那么如下sql
```sql
select * from user u ;
```
会重写
```sql
select * from user u where u.dept_id in (1,2,3);
```

此包仅仅提供数据重写，可选情况，建议使用AtrributeConvert来实现@TenantId注解，以动态提供值
```java
@TenantId
private long tenantId
```
这样不需要程序员显示赋值
```java
User user = new User();
user.setName(xx);
//user.setTenantId(); 不需要,使用@TenantId赋值
userMapper.insert(user)
```

 