#beetlsql 入门

同时具有Hibernate 优点 & Mybatis优点功能，适用于承认以SQL为中心，同时又需求工具能自动能生成大量常用的SQL的应用。

* SQL 以更简洁的方式，Markdown方式集中管理，同时方便程序开发和数据库SQL调试
* 数据模型支持Pojo，也支持Map/List这种无模型的模型
无需注解，自动生成大量内置SQL，轻易完成增删改查功能
* SQL 模板基于Beetl实现，更容易写和调试，以及扩展
* 简单支持关系映射而不引入复杂的OR Mapping概念和技术。
* 支持跨数据库平台，开发者所需工作减少到最小
* 具备Interceptor功能，可以调试，性能诊断SQL，以及扩展其他功能
* 内置支持主从数据库，通过扩展，可以支持更复杂的分库分表逻辑



代码例子
===
	 // 执行/user.md 里的select sql
	List<User>  list = sqlManager.select(“user.select”,paras,User.class);
	// 使用内置的生成的sql执行
	User user = sqlManager.unque(User.class,id);

SQL例子
===

	selectUser
	===
		    select * from user where 1=1
		    @if(user.age==1){
		    and age = #user.age#
		    @}
		    
	selectAll
	===
		    select * from user  
		    @use("selectWhere");
		    
	selectWhere
	===
		    where  age = #age#
	

Markdown方式管理
===
BeetlSQL集中管理SQL语句，SQL 可以按照业务逻辑放到一个文件里，文件可以按照模块逻辑放到一个目录下。文件格式抛弃了XML格式，采用了Markdown，原因是

* XML格式过于复杂，书写不方便
* XML 格式有保留符号，写SQL的时候也不方便，如常用的< 符号 必须转义

目前SQL文件格式非常简单，仅仅是sqlId 和sql语句本身，如下

			文件一些说明，放在头部可有可无，如果有说明，可以是任意文字
			SQL标示
			===
			SQL语句 
				
			SQL标示2
			===
			SQL语句 2
	
所有SQL文件建议放到一个sql目录，sql目录有多个子目录，表示数据库类型，这是公共SQL语句放到sql目录下，特定数据库的sql语句放到各自自目录下
当程序获取SQL语句得时候，先会根据数据库找特定数据库下的sql语句，如果未找到，会寻找sql下的。如下代码

			SqlScript sql = SqlManager.getSql("user.update"); 
			
SqlManager 会根据当前使用的数据库，先找sql/mysql/user.md 文件，确认是否有update语句，如果没有，则会寻找sql/user.md 


丰富的数据模型支持
===
BeetlSql 适合各种类型的引用，对于大中小型应用，模型通常是Pojo，这样易于维护和与三方系统交互，对于特小型项目，往往不需要严格的模型，表示业务实体通常是Map/List 组合。SQL语句的输入可以是Pojo或者Map，SQL语句执行结果也可以映射到Pojo和Map。

			int result = sqlManager.update("user.update",user);
			List<User> list = sqlManager.select("user.select",user,User.class);
			
			Map paras = new HashMap();
			paras.put("age",11);
			User user = sqlManager.single("user.select",paras,User.class);
			//or 
			Map user = sqlManager.single("user.select",paras,Map.class);
		
SQL 模板基于Beetl实现，更容易写和调试，以及扩展	
===		
		
SQL语句可以动态生成，基于Beetl语言，这是因为

* beetl执行效率高效 ，因此对于基于模板的动态sql语句，采用beetl非常合适

* beetl 语法简单易用，可以通过半猜半式的方式实现，杜绝myBatis这样难懂难记得语法。BeetlSql学习曲线几乎没有

* 利用beetl可以定制定界符号，完全可以将sql模板定界符好定义为数据库sql注释符号，这样容易在数据库中测试，如下也是sql模板（定义定界符为"--" 和 "null",null是回车意思);

			selectByCond
			===
			select * form user where 1=1
			--if(age!=null)
			age=#age#
			--}
			


* beetl 错误提示非常友好，减少写SQL脚本编写维护时间
* beetl 能容易与本地类交互（直接访问Java类），能执行一些具体的业务逻辑 ，也可以直接在sql模板中写入模型常量，即使sql重构，也会提前解析报错
* beetl语句易于扩展，提供各种函数，比如分表逻辑函数，跨数据库的公共函数等


无需注解，自动生成大量内置SQL，轻易完成增删改查功能
===		

BeetlSql虽然不是一个O/R Mapping 工具，但能根据默认约定，生成大量常用SQl而几乎不需要注解（对于oralce，需要注解SeqId(name="seqName"))，BeetlSql 根据输入的class，自动能生成如下sql

* 根据主键查询:sqlManager.unique(User.class,1)

* 按照模板查询: 将实例变量作为模板查询，如果其变量的属性为空，则不计入查询条件，date类型不会计入条件:List<User> list = sqlManager.template(User.class,user);

* 根据主键更新 sqlManager.updateById(user);

* 根据模板更新  sqlManager.updateAll(User.class,user);

* 根据主键删除 sqlManager.deleteById(User.class,1);


* insert：sqlManager.insert(User.class,user).自动插入，如果没有主键Annotaion，则会寻找数据库找到主键，并认为是自增主键。总共有如下三种主键Annotation

			SeqId 用于oralce			
			AutoId,用于自增。这是主键默认设置
			AssignId，代码指定主键
		

