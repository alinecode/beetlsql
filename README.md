#Beetlsql

* 作者: 闲大赋,Gavin.King,Sue
* 开发时间:2015-07
* 当前版本 1.0
* 论坛 http://ibeetl.com

#beetlsql 特点

BeetSql是一个全功能DAO工具， 同时具有Hibernate 优点 & Mybatis优点功能，适用于承认以SQL为中心，同时又需求工具能自动能生成大量常用的SQL的应用。

* 无需注解，自动生成大量内置SQL，轻易完成增删改查功能
* 数据模型支持Pojo，也支持Map/List这种快速模型，也支持混合模型
* SQL 以更简洁的方式，Markdown方式集中管理，同时方便程序开发和数据库SQL调试。
* SQL 模板基于Beetl实现，更容易写和调试，以及扩展
* 简单支持关系映射而不引入复杂的OR Mapping概念和技术。
* 具备Interceptor功能，可以调试，性能诊断SQL，以及扩展其他功能
* 内置支持主从数据库，通过扩展，可以支持更复杂的分库分表逻辑
* 支持跨数据库平台，开发者所需工作减少到最小

# 5 分钟例子

##准备工作

为了快速尝试BeetlSQL，需要准备一个Mysql数据库，然后执行如下sql脚本

	DROP TABLE IF EXISTS `user`;
	CREATE TABLE `user` (
	  `id` int(11) NOT NULL,
	  `name` varchar(64) DEFAULT NULL,
	  `age` int(4) DEFAULT NULL,
	  `userName` varchar(64) DEFAULT NULL,
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;

编写一个Pojo类，与数据库表对应

	public class User {
		Integer id;
		String name;
		Integer age;
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	
		public Integer getId() {
			return id;
		}
	
		public void setId(Integer id) {
			this.id = id;
		}
	
		public Integer getAge() {
			return age;
		}
	
		public void setAge(Integer age) {
			this.age = age;
		}
	
	}

##代码例子

写一个java的Main方法，内容如下

	// 创建一个简单的ConnectionSource，只有一个master
	ConnectionSource source = ConnectionSourceHelper.simple(driver,url,userName,password);
	// 采用mysql 习俗
	DBStyle mysql = new MysqlStyle();
	// sql语句放在classpagth的/sql 目录下
	SQLLoader loader = new ClasspathLoader("/sql");	
	// 数据库命名跟java命名采用驼峰转化
	NameConversion nc = new  HumpNameConversion();
	// 最后，创建一个SQLManager
	SqlManager sqlManager = new SqlManager(source,mysql,loader); 
	
	//使用内置的生成的sql 新增用户
	User user = new User();
	user.setAge(19);
	user.setName("xiandafu");
	sqlManager.insert(user);
	//使用内置sql查询用户
	int id = 1;
	user = sqlManager.unque(User.class,id);
	
	//使用user.md 文件里的select语句，参考下一节
	User query = new User();
	query.setName("xiandafu");
	List<User> list = sqlManager.select("user.select",User.class,query)
 
  	

##SQL例子

为了能执行user.select,需要在classpath里建立一个user.md 文件，内容如下

	select
	===
	select * from user where 1=1
	@if(!isEmpty(age)){
	and age = #age#
	@}		    
	@if(!isEmpty(name)){
	and name = #name#
	@}
	

关于如何写sql模板，会稍后章节说明，如下是一些简单说明。

* @ 和回车符号是定界符号，可以在里面写beetl语句。

* "#" 是站位符号，生成sql语句得时候，将输出？，如果你想输出表达式值，需要用text函数，或者任何以db开头的函数，引擎则认为是直接输出文本。

* isEmpty是beetl的一个函数，用来判断变量是否为空或者是否不存在.	

sql模板采用beetl原因是因为beetl 语法类似js，且对模板渲染做了特定优化，相比于mybatis，更加容易掌握和功能强大。    
	
#BeetlSQL 说明

## 获得SQLManager

SQLManager 是系统的核心，他提供了所有的dao方法。获得SQLManager，可以直接构造SQLManager.并通过过单例获取如：

	ConnectionSource source = ConnectionSourceHelper.simple(driver,url,userName,password);	
	// 采用mysql 习俗
	DBStyle mysql = new MysqlStyle();
	// sql语句放在classpagth的/sql 目录下
	SQLLoader loader = new ClasspathLoader("/sql");	
	// 数据库命名跟java命名采用驼峰转化
	NameConversion nc = new  HumpNameConversion();
	// 最后，创建一个SQLManager
	SqlManager sqlManager = new SqlManager(source,mysql,loader); 

更常见的是，已经有了DataSource，创建ConnectionSource 可以采用如下代码

	ConnectionSource source = ConnectionSourceHelper.single(datasource);

如果是主从Datasource

	ConnectionSource source = ConnectionSourceHelper.getMasterSlave(master,slaves)
	


###Spring集成

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



	public class MyServiceImpl implements MyService {

		@Autowired
		SpringBeetlSql beetlsql ;

		@Override
		@Transactional()
		public int total(User user) {
			
			SQLManager dao = beetlsql.getSQLMananger();
			List<User> list = dao.all(User.class);
			int total = list .size();
			dao.deleteById(User.class, 3);
			User u =new User();
			u.id = 3;
			u.name="hello";
			u.age = 12;
			dao.insert(User.class, u);

			return total;

		}

	}

可以参考demo https://git.oschina.net/xiandafu/springbeetlsql

###JFinal集成


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

可以参考demo https://git.oschina.net/xiandafu/jfinal_beet_beetsql_btjson

## SQLManager API

### 查询API

**模板类查询（自动生成sql）**
* public <T> List<T> template(T t)  根据模板查询，返回所有符合这个模板的数据库
* public <T> List<T> template(T t,RowMapper mapper) 同上，mapper可以提供额外的映射，如处理一对多，一对一
* public <T> List<T> template(T t,int start,int size) 同上，可以翻页
* public <T> List<T> template(T t,RowMapper mapper,int start,int size) 翻页，并增加额外的映射
* public <T> long templateCount(T t) 获取符合条件的个数

**通过sqlid查询，sql语句在md文件里**

* public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras) 根据sqlid来查询，参数是个map
* public <T> List<T> select(String sqlId, Class<T> clazz, Object paras) 根据sqlid来查询，参数是个pojo
* public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras, int start, int size)， 增加翻页
* public <T> List<T> select(String sqlId, Class<T> clazz, Object paras, int start, int size) ，增加翻页
* public <T> T selectSingle(String id,Object paras, Class<T> target) 根据sqlid查询，将对应的唯一值映射成指定的taget对象,RowMapper mapper 也随着这些api提供,不在此列出了

