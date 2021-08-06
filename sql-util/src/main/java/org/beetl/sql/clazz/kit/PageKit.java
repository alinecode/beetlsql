package org.beetl.sql.clazz.kit;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import javax.swing.plaf.nimbus.State;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sql分页辅助工具，最新版采用sqlParser
 * @author xiandafu
 * @since  3.5
 */
@Plugin
public  class PageKit {

	//TODO,考虑使用weakmap;
	static ConcurrentHashMap<String,String> cache = new ConcurrentHashMap<>();

    public  String getCountSql(String dbName,String selectSql) {
    	try{
			String countSql = cache.get(selectSql);
			if(countSql!=null){
				return countSql;
			}
			Statement statement = CCJSqlParserUtil.parse(selectSql, parser -> parser.withSquareBracketQuotation(true));
			if(!(statement instanceof  Select)){
				throw new BeetlSQLException(BeetlSQLException.PAGE_QUERY_ERROR,"需要Select语句 "+selectSql);
			}
			Select select = (Select)statement;

			PlainSelect plain = (PlainSelect) select.getSelectBody();
			plain.setOrderByElements(null);
			plain.setSelectItems(Arrays.asList(new CountAll()));
			if (plain.getGroupBy() != null) {
				countSql =  "SELECT COUNT(*) FROM ( " + plain.toString() + " ) a";
			}else{
				countSql =  plain.toString();
			}
			cache.put(selectSql,countSql);
			return  countSql;

		}catch ( JSQLParserException parserException){
    		throw new BeetlSQLException(BeetlSQLException.PARSE_JDBC_SQL,"解析sql错误 "+selectSql,parserException);
		}


    }
    
    public static void main(String[] args) throws JSQLParserException {
    	String sql = "SELECT [id],[dept_id],[dept_name],[name],[description],[sheet_id],[status],[share],[start_date],[end_date],[group_id],[group_name],[created_by],[created_date],[updated_by],[updated_date] FROM [dbo].[t_sheet_main] WHERE [status] IN (  ? , ?  ) AND [dept_id] = ? OR [status] = ? AND [dept_id] <> ? ORDER BY  [created_date]  DESC";
		PageKit pageKit = new PageKit();
		long start = System.currentTimeMillis();
		String countSql = null;
		for(int i=0;i<10000;i++){
			 countSql = pageKit.getCountSql("xxx",sql);
		}
		System.out.println(System.currentTimeMillis()-start);
		System.out.println(countSql);

    }
    public static  class CountAll  extends AllColumns {
    	public String toString(){
    		return "count(*)";
		}
	}

}
