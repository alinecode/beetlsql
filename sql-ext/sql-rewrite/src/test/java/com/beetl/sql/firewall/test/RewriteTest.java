package com.beetl.sql.firewall.test;

import com.beetl.sql.rewrite.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RewriteTest {
	@Test
	public void testSelect() throws JSQLParserException {

		String sql = "select * from user u";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		SqlParserRewrite finder = build();
		List<String> tables = finder.getTableList(statement);
		String expected = "SELECT * FROM user u WHERE u.tenant_id = 1 AND u.is_delete = 0";
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

	@Test
	public void insert4TenantTable() throws JSQLParserException {
		TestTableConfig tableCheck1 = new TestTableConfig();
		String sql = "insert into user (name) values (?) ";
		Insert insert = (Insert) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		ColRewriteParam tenantRewrite = new ColRewriteParam("tenant_id", new ColValueProvider() {
			@Override
			public Object getCurrentValue() {
				return 1;
			}
		});

		SqlParserRewrite finder = new SqlParserRewrite(tableCheck1, Arrays.asList(tenantRewrite));
		List<String> tables = finder.getTableList(insert);
		System.out.println(insert);
		Assert.assertEquals("INSERT INTO user (name, tenant_id) VALUES (?, 1)",insert.toString());

		sql = "insert into user (name,id) values (?,?) ";
		insert = (Insert) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		finder.getTableList(insert);
		Assert.assertEquals("INSERT INTO user (name, id, tenant_id) VALUES (?, ?, 1)",insert.toString());
		System.out.println(insert);
	}

	@Test
	public void insertTable4TableRewrite() throws JSQLParserException {
		TestTableConfig tableCheck1 = new TestTableConfig();
		String sql = "insert into user (name,id) values (?,?) ";
		Insert insert = (Insert) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));

		SqlParserRewrite finder = buildTable();
		List<String> tables = finder.getTableList(insert);
		System.out.println(tables);
		System.out.println(insert);

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

	protected SqlParserRewrite buildTable(){
		TestTableConfig tableCheck1 = new TestTableConfig();
		TableRewriteParam tableTenantRewrite = new TableRewriteParam(Arrays.asList("user","dept"), new TableNameProvider() {
			@Override
			public String getTableName(String name) {
				return name+"_1";
			}
		});




		SqlParserRewrite finder = new SqlParserRewrite(tableCheck1, Collections.emptyList(),tableTenantRewrite);

		return finder;
	}

	@Test
	public void testTable() throws JSQLParserException {

		String sql = "select * from user u where  1=1";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		SqlParserRewrite finder = buildTable();
		List<String> tables = finder.getTableList(statement);
		String expected = "SELECT * FROM user_1 u WHERE 1 = 1";
		Assert.assertEquals(expected,statement.toString());

	}

	@Test
	public void testTable2() throws JSQLParserException {

		String sql = "select id,(select id from dept d where d=1) from user u where  1=1";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql,
			parser -> parser.withSquareBracketQuotation(true));
		SqlParserRewrite finder = buildTable();
		List<String> tables = finder.getTableList(statement);
		String expected = "SELECT id, (SELECT id FROM dept_1 d WHERE d = 1) FROM user_1 u WHERE 1 = 1";
		Assert.assertEquals(expected,statement.toString());

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