* public <T> T selectSingle(String id,Map<String, Object> paras, Class<T> target)  同上，参数是map
* public Integer  intValue(String id,Object paras) 查询结果映射成Integer，输入是objct
* public Integer  intValue(String id,Map paras) 查询结果映射成Integer，输入是map，
其他还有 longValue，bigDecimalValue

### 更新API

**自动生成sql**

* public void insert(Class<?> clazz,Object paras)  插入paras到paras关联的表
* public void insert(Class<?> clazz,Object paras,KeyHolder holder)，插入paras到paras关联的表，如果需要主键，可以通过holder的getKey来获取
* public int updateById(Object obj) 根据主键更新，组件通过annotation表示，如果没有，则认为属性id是主键
* public int[] updateByIdBatch(List<?> list) 批量更新

**通过sqlid更新**

* public int update(String sqlId, Object obj) 根据sqlid更新
* public int update(String sqlId, Map<String, Object> paras) 根据sqlid更新，输出参数是map
* public int[] updateBatch(String sqlId,List<?> list) 批量更新
* public int[] updateBatch(String sqlId,Map<String, Object>[] maps) 批量更新，参数是个数组，元素类型是map

**直接执行sql语句**

* public <T> List<T> execute(String sql,Class<T> clazz, Object paras)
* public <T> List<T> execute(String sql,Class<T> clazz, Map paras)
* public int  executeUpdate(String sql,Object paras)  返回成功执行条数
* public int  executeUpdate(String sql,Map paras) 返回成功执行条数

**强制使用主或者从**

* public void useMaster(DBRunner f)  DBRunner里的beetlsql调用将使用主库
* public void useSlave(DBRunner f) DBRunner里的beetlsql调用将使用从库

## BeetlSQL Annotation

对于自动生成的sql，默认不需要任何annotaton，类名对应于表名（通过NameConverstion类），getter方法的属性名对应于列明（也是通过NameConverstion类），但有些情况还是需要anntation。

