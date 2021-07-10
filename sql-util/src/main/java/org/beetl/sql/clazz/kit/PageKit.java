package org.beetl.sql.clazz.kit;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.statement.select.*;

import java.io.StringReader;
import java.util.Arrays;

/**
 * sql分页辅助工具，最新版采用sqlParser
 * @author xiandafu
 * @since  3.5
 */
@Plugin
public  class PageKit {



    public  String getCountSql(String dbName,String selectSql) {
    	try{
			CCJSqlParserManager parserManager = new CCJSqlParserManager();
			Select select = (Select) parserManager.parse(new StringReader(selectSql));
			PlainSelect plain = (PlainSelect) select.getSelectBody();
			plain.setOrderByElements(null);
			plain.setSelectItems(Arrays.asList(new CountAll()));
			return plain.toString();
		}catch ( JSQLParserException parserException){
    		throw new BeetlSQLException(BeetlSQLException.PARSE_JDBC_SQL,"解析sql错误 "+selectSql,parserException);
		}


    }
    
    public static void main(String[] args) throws JSQLParserException {
    	String sql = " SELECT \n" + "\t\t\t\ta.visit_log_id,\n" + "\t\t\t\ta.openid,\n" + "\t\t\t\ta.unionid,\n"
				+ "\t\t\t\ta.goods_def_id,\n" + "\t\t\t\ta.create_time,\n" + "\t\t\t\tb.nickname\n" + "\t\t\tFROM\n"
				+ "\t\t\t\tvisit_log a\n"
				+ "\t\t\tLEFT JOIN we_chat_user_info b ON (a.unionid = b.unionid or a.openid = b.openid)\n"
				+ "\t\t\tWHERE\n" + "\t\t\t\tdatediff(NOW(), a.create_time) <= 14\n" + "\t\t\tAND a.goods_def_id = 1\n"
				+ "\t\t\tgroup by a.unionid\n" + "\t\t\tORDER BY\n" + "\t\t\t\ta.create_time DESC";
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Select select = (Select) parserManager.parse(new StringReader(sql));
		PlainSelect plain = (PlainSelect) select.getSelectBody();
		plain.setOrderByElements(null);
		plain.setSelectItems(Arrays.asList(new CountAll()));
		System.out.println(select.toString());
    }
    public static  class CountAll  extends AllColumns {
    	public String toString(){
    		return "count(*)";
		}
	}

}
