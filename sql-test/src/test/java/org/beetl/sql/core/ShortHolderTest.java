package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.entity.User;
import org.junit.Test;

public class ShortHolderTest extends BaseTest {
	@Test
	public void testAnd(){
		String sqlTemplate = "select * from user where 1=1 #{id,and}";
		SqlId sqlId = sqlManager.sqlIdFactory.buildTemplate(sqlTemplate);

		SQLSource source = sqlManager.getSqlLoader().queryAutoSQL(sqlId);
		if (source == null) {
			source = new SQLSource(sqlId, sqlTemplate);
			source.setSqlType(SQLType.SELECT);
			this.sqlManager.getSqlLoader().addSQL(sqlId, source);
		}
		User user = new User();
		user.setId(1);
		SQLResult sqlResult = sqlManager.getSQLResult(source,user);
		System.out.println(sqlResult.jdbcSql);
		System.out.println(sqlResult.jdbcPara);
	}
}