*   标签@Table(name="xxxx")  告诉beetlsql，此类对应xxxx表。比如数据库有User表，User类对应于User表，也可以创建一个UserQuery对象，也对应于User表
	
	@Table(name="user")
	public class QueryUser ..

* @AutoID,作用于getter方法，告诉beetlsql，这是自增主键
* @AssignID，作用于getter方法，告诉beetlsql，这是主键，且由代码设定主键
* @SeqID(name="xx_seq"，作用于getter方法，告诉beetlsql，这是序列主键。

对于属性名为id的自增主键，不需要加annotation，beetlsql默认就是@AutoID

（注，如果想要获取自增主键或者序列主键，需要在SQLManager.insert中传入一个KeyHolder)


## BeetlSQL 数据模型

BeetlSQL是一个全功能DAO工具，支持的模型也很全面，包括

* Pojo, 也就是面向对象Java Object

* Map/List, 对于一些敏捷开发，可以直接使用Map/List 作为输入输出参数

* 混合模型，推荐使用混合模型。兼具灵活性和更好的维护性。Pojo可以实现Tail（尾巴的意思)，或者继承TailBean，这样查询出的ResultSet 除了按照pojo进行映射外，无法映射的值将按照列表/值保存。如下一个混合模型:

	/*混合模型*/
	public User extends TailBean{
		private int id ;
		pirvate String name;
		private int roleId;
		/*以下是getter和setter 方法*/
	}

对于sql语句:

	selectUser
	===
	select u.*,r.name r_name from user u left join role r on u.roleId=r.id .....

执行查询的时候

	List<User> list = sqlManager.select("user.selectUser",User.class,paras);
	for(User user:list){
		System.out.println(user.getId());
		System.out.println(user.get("rName"));

	}


程序可以通过get方法获取到未被映射到pojo的值，也可以在模板里直接 ${user.rName}  显示（对于大多数模板引擎都支持）



##Markdown方式管理
---
BeetlSQL集中管理SQL语句，SQL 可以按照业务逻辑放到一个文件里，如User对象放到user.md 里，文件可以按照模块逻辑放到一个目录下。文件格式抛弃了XML格式，采用了Markdown，原因是

* XML格式过于复杂，书写不方便
* XML 格式有保留符号，写SQL的时候也不方便，如常用的< 符号 必须转义
* MD 格式本身就是一个文档格式，也容易通过浏览器阅读和维护

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

			List<User> list = sqlManager.select("user.select",User.class); 
			
SqlManager 会根据当前使用的数据库，先找sql/mysql/user.md 文件，确认是否有select语句，如果没有，则会寻找sql/user.md 

(注:默认的ClasspathLoader采用了这种方法，你可以实现SQLLoader来实现自己的格式和sql存储方式，如数据库存储)

## 开发模式和产品模式

beetlsql默认是开发模式，因此修改md的sql文件，不需要重启。但建议线上不要使用开发模式，因为此模式会每次sql调用都会检测md文件是否变化。可以通过修改/btsql-ext.properties ,修改如下属性改为产品模式

	PRODUCT_MODE = true


		
##SQL 模板基于Beetl实现，更容易写和调试，以及扩展	
		
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

如果不了解beetl，可先自己尝试按照js语法来写sql模板，如果还有疑问，可以查阅官网 http://ibeetl.com

## Beetl 入门
Beetl 语法类似js，java，如下做简要说明，使用可以参考http://ibeetl.com，或者在线体验http://ibeetl.com:8080/beetlonline/

### 定界符号
默认的定界符号是@ 和 回车。 里面可以放控制语句，表达式等语，，站位符号是##,站位符号默认是输出？，并在执行sql的传入对应的值。如果想在占位符号输出变量值，则需要使用text函数

	@if(isEmpty(name)){
		and name = #name#
	}

如果想修改定界符，可以增加一个/btsql-ext.properties. 设置如下属性

	DELIMITER_PLACEHOLDER_START=#
	DELIMITER_PLACEHOLDER_END=#
	DELIMITER_STATEMENT_START=@
	DELIMITER_STATEMENT_END=

beetlsql 的其他属性也可以在此文件里设置

### 变量

通过程序传入的变量叫全局变量，可以在sql模板里使用，也可以定义变量，如

	@var count = 3;
	@var status = {"a":1}  //json变量

