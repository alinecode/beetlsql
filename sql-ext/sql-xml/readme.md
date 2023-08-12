高仿myabais的xml语法，用户也可以自己参考实现自定义的xml标签语法，自定义方法。

```java
XMLBeetlSQL xmlBeetlSQL = new XMLBeetlSQL();
xmlBeetlSQL.config(sqlManager);




```

sql文件以xml方式编写，比如user.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beetlsql>
	<sql id="testAll">
		<!-- 测试sql -->
		select * from sys_user
		<where>
			<if test="has(user) and user.name=='a'">
				and name='3'
			</if>

			<bind value="1+99" export="newValue"/>

			<isNotEmpty value="'1'">
				and name='5'
			</isNotEmpty>
			and name = #{newValue}
			and name in
			<foreach items="[1,2,3]" var="id,status" open="(" close=")" separator=",">
				#{id+status.index}
			</foreach>

			<include refid="commonWhere"/>
		</where>
	</sql>

	<sql id="commonWhere">
		and name='bdfdsf'
	</sql>

	<resultMap id="complexListMap">
		<result property="id" column="id"/>
		<collection property="listInfo" >
			<result property="name" column="name"/>
			<result property="age" column="age"/>
		</collection>
	</resultMap>

</beetlsql>


```