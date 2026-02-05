SQL模版的语法糖，简化那种需要判断后再输出的情况，可以运行`ShortHolderTest`了解其使用

```java
SugarTemplateConfig sugarBeetlSQL = new SugarTemplateConfig();
sugarBeetlSQL.config(sqlManager);

```

# 简化 and 和 or

提供了`and`和`or`格式化函数
```sql
select * from user where 1=1 
	#{name,and} 
	#{userAge,and}
```
类似sql模版

```sql
select * from user where 1=1 
	@--: if(notEmpty(name)){
		and name=#{name}
	@--:}
	@--: if(notEmpty(userAge)){
		and user_age=#{userAge}
	@--:}
	
```

也允许使用较为复杂的表达式
```sql
select * from user u where 1=1 
	#{"u.age">age+1,and} 
	
```
beetlsql插件将解析#{}中的第一个变量age，生成类似sql模版
```sql
select * from user u where 1=1 
	@--: if(notEmpty(age)){
		and u.age>#{age+1}
	@--:}
```

# 简化 update
提供了`set`格式化函数

```sql
update user set #{name,set} #{userAge,set}  where id =#{id}
```
类似sql模版
```sql
update user set 
	@--: if(notEmpty(name)){
		 name=#{name}
	@--:}
	@--: if(notEmpty(userAge)){
		,user_age=#{userAge}
	@--:}
	
```

注意，此扩展包会检测是否sql语句是否以`set`结尾。 如果没有，则增加`,`.  占位符里可以用任意表达式，beetlsql会找到第一个变量来判断非空
这个同`and` `or` 一致


# 简化order by
允许使用格式化函数`asc` `desc` 来自动增加一个order by语句。

```sql
select * from user ${orderCol,asc}
	
```
类似sql模版

```sql
select * from user
	@--: if(notEmpty(orderCol)){
		${orderCol} asc
	@--:}
	
```

注意，目前此插件不支持多个列排序，只支持使用`asc` 或者 `desc` 一次。另外，只能使用${} 而非 #{}

## 如果你想实现语法糖

* 参考SugarSQLGrammarCreator，对beetl的sql模版语法树进行调整，比如使用`ShortHolderFactory.create` 返回新的PlaceholderST

```sql
public class SugarSQLGrammarCreator extends SQLGrammarCreator {
	public SugarSQLGrammarCreator(SQLManager sqlManager) {
		super(sqlManager);
	}
	@Override
	public PlaceholderST createTextOutputSt(Expression exp, FormatExpression format) {
		disableSyntaxCheck("TextOutputSt");
		if(format!=null&& ShortHolderFactory.isSupport(format.getName())){
			PlaceholderST st =ShortHolderFactory.create(sqlManager.getNc(),format.getName(),exp);
			if(st==null){
				throw new IllegalArgumentException("错误的用法 "+format.getName()+" "+exp.token.toString());
			}
			return st;
		}
		return new SQLPlaceholderST(exp, format, null);
	}
```