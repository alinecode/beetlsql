package com.beetl.sql.firewall.test;

import com.beetl.sql.tenant.ColRewriteParam;
import com.beetl.sql.tenant.ColValueProvider;
import com.beetl.sql.tenant.SqlParserRewrite;
import com.beetl.sql.tenant.TableConfig;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RewriteTest {
	@Test
	public void testSelect() throws JSQLParserException {

		String sql = "select * from user u where  1=1";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		SqlParserRewrite finder = build();
		List<String> tables = finder.getTableList(statement);
		String expected = "SELECT * FROM user u WHERE 1 = 1 AND u.tenant_id = 1 AND u.is_delete = 0";
		Assert.assertEquals(expected,statement.toString());

	}


	@Test
	public void testSelectJoin() throws JSQLParserException {

		String sql = "select * from user u left join  dept on u.deptId= dept.id where  1=1";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		SqlParserRewrite finder = build();
		List<String> tables = finder.getTableList(statement);
		String expected = "SELECT * FROM user u LEFT JOIN dept ON u.deptId = dept.id WHERE 1 = 1 AND u.tenant_id = 1 AND u.is_delete = 0";
		Assert.assertEquals(expected,statement.toString());

	}

	@Test
	public void testSubSelectJoin() throws JSQLParserException {

		String sql = "select * from user u left join  (select * from user ) u2  on u.deptId= u2.id where  1=1";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		SqlParserRewrite finder = build();
		List<String> tables = finder.getTableList(statement);
		System.out.println(statement);
		String expected = "SELECT * FROM user u LEFT JOIN (SELECT * FROM user WHERE user.tenant_id = 1 AND user.is_delete = 0) u2 ON u.deptId = u2.id WHERE 1 = 1 AND u.tenant_id = 1 AND u.is_delete = 0";
		Assert.assertEquals(expected,statement.toString());

	}


	@Test
	public void deleteSelect() throws JSQLParserException {

		String sql = "delete from user  where  id=1";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		SqlParserRewrite finder = build();
		List<String> tables = finder.getTableList(statement);
		String expected = "DELETE FROM user WHERE id = 1 AND user.tenant_id = 1 AND user.is_delete = 0";
		System.out.println(statement);
		Assert.assertEquals(expected,statement.toString());

	}

	@Test
	public void updateSelect() throws JSQLParserException {

		String sql = "update user set name='ac'    where  id=1";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		SqlParserRewrite finder = build();
		List<String> tables = finder.getTableList(statement);
		String expected = "UPDATE user SET name = 'ac' WHERE id = 1 AND user.tenant_id = 1 AND user.is_delete = 0";
		System.out.println(statement);
		Assert.assertEquals(expected,statement.toString());

	}

	@Test
	public void selectDataFilter() throws JSQLParserException {

		String sql = "select * from  user ";
		TestTableConfig tableCheck1 = new TestTableConfig();
		ColRewriteParam deptRewrite = new ColRewriteParam("dept_id", new ColValueProvider() {
			@Override
			public Object getCurrentValue() {
				return Arrays.asList(1,2,3);
			}
		});

		SqlParserRewrite finder = new SqlParserRewrite(tableCheck1, Arrays.asList(deptRewrite));

		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		String expected = "SELECT * FROM user WHERE user.dept_id IN (1, 2, 3)";
		finder.getTableList(statement);
		System.out.println(statement);
		Assert.assertEquals(expected,statement.toString());

	}

	protected SqlParserRewrite build(){
		TestTableConfig tableCheck1 = new TestTableConfig();
		ColRewriteParam tenantRewrite = new ColRewriteParam("tenant_id", new ColValueProvider() {
			@Override
			public Object getCurrentValue() {
				return 1;
			}
		});

		ColRewriteParam logicDeleteRewrite = new ColRewriteParam("is_delete", new ColValueProvider() {
			@Override
			public Object getCurrentValue() {
				return 0;
			}
		});

		SqlParserRewrite finder = new SqlParserRewrite(tableCheck1, Arrays.asList(tenantRewrite, logicDeleteRewrite));

		return finder;
	}


	public static class TestTableConfig implements TableConfig {
		@Override
		public boolean contain(String table, String col) {
			if(table.equals("user")){
				return col.equals("tenant_id") || col.equals("is_delete")||col.equals("dept_id");
			}

			return false;
		}
	}


}