支持跨数据库平台，开发者所需工作减少到最小
===
如前所述，BeetlSql 可以通过sql文件的管理和搜索来支持跨数据库开发，如前所述，先搜索特定数据库，然后再查找common。另外BeetlSql也提供了一些夸数据库解决方案
* DbStyle 描述了数据库特性，注入insert语句，翻页语句都通过其子类完成，用户无需操心
* 提供一些默认的函数扩展，代替各个数据库的函数，如时间和时间操作函数date等


具备Interceptor功能，可以调试，性能诊断SQL，以及扩展其他功能	
===

BeetlSql可以在执行sql前后执行一系列的Intercetor，从而有机会执行各种扩展和监控，这比已知的通过数据库连接池做Interceptor更加容易。如下Interceptor都是有可能的

*  监控sql执行较长时间语句，打印并收集（已完成）
*  对每一条sql语句执行后输出其sql和参数，也可以根据条件只输出特定sql集合的sql。便于用户调试（已完成）
*  对sql预计解析，汇总sql执行情况（未完成，需要集成第三方sql分析工具）
*  数据库分表分库逻辑

内置支持主从数据库，通过扩展，可以支持更复杂的分库分表逻辑
===

BeetlSql管理数据源，如果只提供一个数据源，则认为读写均操作此数据源，如果提供多个，则默认第一个为写库，其他为读库。用户在开发代码的时候，无需关心操作的是哪个数据库，因为调用sqlScrip 的 select相关api的时候，总是去读取从库，add/update/delete 的时候，总是读取主库。 

		sqlManager.insert(User.class,user) // 操作主库，如果只配置了一个数据源，则无所谓主从
		sqlManager.unique(id,User.class) //读取从库

当然，也可以根据自己具体逻辑来确定如果选择主从库，只需要扩展BeetlSql，这一切对开发者是透明的

开发者也可以通过在Sql 模板里完成分表逻辑而对使用者透明，如下sql语句

	  insert into 
		#text("log"+date())#
		values () ...
		
		注：text函数直接输出表达式到sql语句，而不是输出？。
		
log表示按照一定规则分表，table可以根据输入的时间去确定是哪个表

		select * from 
		#text("log"+log.date)#
		where 
		
		注：text函数直接输出表达式到sql语句，而不是输出？。

同样，根据输入条件决定去哪个表，或者查询所有表

		@ var tables = getLogTables();
		@ for(table in tables){
		select * from #text(table)# 
		@		if(!tableLP.isLast) print("union");
		@}		
		where name = #name#


Spring集成
===

	<bean id="sqlManager" class="org.beetl.sql.ext.SpringBeetlSql">
		<property name="cs" >
			<bean  class="org.beetl.sql.ext.SpringConnectionSource">
				<property name="master" ref="dataSource"></property>
			</bean>
		</property>
		<property name="dbStyle">
			<bean class="org.beetl.sql.core.db.MySqlStyle"> </bean>
		</property>
		<property name="sqlLoader">
			<bean class="org.beetl.sql.core.ClasspathLoader"> 
				<property name="sqlRoot" value="/sql"></property>
			</bean>
		</property>
		<property name="nc">
			<bean class="org.beetl.sql.core.HumpNameConversion">
			</bean>
		</property>
		<property name="interceptors">
			<list>
				<bean class="org.beetl.sql.ext.DebugInterceptor"></bean>
			</list>
		</property>
	</bean>
	
* cs: 指定ConnectionSource，可以用系统提供的DefaultConnectionSource，支持按照CRUD决定主从。例子里只有一个master库

* dbStyle: 数据库类型，目前只支持org.beetl.sql.core.db.MySqlStyle

* sqlLoader: sql语句加载来源

* nc:  命名转化，有驼峰的HumpNameConversion，有数据库下划线的UnderlinedNameConversion

* interceptors:DebugInterceptor 用来打印sql语句，参数和执行时间

注意：
任何使用了Transactional 注解的，将统一使用Master数据源，例外的是@Transactional(readOnly=true),这将让Beetsql选择从数据库。

JFinal集成
===

在configPlugin 里配置BeetlSql

	JFinalBeetlSql.init();
默认会采用c3p0 作为数据源，其配置来源于jfinal 配置，如果你自己提供数据源或者主从，可以如下
	
	JFinalBeetlSql.init(master,slaves);
	
由于使用了Beetlsql，因此你无需再配置 **数据库连接池插件，和ActiveRecordPlugin**,可以删除相关配置。

在controller里，可以通过JFinalBeetlSql.dao 方法获取到SQLManager

		SQLManager dao = JFinalBeetlSql.dao();
		BigBlog blog = getModel(BigBlog.class);		
		dao.insert(BigBlog.class, blog);

如果想控制事物，还需要注册Trans

		public void configInterceptor(Interceptors me) {
			me.addGlobalActionInterceptor(new Trans());
		}

然后业务方法使用

		@Before(Trans.class)
		public void doXXX(){....+
		
这样，方法执行完毕才会提交事物，任何RuntimeException将回滚，如果想手工控制回滚.也可以通过

	Trans.commit()
	Trans.rollback()
	
如果习惯了JFinal Record模式，建议用户创建一个BaseBean，封装SQLManager CRUD 方法即可。然后其他模型继承此BaseBean

	