### 算数表达式

同js，如a+1-b%30, i++ 等

	select * from user where name like #'%'+name+'%'#

### 逻辑表达式

有“&&” “||” ，还有 “！”，分别表示与，或，非， beetl也支持三元表达式

	#user.gender=1?'女':'男'#

### 控制语句

* if else 这个同java，c，js。
* for,循环语句，如for(id:ids){}
	
	select * from user where status in (
	@for(id in ids){
	#id# #idLP.isLast?"":","#
	@}
注意：变量名＋LP 是一个内置变量，包含了循环状态，具体请参考beetl文档

	
* while 循环语句 ，如while(i<count)

### 访问变量属性

* 如果是对象，直接访问属性名，user.name
* 如果是Map，用key访问 map["key"];
* 如果是数组或者list，用索引访问，如list[1],list[i];
* 可以直采用java方式访问变量的方法和属性，如静态类Constatns
	
	public class Constatns{
		public static int 	RUNNING = 0;
		public static User getUser(){}
	}

直接以java方式访问，需要再变量符号前加上@，可以在模板里访问
	
	select * from user where status = #@Constatns.RUNNING# and id = #@Constatns.getUser().getId()#

	注意，如果Constants 类 没有导入进beetl，则需要带包名，导入beetl方法是配置IMPORT_PACKAGE=包名.;包名.

###判断对象非空

可以采用isEmpty判断变量表达式是否为空(为null)，是否存在，如果是字符串，是否是空字符串，如

	if(isEmpty(user)||isEmpty(role.name))

也可以用传统方法判断，如
	
	if(user==null) or if(role.name!=null)

变量有可能不存在，则需要使用安全输出符号，如

	if(null==user.name!)

变量表达式后面跟上"!" 表示如果变量不存在，则为！后面的值，如果！后面没有值，则为null

	
### 调用方法

同js，唯一值得注意的是，在占位符里调用text方法，会直接输出变量而不是“？”，其他以db开头的方式也是这样。架构师可以设置SQLPlaceholderST.textFunList.add(xxxx) 来决定那些方法在占位符号里可以直接输出文本而不是符号"?"

beetl提供了很多内置方法，如print，debug,isEmpty,date等，具体请参考文档

### 自定义方法

通过配置btsql-ext.properties, 可以注册自己定义的方法在beetlsql里使用，如注册一个返回当前年份的函数，可以在btsql-ext.properties加如下代码

	FN.db.year= com.xxx.YearFunction

这样在模板里,可以调用db.year() 获得当前年份。YearFunction 需要实现Function的 call方法，如下是个简单代码

	public class YearFunction implements Function{
		public String call(Object[] paras, Context ctx){
			return "2015";
		}
	}

关于如何完成自定义方法，请参考 ibeetl 官方文档


## Debug功能

Debug 期望能在控制台或者日志系统输出执行的sql语句，参数，执行结果以及执行时间，可以采用系统内置的DebugInterceptor 来完成，在构造SQLManager的时候，传入即可

	SqlManager sqlManager = new SqlManager(source,mysql,loader,nc ,new Interceptor[]{new DebugInterceptor() });

或者通过spring，jfianl这样框架配置完成。使用后，执行beetlsql，会有类似输出

	======DebugInterceptor Before======
	sqlId :user.updatexxx
	sql ： insert into user (id,name,age) values (?,?,?)
	paras : [4, old, null]
	======DebugInterceptor After======
	sqlId : user.updatexxx
	execution time : 54ms
	成功更新[1]
	
	
beetlsql会分别输出 执行前的sql和参数，以及执行后的结果和耗费的时间。你可以参考DebugInterceptor 实现自己的调试输出

##Interceptor功能


BeetlSql可以在执行sql前后执行一系列的Intercetor，从而有机会执行各种扩展和监控，这比已知的通过数据库连接池做Interceptor更加容易。如下Interceptor都是有可能的

*  监控sql执行较长时间语句，打印并收集。TimeStatInterceptor 类完成
*  对每一条sql语句执行后输出其sql和参数，也可以根据条件只输出特定sql集合的sql。便于用户调试。DebugInterceptor完成
*  对sql预计解析，汇总sql执行情况（未完成，需要集成第三方sql分析工具）

你也可以自行扩展Interceptor类，来完成特定需求。
如下，在执行数据库操作前会执行befor，通过ctx可以获取执行的上下文参数，数据库成功执行后，会执行after方法

	public interface Interceptor {
		public void before(InterceptorContext ctx);
		public void after(InterceptorContext ctx);
	}

InterceptorContext 如下，包含了sqlId，实际得sql，和实际得参数, 也包括执行结果result。对于查询，执行结果是查询返回的结果集条数，对于更新，返回的是成功条数，如果是批量更新，则是一个数组。可以参考源码DebugInterceptor

	public class InterceptorContext {
		private String sqlId;
		private String sql;
		private  List<Object> paras;
		private boolean isUpdate = false ;
		private Object result ;
		private Map<String,Object> env  = null;
		
	}



##内置支持主从数据库

BeetlSql管理数据源，如果只提供一个数据源，则认为读写均操作此数据源，如果提供多个，则默认第一个为写库，其他为读库。用户在开发代码的时候，无需关心操作的是哪个数据库，因为调用sqlScrip 的 select相关api的时候，总是去读取从库，add/update/delete 的时候，总是读取主库。 

		sqlManager.insert(User.class,user) // 操作主库，如果只配置了一个数据源，则无所谓主从
		sqlManager.unique(id,User.class) //读取从库


主从库的逻辑是由ConnectionSource来决定的，如下DefaultConnectionSource 的逻辑

	@Override
	public Connection getConn(String sqlId,boolean isUpdate,String sql,List<?> paras){
		if(this.slaves==null||this.slaves.length==0) return this.getWriteConn(sqlId,sql,paras);		
		if(isUpdate) return this.getWriteConn(sqlId,sql,paras);
		int status  = forceStatus.get();
		if(status ==0||status==1){
			return this.getReadConn(sqlId, sql, paras);
		}else{
			return this.getWriteConn(sqlId,sql,paras);
		}
	}


* forceStatus 可以强制SQLManager 使用主或者从数据库。参考api SQLManager.useMaster(DBRunner f)  ，SQLManager.useSlave(DBRunner f)  


对于于不同的ConnectionSource 完成逻辑不一样，对于spring，jfinal这样的框架，如果sqlManager在事务环境里，总是操作主数据库，如果是只读事务环境
则操作从数据库。如果没有事务环境，则根据sql是查询还是更新来决定。


如下是SpringConnectionSource 提供的主从逻辑

	public Connection getConn(String sqlId,boolean isUpdate,String sql,List paras){
		//只有一个数据源
		if(this.slaves==null||this.slaves.length==0) return this.getWriteConn(sqlId,sql,paras);
		//如果是更新语句，也得走master
		if(isUpdate) return this.getWriteConn(sqlId,sql,paras);
		//如果api强制使用
		int status  = forceStatus.get();
		if(status==1){
			return this.getReadConn(sqlId, sql, paras);
		}else if(status ==2){
			return this.getWriteConn(sqlId,sql,paras);
		}
		//在事物里都用master，除了readonly事物
		boolean inTrans = TransactionSynchronizationManager.isActualTransactionActive();
		if(inTrans){
			boolean  isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
			if(!isReadOnly){
				return this.getWriteConn(sqlId,sql,paras);
			}
		}
		 return this.getReadConn(sqlId, sql, paras);
	}


注意，对于使用者来说，无需关心本节说的内容，仅仅供要定制主从逻辑的架构师。

## 可以支持更复杂的分库分表逻辑

开发者也可以通过在Sql 模板里完成分表逻辑而对使用者透明，如下sql语句

	  insert into 
		#text("log_"+ getMonth(date())#
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


##跨数据库平台

如前所述，BeetlSql 可以通过sql文件的管理和搜索来支持跨数据库开发，如前所述，先搜索特定数据库，然后再查找common。另外BeetlSql也提供了一些夸数据库解决方案

* DbStyle 描述了数据库特性，注入insert语句，翻页语句都通过其子类完成，用户无需操心
* 提供一些默认的函数扩展，代替各个数据库的函数，如时间和时间操作函数date等





## 开发人员帅照

###闲大赋
![xiandfu](http://ibeetl.com/guide/xiandafu.jpg)

###Gavin·King 
![Gavin](http://ibeetl.com/guide/GV2.png)

### Sue
![Sue](http://ibeetl.com/guide/SUE.jpg)





